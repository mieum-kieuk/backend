package archivegarden.shop.service.payment;

import archivegarden.shop.controller.user.point.PointService;
import archivegarden.shop.dto.delivery.AddDeliveryForm;
import archivegarden.shop.dto.user.payment.CustomDataDTO;
import archivegarden.shop.dto.user.payment.PortonePaymentResultDTO;
import archivegarden.shop.dto.user.payment.WebhookRequest;
import archivegarden.shop.entity.*;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.DeliveryRepository;
import archivegarden.shop.repository.member.MemberRepository;
import archivegarden.shop.repository.order.OrderRepository;
import archivegarden.shop.repository.payment.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PointService pointService;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Value("${portone.store-id}")
    private String storeId;

    @Value("${portone.api-secret}")
    private String apiSecret;

    /**
     * STEP5: 공통처리
     * 1. access token 발급
     * 2. 결제 단건 조회
     * 3. 결제 데이터 생성
     * 4. 주문 요청과 실제 결제 금액이 같은지 비교
     *
     * @throws EntityNotFoundException
     */
    public String doResult(WebhookRequest webhook) {
        String paymentId = webhook.getData().getPaymentId();
        String transactionId = webhook.getData().getTransactionId();

        try {
            if (paymentId == null) return "결제실패";

            String accessToken = getAccessToken();
            Response response = getPayment(accessToken, paymentId);

            if (!response.isSuccessful()) {
                log.error("결제 조회가 실패 경우");
                return "결제실패";
            }

            String body = response.body().string();
            PortonePaymentResultDTO result = objectMapper.readValue(body, PortonePaymentResultDTO.class);
            String payStatus = result.getStatus();
            CustomDataDTO custom = objectMapper.readValue(result.getCustomData(), CustomDataDTO.class);

            Long orderId = custom.getOrderId();
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));

            Address address = new Address(custom.getZipCode(), custom.getAddressLine1(), custom.getAddressLine2());
            order.setRecipientInfo(custom.getRecipientName(), address, custom.getPhonenumber(), custom.getDeliveryRequestMsg());

            LocalDateTime paidAt = null;
            LocalDateTime cancelledAt = null;
            LocalDateTime failedAt = null;

            if ("PAID".equals(payStatus)) {
                paidAt = result.getPaidAt().toLocalDateTime();
            } else if ("CANCELLED".equals(payStatus)) {
                cancelledAt = result.getCancelledAt().toLocalDateTime();
                paymentRepository.findByMerchantUid(paymentId).updateStatus(payStatus, cancelledAt);
                return "CANCELLED";
            } else if ("FAILED".equals(payStatus)) {
                failedAt = result.getFailedAt().toLocalDateTime();
            } else if ("READY".equals(payStatus)) {
                return "READY";
            }

            Payment payment = Payment.builder()
                    .order(order)
                    .amount(result.getAmount().getTotal())
                    .pgProvider(result.getChannel().getPgProvider())
                    .buyerEmail(result.getCustomer().getEmail())
                    .cardName(result.getMethod().getCard().getName())
                    .cardQuota(result.getMethod().getInstallment().getMonth().longValue())
                    .currency(result.getCurrency())
                    .impUid(transactionId)
                    .merchantUid(paymentId)
                    .payMethod(result.getMethod().getType().replace("PaymentMethod", ""))
                    .status(payStatus)
                    .paidAt(paidAt)
                    .failedAt(failedAt)
                    .build();

            paymentRepository.save(payment);
            order.setPayment(payment);

            System.out.println(order.getAmount());
            System.out.println(result.getAmount().getTotal());
            if (order.getAmount().equals(result.getAmount().getTotal().intValue())) {
                switch (payStatus) {
                    case "PAID" -> order.updateStatus(OrderStatus.SUCCESS, "결제 성공");
                    case "FAILED" -> order.updateStatus(OrderStatus.FAIL, "주문 실패");
                    case "CANCELLED" -> order.updateStatus(OrderStatus.CANCEL, "결과 수신시 취소로 수신");
                }
            } else {
                log.warn("금액 위반조 결제 취소: expected={}, actual={}", order.getAmount(), result.getAmount().getTotal());
                cancelPayment(accessToken, paymentId);
                order.updateStatus(OrderStatus.FAIL, "금액 위반조 취소");
            }

            if ("PAID".equals(payStatus) && "new".equals(custom.getDeliveryOption())) {
                Member member = memberRepository.findByEmail(result.getCustomer().getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

                AddDeliveryForm form = new AddDeliveryForm(
                        custom.getDeliveryName(),
                        custom.getRecipientName(),
                        custom.getZipCode(),
                        custom.getAddressLine1(),
                        custom.getAddressLine2(),
                        custom.getPhonenumber(),
                        Boolean.TRUE.equals(custom.getIsDefaultDelivery())
                );
                Delivery delivery = Delivery.createDelivery(form, member);
                deliveryRepository.save(delivery);
            }

            // 적립금 참조도 가능
            if ("PAID".equals(payStatus) && custom.getUsePoints() != null && custom.getUsePoints() > 0) {
                Member member = memberRepository.findByEmail(result.getCustomer().getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
//                pointService.usePoints(member.getId(), paymentId, custom.getUsePoints());
            }

            return payStatus;
        } catch (IOException | ParseException e) {
            log.error("doResult 예외", e);
            return "결제실패 : 관리자에게 문의해 주세요.";
        }
    }

    /**
     * 결제 성공 여부 조회
     */
    public boolean isPaymentSuccess(String paymentId) {
        String orderStatus = orderRepository.findOrderStatusByMerchantUid(paymentId);
        return "SUCCESS".equals(orderStatus);
    }

    /**
     * PortOne API Secret을 사용해 access token을 발급받습니다.
     *
     * 유효기간: 하루
     *
     * @return 발급된 access token 문자열
     * @throws IOException 네트워크 오류 발생 시
     */
    private String getAccessToken() throws IOException, ParseException {
        JSONObject json = new JSONObject();
        json.put("apiSecret", apiSecret);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json.toJSONString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.portone.io/login/api-secret")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        String accessToken = "";
        if (response.isSuccessful()) {
            JSONParser parser = new JSONParser();
            JSONObject resultObj = (JSONObject) parser.parse(response.body().string());
            accessToken = (String) resultObj.get("accessToken");
        } else {
            //웹훅 결제조회 실패로 결제취소 처리하거나 콜백에서 처리할 수 있다.
            log.error("결제 조회를 위한 토큰 발급에 실패하였습니다.");
        }

        return accessToken;
    }

    /**
     * 아임포트
     * 결제 단건 조회
     */
    private Response getPayment(String accessToken, String paymentId) throws IOException {
        Request request = new Request.Builder()
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.portone.io/payments/" + paymentId + "?storeId=" + storeId)
                .build();

        OkHttpClient client = new OkHttpClient();
        return client.newCall(request).execute();
    }

    /**
     * 아임포트 결제 취소
     */
    private void cancelPayment(String accessToken, String paymentId) throws IOException {
        JSONObject json = new JSONObject();
        json.put("reason", "금액 위변조");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json.toJSONString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.portone.io/payments/" + paymentId + "/cancel")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).execute();
    }
}

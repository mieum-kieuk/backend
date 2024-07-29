package archivegarden.shop.service.payment;

import archivegarden.shop.dto.payment.Portone;
import archivegarden.shop.dto.payment.Webhook;
import archivegarden.shop.entity.Order;
import archivegarden.shop.entity.OrderStatus;
import archivegarden.shop.entity.Payment;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.order.OrderRepository;
import archivegarden.shop.repository.payment.PaymentRepository;
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
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${portone.storeId}")
    private String storeId;

    @Value("${portone.apiKey}")
    private String apiKey;

    @Value("${portone.apiSecret}")
    private String apiSecret;

    /**
     * STEP5: 공통처리
     * 1. access token 발급
     * 2. 결제 단건 조회
     * 3. 결제 데이터 생성
     * 4. 주문 요청과 실제 결제 금액이 같은지 비교
     */
    public String doResult(Webhook webhook) {

        String status = "";
        String paymentId = webhook.getData().getPaymentId();
        String transactionId = webhook.getData().getTransactionId();

        try {
            if (paymentId != null) {
                //1. 결제 조회를 위한 access_token 발급, 유효기간은 하루
                String accessToken = getAccessToken();

                //2. 결제 단건 조회
                Response response = getPayment(accessToken, paymentId);

                Portone payResultEntity = new Portone();

                LocalDateTime paidAt = null;
                LocalDateTime failedAt = null;
                LocalDateTime cancelledAt = null;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                if (response.isSuccessful()) {
                    //최종 결제결과
                    JSONParser parser = new JSONParser();
                    JSONObject resultObj = (JSONObject) parser.parse(response.body().string());
                    String payStatus = (String) resultObj.get("status");
                    payResultEntity.setOrderName((String) resultObj.get("orderName"));  //주문명
                    JSONObject amount = (JSONObject) resultObj.get("amount"); //결제 금액 세부 정보
                    payResultEntity.setAmount((Long) amount.get("total"));    //총 결제금액
                    payResultEntity.setVat((Long) amount.get("vat"));    //부가세액
                    payResultEntity.setCurrency((String) resultObj.get("currency"));   //통화 단위
                    JSONObject customer = (JSONObject) resultObj.get("customer");   //고객 정보
                    if (customer != null) {
                        payResultEntity.setBuyerName((String) customer.get("name"));    //이름
                        payResultEntity.setBuyerEmail((String) customer.get("email"));    //이메일
                        payResultEntity.setBuyerPhone((String) customer.get("phoneNumber"));    //휴대전화번호
                    }
                    JSONObject paymentMethodObj = (JSONObject) resultObj.get("method");
                    String type = (String) paymentMethodObj.get("type");
                    payResultEntity.setPayMethod(type.split("PaymentMethod")[1]); //결제수단 정보
                    payResultEntity.setCustomData((String) resultObj.get("customData")); //사용자 지정 데이터
                    JSONObject channel = (JSONObject) resultObj.get("channel"); //(결제, 본인인증 등에) 선택된 채널 정보
                    payResultEntity.setPgProvider((String) channel.get("pgProvider")); //PG사 결제 모듈

                    payResultEntity.setStatus(payStatus);   //결제 건 상태
                    if ("PAID".equals(payStatus)) { //결제 완료
                        payResultEntity.setPaidAt((String) resultObj.get("paidAt"));  //결제 완료 시점
                        JSONObject cardObj = (JSONObject) paymentMethodObj.get("card");  //카드 상세 정보
                        payResultEntity.setCardName((String) cardObj.get("name"));    //카드 상품명
                        payResultEntity.setApprovalNumber((String) paymentMethodObj.get("approval_number"));  //승인 번호
                        JSONObject installment = (JSONObject) paymentMethodObj.get("installment"); //할부 정보
                        payResultEntity.setCardQuota((Long) installment.get("month"));   //할부 개월 수
                        paidAt = LocalDateTime.parse(payResultEntity.getPaidAt().replace("T", " ").substring(0, 19), formatter).plusHours(9);
                    } else if("CANCELLED".equals(payStatus)) {  //결제 취소
                        cancelledAt = LocalDateTime.parse(resultObj.get("cancelledAt").toString().replace("T", " ").substring(0, 19), formatter);
                        Payment payment = paymentRepository.findByMerchantUid(paymentId);
                        payment.updateStatus(payResultEntity.getStatus(), cancelledAt);
                        return "CANCELLED";
                    }
                    else if ("FAILED".equals(payStatus)) {    //결제 실패
                        payResultEntity.setFailedAt((String) resultObj.get("failedAt"));
                        failedAt = LocalDateTime.parse(payResultEntity.getFailedAt().replace("T", " ").substring(0, 19), formatter);
                    } else if ("READY".equals(payStatus)) {  //결제 준비
                        return "READY";
                    }
                } else {
                    //웹훅 결제조회 실패로 결제 취소 처리하거나 콜백에서 처리할 수 있다.
                    log.error("결제 조회에 실패하였습니다.");
                }

                //custom_data에 주문테이블 pk를 실었다가 읽는다.
                String orderId = payResultEntity.getCustomData();
                Order order = orderRepository.findById(Long.parseLong(orderId)).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품입니다."));

                //결제데이터 생성
                Payment payment = Payment.builder()
                        .order(order)
                        .amount(payResultEntity.getAmount())
                        .pgProvider(payResultEntity.getPgProvider())
                        .approvalNumber(payResultEntity.getApprovalNumber())
                        .buyerEmail(payResultEntity.getBuyerEmail())
                        .cardName(payResultEntity.getCardName())
                        .cardQuota(payResultEntity.getCardQuota())
                        .currency(payResultEntity.getCurrency())
                        .impUid(transactionId)
                        .merchantUid(paymentId)
                        .payMethod(payResultEntity.getPayMethod())
                        .status(payResultEntity.getStatus())
                        .paidAt(paidAt)
                        .failedAt(failedAt)
                        .build();

                paymentRepository.save(payment);

                //주문요청 금액과 실 결제금액이 같은지 비교
                if (order.getAmount().toString().equals(payResultEntity.getAmount().toString())) {
                    if ("CANCELLED".equals(payResultEntity.getStatus())) {
                        //주문상태 변경
                        order.updateStatus(OrderStatus.CANCEL, "결과 수신시 취소로 수신");
                    } else if ("FAILED".equals(payResultEntity.getStatus())) {
                        //주문상태 변경
                        order.updateStatus(OrderStatus.FAIL, "주문 실패");
                    } else if ("PAID".equals(payResultEntity.getStatus())) {
                        //상품재고 감소 및 주문상태 변경
                        order.updateStatus(OrderStatus.SUCCESS, "결제 성공");
                    }
                } else {
                    //금액 위변조 취소처리
                    log.info("금액 위변조 취소처리: {} - {}", order.getAmount(), payResultEntity.getAmount());

                    //취소 API를 전송하여 취소를 진행합니다.
                    cancelPayment(accessToken, paymentId);

                    //주문상태 변경
                    order.updateStatus(OrderStatus.FAIL, "금액 위변조 취소처리");
                }

                status = payResultEntity.getStatus();
            } else {
                status = "결제실패";
            }
        } catch (IOException | ParseException e) {
            status = "결제실패 : 관리자에게 문의해 주세요.";
        }

        return status;
    }

    /**
     * 결제 성공 여부 조회
     */
    public boolean isPaymentSuccess(String paymentId) {
        String orderStatus = orderRepository.findOrderStatusByMerchantUid(paymentId);
        return "SUCCESS".equals(orderStatus);
    }

    /**
     * 아임포트
     * API secret을 사용한 토큰 발급
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
     * 아임포트
     * 결제 취소
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

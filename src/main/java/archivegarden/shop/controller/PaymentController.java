package archivegarden.shop.controller;

import archivegarden.shop.dto.payment.Portone;
import archivegarden.shop.dto.payment.Webhook;
import archivegarden.shop.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //콜백수신처리 - 콜백은 네트워크 상황에 따라 유실 될 수 있어 웹훅은 필수로 구현
    @PostMapping("/payment/callback")
    public ResponseEntity<?> callback(@RequestBody Portone portone) {
        //응답 header 생성
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
        JSONObject responseObj = new JSONObject();

        try {
            String txId = portone.getTxId();
            String paymentId = portone.getPaymentId();
            String code = portone.getCode();
            String message = portone.getMessage();

            log.info("---callback receive---");
            log.info("----------------------");
            log.info("txId: {}", txId);
            log.info("paymentId: {}", paymentId);
            log.info("code: {}", code);
            log.info("message: {}", message);

            boolean payment = paymentService.isPaymentSuccess(paymentId);

            String status = "fail";
            String fail_reason = "결제에 실패하였습니다.";
            if (payment) {
                status = "success";
                fail_reason = "결제에 성공하였습니다.";
            }

            responseObj.put("status", status);
            responseObj.put("fail_reason", fail_reason);

        } catch (Exception e) {
            e.printStackTrace();
            responseObj.put("status", "fail");
            responseObj.put("fail_reason", "관리자에게 문의해 주세요.");
        }

        return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }

    //웹훅 수신 처리
    @PostMapping("/payment/webhook")
    public ResponseEntity<?> webhook_receive(@RequestBody Webhook webhook) {
        //응답 header 생성
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
        JSONObject responseObj = new JSONObject();

        try {
            String type = webhook.getType();
            LocalDateTime timestamp = webhook.getTimestamp();
            Webhook.Data data = webhook.getData();

            log.info("---webhook receive---");
            log.info("----------------------");
            log.info("type: {}", type);
            log.info("timestamp: {}", timestamp);
            log.info("data: {}", data);

            paymentService.doResult(webhook);

        } catch (Exception e) {
            e.printStackTrace();
            responseObj.put("status", "결제실패: 관리자에게 문의해 주세요.");
        }

        return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }
}
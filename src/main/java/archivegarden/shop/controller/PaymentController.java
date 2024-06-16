package archivegarden.shop.controller;

import archivegarden.shop.dto.payment.Portone;
import archivegarden.shop.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${portone.storeId}")
    private String storeId;

    @Value("${portone.apiKey}")
    private String apiKey;

    //콜백수신처리 - 콜백은 네트워크 상황에 따라 유실 될 수 있어 웹훅은 필수로 구현
    @RequestMapping(value = "/payment/callback", method = RequestMethod.POST)
    public ResponseEntity<?> callback(@RequestBody Portone portone) {
        //응답 header 생성
        HttpHeaders responeHeaders = new HttpHeaders();
        responeHeaders.add("Content-Type", "application/json; charset=UTF-8");
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

            //웹훅 우선순위 설정에 따라 웹훅으로 DB결과를 반영하여 콜백은 DB의 결과를 조회하여 프론트로 전달한다.
            boolean payment = paymentService.findPayment(paymentId);
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


        return new ResponseEntity<String>(responseObj.toString(), responeHeaders, HttpStatus.OK);
    }
}
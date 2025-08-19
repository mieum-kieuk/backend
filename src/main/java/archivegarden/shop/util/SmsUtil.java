package archivegarden.shop.util;

import archivegarden.shop.exception.api.SmsGatewayApiException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse.MessageList;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.from}")
    private String from;

    private DefaultMessageService messageService;

    private static final String PROVIDER_SUCCESS = "2000";
    private static final String COOL_SMS_API_BASE_URL = "https://api.coolsms.co.kr";

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, COOL_SMS_API_BASE_URL);
    }

    /**
     * 회원가입시 인증 코드 발송
     * <p>
     * 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
     *
     * @param to               수신자 휴대전화번호
     * @param verificationCode 인증코드
     * @throws SmsGatewayApiException 문자 발송 중 오류가 발생한 경우
     */
    public void sendVerificationCode(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setText("[미음키읔] 인증번호 [" + verificationCode + "] 을 입력해 주세요.");

        try {
            MultipleDetailMessageSentResponse resp = messageService.send(message);

            if (resp == null || resp.getMessageList() == null || resp.getMessageList().isEmpty()) {
                throw new SmsGatewayApiException("SMS 서버 응답이 비어있습니다.", "EMPTY_MESSAGE_LIST", null, null);
            }

            MessageList messageResult = resp.getMessageList().get(0);

            String statusCode = messageResult.getStatusCode();
            String statusMessage = messageResult.getStatusMessage();

            if (!PROVIDER_SUCCESS.equals(statusCode)) {
                throw new SmsGatewayApiException("문자 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", statusCode, statusMessage, null);
            }

        } catch (NurigoMessageNotReceivedException e) {
            throw new SmsGatewayApiException("문자 발송 접수에 실패했습니다.", "MESSAGE_NOT_RECEIVED", e.getMessage(), e);
        } catch (NurigoEmptyResponseException e) {
            throw new SmsGatewayApiException("SMS 서버로부터 응답을 받지 못했습니다.", "EMPTY_RESPONSE", e.getMessage(), e);
        } catch (NurigoUnknownException e) {
            throw new SmsGatewayApiException("문자 발송 중 알 수 없는 오류가 발생했습니다.", "UNKNOWN_ERROR", e.getMessage(), e);
        } catch (Exception e) {
            throw new SmsGatewayApiException("문자 발송 처리 중 예기치 못한 오류가 발생했습니다.", null, e.getMessage(), e);
        }
    }
}

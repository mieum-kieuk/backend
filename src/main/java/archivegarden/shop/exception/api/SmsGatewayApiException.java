package archivegarden.shop.exception.api;

import lombok.Getter;

@Getter
public class SmsGatewayApiException extends RuntimeException {

    private final String providerCode;
    private final String providerMessage;

    public SmsGatewayApiException(String userMessage, String providerCode, String providerMessage, Throwable cause) {
        super(userMessage, cause);
        this.providerCode = providerCode;
        this.providerMessage = providerMessage;
    }
}


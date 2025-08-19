package archivegarden.shop.exception.api;

import lombok.Getter;

@Getter
public class EmailSendFailedApiException extends RuntimeException {

    private final String providerMessage;

    public EmailSendFailedApiException(String userMessage, String providerMessage, Throwable cause) {
        super(userMessage, cause);
        this.providerMessage = providerMessage;
    }
}

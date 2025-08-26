package archivegarden.shop.exception.api;

public class ConflictApiException extends RuntimeException {

    public ConflictApiException(String message) {
        super(message);
    }

    public ConflictApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

package archivegarden.shop.exception.api;

public class EntityNotFoundApiException extends RuntimeException {

    public EntityNotFoundApiException(String message) {
        super(message);
    }

    public EntityNotFoundApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

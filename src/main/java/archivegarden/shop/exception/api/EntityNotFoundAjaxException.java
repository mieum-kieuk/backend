package archivegarden.shop.exception.api;

public class EntityNotFoundAjaxException extends RuntimeException {

    public EntityNotFoundAjaxException(String message) {
        super(message);
    }

    public EntityNotFoundAjaxException(String message, Throwable cause) {
        super(message, cause);
    }
}

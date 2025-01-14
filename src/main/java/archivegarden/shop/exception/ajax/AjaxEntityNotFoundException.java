package archivegarden.shop.exception.ajax;

public class AjaxEntityNotFoundException extends RuntimeException {

    public AjaxEntityNotFoundException() {
    }

    public AjaxEntityNotFoundException(String message) {
        super(message);
    }

    public AjaxEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AjaxEntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public AjaxEntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

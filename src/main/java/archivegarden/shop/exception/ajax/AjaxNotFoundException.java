package archivegarden.shop.exception.ajax;

public class AjaxNotFoundException extends RuntimeException {

    public AjaxNotFoundException() {
    }

    public AjaxNotFoundException(String message) {
        super(message);
    }

    public AjaxNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AjaxNotFoundException(Throwable cause) {
        super(cause);
    }

    public AjaxNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

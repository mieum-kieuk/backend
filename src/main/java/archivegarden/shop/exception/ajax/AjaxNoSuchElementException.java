package archivegarden.shop.exception.ajax;

public class AjaxNoSuchElementException extends RuntimeException {

    public AjaxNoSuchElementException() {
    }

    public AjaxNoSuchElementException(String message) {
        super(message);
    }

    public AjaxNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public AjaxNoSuchElementException(Throwable cause) {
        super(cause);
    }

    public AjaxNoSuchElementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

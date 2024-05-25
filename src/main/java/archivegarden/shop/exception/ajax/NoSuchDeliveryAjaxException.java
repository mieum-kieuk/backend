package archivegarden.shop.exception.ajax;

public class NoSuchDeliveryAjaxException extends RuntimeException {

    public NoSuchDeliveryAjaxException() {
    }

    public NoSuchDeliveryAjaxException(String message) {
        super(message);
    }

    public NoSuchDeliveryAjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDeliveryAjaxException(Throwable cause) {
        super(cause);
    }

    public NoSuchDeliveryAjaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

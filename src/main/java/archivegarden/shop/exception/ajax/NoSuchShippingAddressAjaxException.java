package archivegarden.shop.exception.ajax;

public class NoSuchShippingAddressAjaxException extends RuntimeException {

    public NoSuchShippingAddressAjaxException() {
    }

    public NoSuchShippingAddressAjaxException(String message) {
        super(message);
    }

    public NoSuchShippingAddressAjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchShippingAddressAjaxException(Throwable cause) {
        super(cause);
    }

    public NoSuchShippingAddressAjaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

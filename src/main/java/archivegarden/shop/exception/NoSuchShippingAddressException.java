package archivegarden.shop.exception;

public class NoSuchShippingAddressException extends RuntimeException {

    public NoSuchShippingAddressException() {
    }

    public NoSuchShippingAddressException(String message) {
        super(message);
    }

    public NoSuchShippingAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchShippingAddressException(Throwable cause) {
        super(cause);
    }

    public NoSuchShippingAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

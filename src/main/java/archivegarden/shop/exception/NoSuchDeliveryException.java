package archivegarden.shop.exception;

public class NoSuchDeliveryException extends RuntimeException {

    public NoSuchDeliveryException() {
    }

    public NoSuchDeliveryException(String message) {
        super(message);
    }

    public NoSuchDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDeliveryException(Throwable cause) {
        super(cause);
    }

    public NoSuchDeliveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

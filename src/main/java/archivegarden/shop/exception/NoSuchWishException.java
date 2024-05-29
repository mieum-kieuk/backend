package archivegarden.shop.exception;

public class NoSuchWishException extends RuntimeException {

    public NoSuchWishException() {
    }

    public NoSuchWishException(String message) {
        super(message);
    }

    public NoSuchWishException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchWishException(Throwable cause) {
        super(cause);
    }

    public NoSuchWishException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

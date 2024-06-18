package archivegarden.shop.exception.admin;

public class AdminNotFoundException extends RuntimeException {

    public AdminNotFoundException() {
    }

    public AdminNotFoundException(String message) {
        super(message);
    }

    public AdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdminNotFoundException(Throwable cause) {
        super(cause);
    }

    public AdminNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

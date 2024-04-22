package archivegarden.shop.exception;

public class NoSuchBoardException extends RuntimeException {

    public NoSuchBoardException() {
    }

    public NoSuchBoardException(String message) {
        super(message);
    }

    public NoSuchBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBoardException(Throwable cause) {
        super(cause);
    }

    public NoSuchBoardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

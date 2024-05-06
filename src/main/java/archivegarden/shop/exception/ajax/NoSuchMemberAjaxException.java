package archivegarden.shop.exception.ajax;

public class NoSuchMemberAjaxException extends RuntimeException {

    public NoSuchMemberAjaxException() {
    }

    public NoSuchMemberAjaxException(String message) {
        super(message);
    }

    public NoSuchMemberAjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchMemberAjaxException(Throwable cause) {
        super(cause);
    }

    public NoSuchMemberAjaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

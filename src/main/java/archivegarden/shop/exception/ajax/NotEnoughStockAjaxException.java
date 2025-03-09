package archivegarden.shop.exception.ajax;

public class NotEnoughStockAjaxException extends RuntimeException {
    public NotEnoughStockAjaxException() {
    }

    public NotEnoughStockAjaxException(String message) {
        super(message);
    }

    public NotEnoughStockAjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockAjaxException(Throwable cause) {
        super(cause);
    }

    public NotEnoughStockAjaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

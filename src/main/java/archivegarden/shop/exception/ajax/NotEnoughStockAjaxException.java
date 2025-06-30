package archivegarden.shop.exception.ajax;

public class NotEnoughStockAjaxException extends RuntimeException {

    public NotEnoughStockAjaxException(String message) {
        super(message);
    }

    public NotEnoughStockAjaxException(String message, Throwable cause) {
        super(message, cause);
    }
}

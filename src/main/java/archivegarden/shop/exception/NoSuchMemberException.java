package archivegarden.shop.exception;

public class NoSuchMemberException extends RuntimeException {

    public NoSuchMemberException() {
    }

    public NoSuchMemberException(String message) {
        super(message);
    }
}

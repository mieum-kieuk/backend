package archivegarden.shop.security.exception;

import org.springframework.security.core.AuthenticationException;

public class IllegalLoginException extends AuthenticationException {

    public IllegalLoginException(String msg) {
        super(msg);
    }
}

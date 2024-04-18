package archivegarden.shop.controller;

import archivegarden.shop.exception.NoSuchDiscountException;
import archivegarden.shop.exception.NoSuchMemberException;
import archivegarden.shop.exception.NoSuchNoticeException;
import archivegarden.shop.exception.NoSuchProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NoSuchDiscountException.class)
    public String noSuchDiscountException(NoSuchDiscountException e) {
        return "error/no_such_discount_exception.html";
    }

    @ExceptionHandler(NoSuchProductException.class)
    public String noSuchProductException(NoSuchProductException e) {
        return "error/no_such_product_exception";
    }

    @ExceptionHandler(NoSuchMemberException.class)
    public String noSuchMemberException(NoSuchMemberException e) {
        return "error/no_such_member_exception";
    }

    @ExceptionHandler(NoSuchNoticeException.class)
    public String noSuchNoticeException(NoSuchNoticeException e) {
        return "error/no_such_notice_exception";
    }
}

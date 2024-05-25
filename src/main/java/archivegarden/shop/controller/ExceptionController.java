package archivegarden.shop.controller;

import archivegarden.shop.exception.*;
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

    @ExceptionHandler(NoSuchBoardException.class)
    public String noSuchNoticeException(NoSuchBoardException e) {
        return "error/no_such_board_exception";
    }

    @ExceptionHandler(NoSuchDeliveryException.class)
    public String noSuchDeliveryException(NoSuchDeliveryException e) {
        return "error/no_such_delivery_exception";
    }
}

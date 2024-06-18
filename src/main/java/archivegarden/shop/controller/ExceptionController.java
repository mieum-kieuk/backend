package archivegarden.shop.controller;

import archivegarden.shop.exception.admin.AdminNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

//    @ExceptionHandler(NoSuchElementException.class)
//    public String noSuchElementException(NoSuchElementException e) {
//        return "error/not_found_exception.html";
//    }

    @ExceptionHandler(AdminNotFoundException.class)
    public String adminNoSuchElementException(AdminNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "AdminNotFoundException", e.getMessage());
        return "error/admin/not_found_exception";
    }

//    @ExceptionHandler(NoSuchDiscountException.class)
//    public String noSuchDiscountException(NoSuchDiscountException e) {
//        return "error/no_such_discount_exception.html";
//    }
//
//    @ExceptionHandler(NoSuchProductException.class)
//    public String noSuchProductException(NoSuchProductException e) {
//        return "error/no_such_product_exception";
//    }
//
//    @ExceptionHandler(NoSuchMemberException.class)
//    public String noSuchMemberException(NoSuchMemberException e) {
//        return "error/no_such_member_exception";
//    }
//
//    @ExceptionHandler(NoSuchBoardException.class)
//    public String noSuchNoticeException(NoSuchBoardException e) {
//        return "error/no_such_board_exception";
//    }
//
//    @ExceptionHandler(NoSuchDeliveryException.class)
//    public String noSuchDeliveryException(NoSuchDeliveryException e) {
//        return "error/no_such_delivery_exception";
//    }
}

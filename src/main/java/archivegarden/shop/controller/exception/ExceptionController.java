package archivegarden.shop.controller.exception;

import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.ProductNotFoundException;
import archivegarden.shop.exception.common.DuplicateEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateEntityException.class)
    public String duplicateEntityException(DuplicateEntityException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "DuplicateEntityException", e.getMessage());
        return "error/common/entity_not_found.html";
    }

//    @ExceptionHandler(NotFoundException.class)
//    public String notFoundException(NotFoundException e) {
//        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "NotFoundException", e.getMessage());
//        return "error/not_found_exception.html";
//    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String productNotFoundException(ProductNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "ProductNotFoundException", e.getMessage());
        return "error/product_not_found_exception.html";
    }

//    @ExceptionHandler(AdminNotFoundException.class)
//    public String adminNoSuchElementException(AdminNotFoundException e) {
//        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "AdminNotFoundException", e.getMessage());
//        return "error/admin/not_found_exception";
//    }

//    @ExceptionHandler(NoSuchDiscountException.class)
//    public String noSuchDiscountException(NoSuchDiscountException e) {
//        return "error/no_such_discount_exception.html";
//    }

//    @ExceptionHandler(NoSuchBoardException.class)
//    public String noSuchNoticeException(NoSuchBoardException e) {
//        return "error/no_such_board_exception";
//    }
}

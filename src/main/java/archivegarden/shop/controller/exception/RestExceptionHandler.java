package archivegarden.shop.controller.exception;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AjaxNotFoundException.class)
    public ResultResponse ajaxNotFoundException(AjaxNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "AjaxNotFoundException", e.getMessage());
        return new ResultResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());

    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NoSuchElementException.class)
//    public ErrorResult noSuchElementException(NoSuchElementException e) {
//        return new ErrorResult("BAD_REQUEST", e.getMessage());
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NoSuchDiscountAjaxException.class)
//    public ErrorResult noSuchDiscountException(NoSuchDiscountAjaxException e) {
//        return new ErrorResult("BAD_REQUEST", "존재하지 않는 할인 혜택입니다. 다시 시도해 주세요.");
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NoSuchProductAjaxException.class)
//    public ErrorResult noSuchProductAjaxException(NoSuchProductAjaxException e) {
//        return new ErrorResult("BAD_REQUEST", "존재하지 않는 상품입니다. 다시 시도해 주세요.");
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NoSuchMemberAjaxException.class)
//    public ErrorResult noSuchMemberAjaxException(NoSuchMemberAjaxException e) {
//        return new ErrorResult("BAD_REQUEST", "존재하지 않는 회원입니다. 다시 시도해 주세요.");
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NoSuchImageAjaxException.class)
//    public ErrorResult noSuchImageAjaxException(NoSuchImageAjaxException e) {
//        return new ErrorResult("BAD_REQUEST", "존재하지 않는 첨부 파일입니다. 다시 시도해 주세요.");
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(NotEnoughStockAjaxException.class)
//    public ErrorResult notEnoughStockAjaxException(NotEnoughStockAjaxException e) {
//        return new ErrorResult("BAD_REQUEST", e.getMessage());
//    }
}

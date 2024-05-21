package archivegarden.shop.controller;

import archivegarden.shop.dto.ErrorResult;
import archivegarden.shop.exception.ajax.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchDiscountAjaxException.class)
    public ErrorResult noSuchDiscountException(NoSuchDiscountAjaxException e) {
        return new ErrorResult("BAD_REQUEST", "존재하지 않는 할인 혜택입니다. 다시 시도해 주세요.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchProductAjaxException.class)
    public ErrorResult noSuchProductAjaxException(NoSuchProductAjaxException e) {
        return new ErrorResult("BAD_REQUEST", "존재하지 않는 상품입니다. 다시 시도해 주세요.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchMemberAjaxException.class)
    public ErrorResult noSuchMemberAjaxException(NoSuchMemberAjaxException e) {
        return new ErrorResult("BAD_REQUEST", "존재하지 않는 회원입니다. 다시 시도해 주세요.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchImageAjaxException.class)
    public ErrorResult noSuchImageAjaxException(NoSuchImageAjaxException e) {
        return new ErrorResult("BAD_REQUEST", "존재하지 않는 첨부 파일입니다. 다시 시도해 주세요.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughStockAjaxException.class)
    public ErrorResult notEnoughStockAjaxException(NotEnoughStockAjaxException e) {
        return new ErrorResult("BAD_REQUEST", e.getMessage());
    }
}

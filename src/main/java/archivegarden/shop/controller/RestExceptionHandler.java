package archivegarden.shop.controller;

import archivegarden.shop.dto.ErrorResult;
import archivegarden.shop.exception.ajax.NoSuchDiscountAjaxException;
import archivegarden.shop.exception.ajax.NoSuchProductAjaxException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoSuchDiscountAjaxException.class, NoSuchProductAjaxException.class})
    public ErrorResult noSuchDiscountException(NoSuchDiscountAjaxException e) {
        return new ErrorResult("BAD_REQUEST", "삭제 도중 오류가 발생했습니다. 다시 시도해 주세요.");
    }
}

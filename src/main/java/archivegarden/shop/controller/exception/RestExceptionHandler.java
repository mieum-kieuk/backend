package archivegarden.shop.controller.exception;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AjaxEntityNotFoundException.class)
    public ResultResponse entityNotFoundException(AjaxEntityNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "AjaxEntityNotFoundException", e.getMessage());
        return new ResultResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughStockAjaxException.class)
    public ResultResponse notEnoughStockAjaxException(NotEnoughStockAjaxException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "NotEnoughStockAjaxException", e.getMessage());
        return new ResultResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}

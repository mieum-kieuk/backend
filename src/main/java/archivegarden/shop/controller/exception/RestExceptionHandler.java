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
}

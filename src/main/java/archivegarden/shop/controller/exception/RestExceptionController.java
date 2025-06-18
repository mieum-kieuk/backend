package archivegarden.shop.controller.exception;

import archivegarden.shop.dto.ResultResponse;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.ajax.NotEnoughStockAjaxException;
import archivegarden.shop.exception.global.EmailSendFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionController {

    @ExceptionHandler(EntityNotFoundAjaxException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultResponse handleEntityNotFoundAjaxException(EntityNotFoundAjaxException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "EntityNotFoundAjaxException", e.getMessage());
        return new ResultResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(NotEnoughStockAjaxException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse handleNotEnoughStockAjaxException(NotEnoughStockAjaxException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "NotEnoughStockAjaxException", e.getMessage());
        return new ResultResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(EmailSendFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultResponse handleEmailSendFailedException(EmailSendFailedException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "EmailSendFailedException", e.getMessage());
        return new ResultResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}

package archivegarden.shop.controller.exception;

import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.exception.common.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "EntityNotFoundException", e.getMessage());
        return "error/common/entity_not_found.html";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleFileUploadException(FileUploadException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "FileUploadException", e.getMessage());
        return "error/common/entity_not_found.html";
    }
}

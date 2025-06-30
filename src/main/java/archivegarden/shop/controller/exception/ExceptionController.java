package archivegarden.shop.controller.exception;

import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.exception.global.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

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

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "AccessDeniedException", e.getMessage());
        return "error/common/403.html";
    }
}

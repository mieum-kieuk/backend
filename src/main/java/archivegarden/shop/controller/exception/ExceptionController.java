package archivegarden.shop.controller.exception;

import archivegarden.shop.exception.common.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    public String entityNotFoundException(EntityNotFoundException e) {
        log.warn("[{}] cause={}, message={}", e.getStackTrace()[0], "EntityNotFoundException", e.getMessage());
        return "error/common/entity_not_found.html";
    }
}

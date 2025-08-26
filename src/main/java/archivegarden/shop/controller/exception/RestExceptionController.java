package archivegarden.shop.controller.exception;

import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.dto.common.ErrorResponseDto;
import archivegarden.shop.exception.api.*;
import archivegarden.shop.exception.global.EmailSendFailedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class RestExceptionController {

    @ExceptionHandler(EntityNotFoundApiException.class)
    public ResponseEntity<ApiResponseDto> handleEntityNotFoundApiException(EntityNotFoundApiException e, HttpServletRequest req) {
        log.warn("[EntityNotFoundApiException] uri={} user={} cause={}",
                req.getRequestURI(),
                req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(ConflictApiException.class)
    public ResponseEntity<ErrorResponseDto> handleConflictApiException(ConflictApiException e, HttpServletRequest req) {
        String user = req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous";

        log.info("[ConflictApiException] uri={} user={} code={} msg={}", req.getRequestURI(), user, "CONFLICT", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("CONFLICT", e.getMessage(), req.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest req) {
        log.warn("[DataIntegrityViolationException] uri={} cause={}", req.getRequestURI(), e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("CONFLICT", "중복된 데이터가 존재합니다.", req.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(SmsGatewayApiException.class)
    public ResponseEntity<ErrorResponseDto> handleSmsGatewayException(SmsGatewayApiException e, HttpServletRequest req) {
        log.error("[SmsGatewayApiException] uri={}, user={}, providerCode={}, providerMsg={}",
                req.getRequestURI(),
                req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous",
                e.getProviderCode(), e.getProviderMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("SMS_GATEWAY_ERROR", "문자 발송 중 오류가 발생했습니다. 다시 시도해 주세요.", req.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(EmailSendFailedApiException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailSendFailedException(EmailSendFailedApiException e, HttpServletRequest req) {
        log.error("[EmailSendFailedApiException] uri={}, user={}, providerMsg={}",
                req.getRequestURI(),
                req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous",
                e.getProviderMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("SMS_GATEWAY_ERROR", "메일 전송 중 오류가 발생했습니다. 다시 시도해 주세요.", req.getRequestURI(), Instant.now()));
    }

    @ExceptionHandler(NotEnoughStockAjaxException.class)
    public ResponseEntity<ApiResponseDto> handleNotEnoughStockAjaxException(NotEnoughStockAjaxException e, HttpServletRequest req) {
        log.warn("[NotEnoughStockAjaxException] uri={} user={} cause={}",
                req.getRequestURI(),
                req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(EmailSendFailedException.class)
    public ResponseEntity<ApiResponseDto> handleEmailSendFailedException(EmailSendFailedException e, HttpServletRequest req) {
        log.warn("[EmailSendFailedException] uri={} user={} cause={}",
                req.getRequestURI(),
                req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonymous",
                e.getMessage()
        );

        return ResponseEntity.internalServerError()
                .body(new ApiResponseDto("INTERNAL_SERVER_ERROR", e.getMessage()));
    }
}

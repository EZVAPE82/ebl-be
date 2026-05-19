package com.elfbarlounge.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 전역 예외 핸들러.
 * - 내부 정보(스택트레이스, SQL, 클래스명) 절대 응답 비노출 (vibe 함정 #2)
 * - 추적 ID(traceId)만 응답에 포함, 상세 로그는 서버에만
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException e, HttpServletRequest req) {
        String traceId = newTraceId();
        log.warn("[{}] ApiException {} {} - {} {}", traceId, e.getStatus(), e.getCode(), req.getMethod(), req.getRequestURI());
        return ResponseEntity.status(e.getStatus()).body(body(e.getCode(), e.getMessage(), traceId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String traceId = newTraceId();
        String summary = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[{}] Validation - {} {} - {}", traceId, req.getMethod(), req.getRequestURI(), summary);
        return ResponseEntity.badRequest().body(body("VALIDATION_FAILED", "입력값이 올바르지 않습니다.", traceId));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest req) {
        String traceId = newTraceId();
        log.warn("[{}] BadRequestBody - {} {}", traceId, req.getMethod(), req.getRequestURI());
        return ResponseEntity.badRequest().body(body("MALFORMED_REQUEST", "요청 본문 형식이 올바르지 않습니다.", traceId));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest req) {
        String traceId = newTraceId();
        log.warn("[{}] MissingParam {} - {} {}", traceId, e.getParameterName(), req.getMethod(), req.getRequestURI());
        return ResponseEntity.badRequest().body(body("PARAM_MISSING", "필수 파라미터가 누락되었습니다.", traceId));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        String traceId = newTraceId();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body("METHOD_NOT_ALLOWED", "허용되지 않은 메서드입니다.", traceId));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException e, HttpServletRequest req) {
        String traceId = newTraceId();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body("NOT_FOUND", "요청 경로를 찾을 수 없습니다.", traceId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnknown(Exception e, HttpServletRequest req) {
        String traceId = newTraceId();
        // 내부 상세는 서버 로그에만, 응답엔 traceId만 노출
        log.error("[{}] Unhandled - {} {}", traceId, req.getMethod(), req.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("INTERNAL_ERROR", "일시적인 오류가 발생했습니다.", traceId));
    }

    private Map<String, Object> body(String code, String message, String traceId) {
        return Map.of(
                "code", code,
                "message", message,
                "traceId", traceId,
                "timestamp", Instant.now().toString()
        );
    }

    private String newTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

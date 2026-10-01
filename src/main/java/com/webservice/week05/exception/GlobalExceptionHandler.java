package com.webservice.week05.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid 검증 실패 -> 400, 필드별 메시지 반환 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** JSON 형식 오류, 타입 불일치(rating에 문자열 등) -> 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "요청 본문(JSON)을 해석할 수 없습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

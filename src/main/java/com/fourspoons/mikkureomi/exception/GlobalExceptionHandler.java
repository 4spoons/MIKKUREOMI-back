package com.fourspoons.mikkureomi.exception;

import com.fourspoons.mikkureomi.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException — 서비스 로직에서 발생한 비즈니스 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorMessage error = e.getErrorMessage();

        HttpStatus status = switch (error) {
            case EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;        // 409
            case INVALID_PASSWORD -> HttpStatus.UNAUTHORIZED;        // 401
            case USER_NOT_FOUND, PROFILE_NOT_FOUND, MEAL_PICTURE_NOT_FOUND, MEAL_NOT_FOUND, DAILY_REPORT_NOT_FOUND -> HttpStatus.NOT_FOUND; // 404
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(error.getMessage()));
    }

    // @Valid 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : ErrorMessage.INVALID_INPUT_VALUE.getMessage();
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(message));
    }

    // JWT 관련 오류
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(ErrorMessage.JWT_INVALID_TOKEN.getMessage()));
    }

    // 인증은 되었지만 권한이 없는 경우
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(ErrorMessage.ACCESS_DENIED.getMessage()));
    }

    // 예상치 못한 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorMessage.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
package com.example.mansshop_boot.config.customException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.customException.exception.CustomTokenExpiredException;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomTokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredException(Exception e) {
//        log.warn("tokenExpiredException : {}", e.getMessage());

        return toResponseEntity(ErrorCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(Exception e) {
//        log.warn("AccessDeniedException : {}", e.getMessage());

        return toResponseEntity(ErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(CustomTokenStealingException.class)
    public ResponseEntity<?> tokenStealingException(Exception e) {
//        log.warn("TokenStealing : {}", e.getMessage());

        return toResponseEntity(ErrorCode.TOKEN_STEALING);
    }

    @ExceptionHandler(CustomBadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(Exception e) {
//        log.warn("BadCredentials Exception : {}", e.getMessage());

        return toResponseEntity(ErrorCode.BAD_CREDENTIALS);
    }

    private ResponseEntity<?> toResponseEntity(ErrorCode errorCode){

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(
                        ExceptionEntity.builder()
                                .errorCode(String.valueOf(errorCode.getHttpStatus()))
                                .errorMessage(errorCode.getMessage())
                                .build()
                );
    }
}

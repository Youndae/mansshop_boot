package com.example.mansshop_boot.config.customException;

import com.example.mansshop_boot.config.customException.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomTokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredException(Exception e) {
        log.warn("tokenExpiredException : {}", e.getMessage());

        return toResponseEntity(ErrorCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler({CustomAccessDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<?> accessDeniedException(Exception e) {
        log.warn("AccessDeniedException : {}", e.getMessage());

        return toResponseEntity(ErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(CustomTokenStealingException.class)
    public ResponseEntity<?> tokenStealingException(Exception e) {
        log.warn("TokenStealing : {}", e.getMessage());

        return toResponseEntity(ErrorCode.TOKEN_STEALING);
    }

    @ExceptionHandler({CustomBadCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<?> badCredentialsException(Exception e) {
        log.warn("BadCredentials Exception : {}", e.getMessage());

        return toResponseEntity(ErrorCode.BAD_CREDENTIALS);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<?> notFoundException(Exception e) {
        log.warn("NotFoundException : {}", e.getMessage());

        return toResponseEntity(ErrorCode.NOT_FOUND);
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


    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public void notFound(Exception e) {
        log.info("clientError not found");
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public void persisterNotFound(Exception e) {
        log.info("persister NotFound");
    }
}

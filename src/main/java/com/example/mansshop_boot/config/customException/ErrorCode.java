package com.example.mansshop_boot.config.customException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AccessDeniedException")
    , BAD_CREDENTIALS(HttpStatus.FORBIDDEN, "BadCredentialsException")
    , TOKEN_STEALING(HttpStatus.valueOf(800), "TokenStealingException")
    , TOKEN_EXPIRED(HttpStatus.valueOf(401), "TokenExpiredException");

    private final HttpStatus httpStatus;

    private final String message;
}

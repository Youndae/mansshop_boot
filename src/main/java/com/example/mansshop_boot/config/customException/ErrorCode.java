package com.example.mansshop_boot.config.customException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCESS_DENIED(403, "AccessDeniedException")
    , BAD_CREDENTIALS(403, "BadCredentialsException")
    , TOKEN_STEALING(800, "TokenStealingException")
    , TOKEN_EXPIRED(401, "TokenExpiredException")
    , NOT_FOUND(400, "NotFoundException");

    private final int httpStatus;

    private final String message;
}

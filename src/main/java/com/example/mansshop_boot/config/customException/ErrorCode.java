package com.example.mansshop_boot.config.customException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCESS_DENIED(403, "AccessDeniedException")
    , BAD_CREDENTIALS(403, "BadCredentialsException");

    private final int httpStatus;

    private final String message;
}

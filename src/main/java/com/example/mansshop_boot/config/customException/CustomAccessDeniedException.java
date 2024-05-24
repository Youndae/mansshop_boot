package com.example.mansshop_boot.config.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomAccessDeniedException extends RuntimeException{
    ErrorCode errorCode;

    String message;
}

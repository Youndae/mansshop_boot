package com.example.mansshop_boot.config.customException.exception;

import com.example.mansshop_boot.config.customException.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomOrderDataFailedException extends RuntimeException {

    ErrorCode errorCode;

    String message;
}

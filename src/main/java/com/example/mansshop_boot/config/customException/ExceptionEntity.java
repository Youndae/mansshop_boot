package com.example.mansshop_boot.config.customException;

import lombok.Builder;


public record ExceptionEntity(String errorCode, String errorMessage) {

    @Builder
    public ExceptionEntity {
    }
}

package com.example.mansshop_boot.config.customException;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public record ExceptionEntity(String errorCode, String errorMessage) {

    @Builder
    public ExceptionEntity {
    }
}

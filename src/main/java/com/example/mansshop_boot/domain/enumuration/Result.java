package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    TOKEN_STEALING("token Stealing")
    , TOKEN_EXPIRATION("token expiration")
    , WRONG_TOKEN("wrong token");

    private final String resultKey;
}

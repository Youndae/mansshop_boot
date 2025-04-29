package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    TOKEN_STEALING("token Stealing")
    , TOKEN_EXPIRATION("token expiration")
    , WRONG_TOKEN("wrong token")
    , OK("OK")
    , FAIL("FAIL")
    , ERROR("error")
    , NOTFOUND("not found");

    private final String resultKey;
}

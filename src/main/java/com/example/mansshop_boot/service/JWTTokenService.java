package com.example.mansshop_boot.service;


import com.example.mansshop_boot.domain.dto.token.TokenDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface JWTTokenService {

    void deleteCookieAndThrowException(HttpServletResponse response);

    void deleteTokenAndCookieAndThrowException(String tokenClaim, String ino, HttpServletResponse response);

    void tokenStealingExceptionResponse(HttpServletResponse response);

    void tokenExpirationResponse(HttpServletResponse response);

    String reIssueToken(TokenDTO tokenDTO, HttpServletResponse response);
}

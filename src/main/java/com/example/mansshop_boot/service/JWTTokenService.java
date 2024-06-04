package com.example.mansshop_boot.service;


import com.example.mansshop_boot.domain.dto.TokenDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface JWTTokenService {

    void deleteCookieAndThrowException(HttpServletResponse response);

    void deleteTokenAndCookieAndThrowException(String tokenClaim, String ino, HttpServletResponse response);

    void tokenStealingExceptionResponse(HttpServletResponse response);

    void tokenExpirationResponse(HttpServletResponse response);

    ResponseEntity<?> reIssueToken(TokenDTO tokenDTO, HttpServletResponse response);
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.TokenDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.enumuration.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JWTTokenServiceImpl implements JWTTokenService{

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public void deleteCookieAndThrowException(HttpServletResponse response) {
        jwtTokenProvider.deleteCookie(response);
        tokenStealingExceptionResponse(response);
    }

    @Override
    public void deleteTokenAndCookieAndThrowException(String tokenClaim, String ino, HttpServletResponse response) {
        jwtTokenProvider.deleteRedisDataAndCookie(tokenClaim, ino, response);
        tokenStealingExceptionResponse(response);
    }

    @Override
    public void tokenStealingExceptionResponse(HttpServletResponse response) {
        response.setStatus(ErrorCode.TOKEN_STEALING.getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }

    @Override
    public void tokenExpirationResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }

    @Override
    public String reIssueToken(TokenDTO tokenDTO, HttpServletResponse response) {

        log.info("reIssued :: dto : {}", tokenDTO);

        String decodeAccessToken = jwtTokenProvider.decodeToken(tokenDTO.accessTokenValue());

        log.info("reIssued :: accessDecode : {}", decodeAccessToken);

        if(decodeAccessToken.equals(Result.WRONG_TOKEN.getResultKey())){
            String decodeRefreshToken = jwtTokenProvider.decodeToken(tokenDTO.refreshTokenValue());

            if(decodeRefreshToken.equals(Result.WRONG_TOKEN.getResultKey())){
                jwtTokenProvider.deleteCookie(response);

                return Result.FAIL.getResultKey();
            }
        }else {
            String claimByRefreshToken = jwtTokenProvider.verifyRefreshToken(
                    tokenDTO.refreshTokenValue()
                    , tokenDTO.inoValue()
                    , decodeAccessToken
            );

            if(claimByRefreshToken.equals(Result.TOKEN_STEALING.getResultKey())
                    || claimByRefreshToken.equals(Result.WRONG_TOKEN.getResultKey())) {
                deleteCookieAndThrowException(response);
                return Result.FAIL.getResultKey();
            }else
                jwtTokenProvider.issueTokens(decodeAccessToken, tokenDTO.inoValue(), response);
        }

        log.info("reissue success");

        return Result.OK.getResultKey();
    }
}

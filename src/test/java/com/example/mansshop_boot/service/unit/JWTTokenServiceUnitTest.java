package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.token.TokenDTO;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.service.JWTTokenServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTTokenServiceUnitTest {

    @InjectMocks
    @Spy
    private JWTTokenServiceImpl jwtTokenService;

    @Mock
    private JWTTokenProvider tokenProvider;

    private final String USER_ID = "user";

    private final String INO = "INOVALUE";


    @Test
    @DisplayName(value = "토큰 쿠키 제거")
    void deleteCookieAndThrowException() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        jwtTokenService.deleteCookieAndThrowException(response);

        verify(tokenProvider).deleteCookie(response);
        verify(response).setStatus(ErrorCode.TOKEN_STEALING.getHttpStatus());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding("utf-8");
    }

    @Test
    @DisplayName(value = "토큰 쿠키 제거 및 Redis 데이터 제거")
    void deleteTokenAndCookieAndThrowException() {
        String tokenClaim = "user";
        String ino = "ino123";
        HttpServletResponse response = mock(HttpServletResponse.class);

        jwtTokenService.deleteTokenAndCookieAndThrowException(tokenClaim, ino, response);

        verify(tokenProvider).deleteRedisDataAndCookie(tokenClaim, ino, response);
        verify(response).setStatus(ErrorCode.TOKEN_STEALING.getHttpStatus());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding("utf-8");
    }

    @Test
    @DisplayName(value = "토큰 재발급")
    void reIssueToken() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                INO
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenProvider.decodeToken(tokenDTO.accessTokenValue())).thenReturn(USER_ID);
        when(tokenProvider.verifyRefreshToken(tokenDTO.refreshTokenValue(), tokenDTO.inoValue(), USER_ID)).thenReturn(USER_ID);

        jwtTokenService.reIssueToken(tokenDTO, response);

        verify(tokenProvider).issueTokens(USER_ID, tokenDTO.inoValue(), response);
    }

    @Test
    @DisplayName(value = "토큰 재발급. ino 값이 없는 경우")
    void reIssueTokenInoIsNull() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                null
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(jwtTokenService).deleteCookieAndThrowException(response);

        Assertions.assertThrows(CustomTokenStealingException.class,
                () -> jwtTokenService.reIssueToken(tokenDTO, response));

        verify(jwtTokenService).deleteCookieAndThrowException(response);
    }

    @Test
    @DisplayName(value = "토큰 재발급. AccessToken 검증결과만 WRONG인 경우")
    void reIssueTokenAccessTokenIsWrong() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                INO
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenProvider.decodeToken(tokenDTO.accessTokenValue())).thenReturn(Result.WRONG_TOKEN.getResultKey());
        when(tokenProvider.decodeToken(tokenDTO.refreshTokenValue())).thenReturn(USER_ID);
        doNothing().when(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);

        Assertions.assertThrows(CustomTokenStealingException.class,
                () -> jwtTokenService.reIssueToken(tokenDTO, response));

        verify(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);
    }

    @Test
    @DisplayName(value = "토큰 재발급. 모든 토큰 검증결과가 WRONG인 경우")
    void reIssueAllTokenIsWrong() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                INO
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenProvider.decodeToken(tokenDTO.accessTokenValue())).thenReturn(Result.WRONG_TOKEN.getResultKey());
        when(tokenProvider.decodeToken(tokenDTO.refreshTokenValue())).thenReturn(Result.WRONG_TOKEN.getResultKey());

        Assertions.assertThrows(CustomTokenStealingException.class,
                () -> jwtTokenService.reIssueToken(tokenDTO, response));

        verify(tokenProvider).deleteCookie(response);
    }

    @Test
    @DisplayName(value = "토큰 재발급. RefreshToken 검증 결과가 WRONG인 경우")
    void reIssueRefreshTokenIsWrong() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                INO
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenProvider.decodeToken(tokenDTO.accessTokenValue())).thenReturn(USER_ID);
        when(tokenProvider.verifyRefreshToken(tokenDTO.refreshTokenValue(), tokenDTO.inoValue(), USER_ID)).thenReturn(Result.WRONG_TOKEN.getResultKey());
        doNothing().when(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);

        Assertions.assertThrows(CustomTokenStealingException.class,
                () -> jwtTokenService.reIssueToken(tokenDTO, response));

        verify(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);
    }

    @Test
    @DisplayName(value = "토큰 재발급. RefreshToken 검증 결과가 Stealing인 경우")
    void reIssueRefreshTokenIsStealing() {
        TokenDTO tokenDTO = new TokenDTO(
                "AccessTokenValue",
                "RefreshTokenValue",
                INO
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenProvider.decodeToken(tokenDTO.accessTokenValue())).thenReturn(USER_ID);
        when(tokenProvider.verifyRefreshToken(tokenDTO.refreshTokenValue(), tokenDTO.inoValue(), USER_ID)).thenReturn(Result.TOKEN_STEALING.getResultKey());
        doNothing().when(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);
        Assertions.assertThrows(CustomTokenStealingException.class,
                () -> jwtTokenService.reIssueToken(tokenDTO, response));

        verify(jwtTokenService).deleteTokenAndCookieAndThrowException(USER_ID, tokenDTO.inoValue(), response);
    }
}

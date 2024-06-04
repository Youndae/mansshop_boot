package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.TokenDTO;
import com.example.mansshop_boot.service.JWTTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    @Value("#{jwt['token.all.prefix']}")
    private String tokenPrefix;

    private final JWTTokenService tokenService;

    @GetMapping("/reissue")
    public ResponseEntity<?> reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(accessHeader).replace(tokenPrefix, "");
        Cookie refreshToken = WebUtils.getCookie(request, refreshHeader);
        Cookie ino = WebUtils.getCookie(request, inoHeader);

        log.info("reissue!!!!");

        TokenDTO tokenDTO = TokenDTO.builder()
                .accessTokenValue(accessToken)
                .refreshTokenValue(refreshToken == null ? null : refreshToken.getValue().replace(tokenPrefix, ""))
                .inoValue(ino == null ? null : ino.getValue())
                .build();

        return tokenService.reIssueToken(tokenDTO, response);
    }
}

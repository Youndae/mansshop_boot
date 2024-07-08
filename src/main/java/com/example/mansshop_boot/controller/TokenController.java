package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.token.TokenDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.service.JWTTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    /**
     *
     * @param request
     * @param response
     *
     * 토큰 재발급 요청
     */
    @GetMapping("/reissue")
    public ResponseEntity<ResponseMessageDTO> reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(accessHeader).replace(tokenPrefix, "");
        Cookie refreshToken = WebUtils.getCookie(request, refreshHeader);
        Cookie ino = WebUtils.getCookie(request, inoHeader);

        TokenDTO tokenDTO = TokenDTO.builder()
                .accessTokenValue(accessToken)
                .refreshTokenValue(refreshToken == null ? null : refreshToken.getValue().replace(tokenPrefix, ""))
                .inoValue(ino == null ? null : ino.getValue())
                .build();

        String responseMessage = tokenService.reIssueToken(tokenDTO, response);
        HttpStatus status = HttpStatus.OK;

        if(responseMessage.equals(Result.FAIL.getResultKey()))
            status = HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

package com.example.mansshop_boot.config.oAuth;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 successHandler");
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String userId = customOAuth2User.getUserId();
        String nickname = customOAuth2User.getNickname();
        jwtTokenProvider.createTemporaryToken(userId, response);

        if(nickname == null)
            response.sendRedirect("/oAuth?type=new");
        else
            response.sendRedirect("/oAuth?type=old");
    }
}

package com.example.mansshop_boot.auth.oAuth;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String userId = customOAuth2User.getUserId();
        jwtTokenProvider.createTemporaryToken(userId, response);

        /*
        실제 운영되는 서비스의 경우 사용자의 동의하에 필요한 정보를 모두 받을 수 있기 때문에 따로 입력 받을 필요가 없음.
        만약 그 외 직접 받아야 하는 정보가 있다면 아래와 같이 처리해 쿼리 스트링 값에 따라 처리하도록 한다.
        if(nickname == null)
            response.sendRedirect("/oAuth?type=new");
        else
        */

        response.sendRedirect("/oAuth");
    }
}

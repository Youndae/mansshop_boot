package com.example.mansshop_boot.config;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.config.oAuth.CustomOAuth2User;
import com.example.mansshop_boot.domain.dto.oAuth.OAuth2DTO;
import com.example.mansshop_boot.config.security.CustomUser;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.MemberRepository;
import com.example.mansshop_boot.service.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * inoToken = 다중 로그인 허용을 위한 디바이스 식별 번호
 * 정상적으로 로그인한 사용자라면 무조건 존재해야 하기 때문에 조건문으로 가장 먼저 확인.
 *
 * if(ino != null){
 *     if(accessToken != null && refreshToken != null){
 *         정상적인 로그인 사용자의 요청.
 *         토큰 검증 진행.
 *         반환값에 따라 정상이면 이후 처리 진행
 *          검증이 안되는 잘못된 토큰이거나 탈취라고 판단되는 토큰인 경우 모든 토큰을 삭제 처리 후 응답 반환
 *     }else if(accessToken != null && refreshToken == null) {
 *         refreshToken만 없는 경우는 발생할 수 없기 때문에 탈취로 판단
 *     }else {
 *         두 토큰이 모두 존재하지 않거나 RefreshToken만 존재한다면
 *         AccessToken의 만료 또는 새로고침으로 인한 AccessToken 누락일 수 있으므로 검증을 진행하지 않고 이후 처리 진행.
 *         AccessToken이 없다면 권한 처리가 안되기 때문에 문제가 발생할 여지는 없다고 판단.
 *     }
 * }else {
 *     ino가 없다면 아무런 검증도 진행하지 않고 이후 처리 진행.
 *     권한 처리를 하기 위한 조건이 username != null이기 때문에 권한 관리도 되지 않는 비 로그인이라고 판단.
 *     또한 ino가 존재하지 않는다면 권한이 필요하지 않은 기능만 사용이 가능하거나 탈취인지 검증할 수도 없기 때문에 모든 과정을 생략.
 * }
 *
 * if(username != null){
 *     username이 Null이 아닌 경우는 검증이 정상적으로 이루어진 경우.
 *     로컬 로그인인지 OAuth2 로그인인지에 따라 사용자 아이디와 권한을 CustomUser를 통해 찾아내고 해당 정보를 통해 Authentication 객체 생성.
 *     권한 관리를 위해 SecurityContextHolder에 set.
 * }
 *
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    private final JWTTokenProvider jwtTokenProvider;

    private final JWTTokenService jwtTokenService;

    @Value("#{jwt['token.all.prefix']}")
    private String tokenPrefix;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;


    @Override
    protected void doFilterInternal(HttpServletRequest request
                                    , HttpServletResponse response
                                    , FilterChain chain) throws ServletException, IOException {

        String accessToken = request.getHeader(accessHeader);
        Cookie refreshToken = WebUtils.getCookie(request, refreshHeader);
        Cookie inoToken = WebUtils.getCookie(request, inoHeader);
        String username = null; // Authentication 객체 생성 시 필요한 사용자 아이디

        if(inoToken != null){
            String inoValue = inoToken.getValue();
            if(accessToken != null && refreshToken != null) {
                String refreshTokenValue = refreshToken.getValue();
                String accessTokenValue = accessToken.replace(tokenPrefix, "");

                if(!jwtTokenProvider.checkTokenPrefix(accessToken)
                        || !jwtTokenProvider.checkTokenPrefix(refreshTokenValue)){
                    chain.doFilter(request, response);
                    return;
                }else {
                    String claimByAccessToken = jwtTokenProvider.verifyAccessToken(accessTokenValue, inoValue);

                    if(claimByAccessToken.equals(Result.WRONG_TOKEN.getResultKey())
                        || claimByAccessToken.equals(Result.TOKEN_STEALING.getResultKey())){
                        jwtTokenService.deleteCookieAndThrowException(response);
                        return;
                    }else if(claimByAccessToken.equals(Result.TOKEN_EXPIRATION.getResultKey())){
                        if(request.getRequestURI().equals("/api/reissue")) {
                            chain.doFilter(request, response);
                        }else
                            jwtTokenService.tokenExpirationResponse(response);

                        return;
                    }else {
                        username = claimByAccessToken;
                    }
                }
            }else if(accessToken != null && refreshToken == null){
                String decodeTokenClaim = jwtTokenProvider.decodeToken(accessToken.replace(tokenPrefix, ""));

                jwtTokenService.deleteTokenAndCookieAndThrowException(decodeTokenClaim, inoValue, response);
                return;
            }else {
                chain.doFilter(request, response);
                return;
            }
        }

        if(username != null){
            Member memberEntity = memberRepository.findById(username).get();
            String userId;
            Collection<? extends GrantedAuthority> authorities;

            if(memberEntity.getProvider().equals("local")){
                CustomUser customUser = new CustomUser(memberEntity);
                userId = customUser.getMember().getUserId();
                authorities = customUser.getAuthorities();
            }else{
                OAuth2DTO oAuth2DTO = OAuth2DTO.builder()
                        .userId(memberEntity.getUserId())
                        .username(memberEntity.getUserName())
                        .authList(memberEntity.getAuths())
                        .build();

                CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2DTO);
                userId = customOAuth2User.getUserId();
                authorities = customOAuth2User.getAuthorities();
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}

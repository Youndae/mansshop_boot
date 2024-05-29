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
 * if(세가지 토큰이 모두 존재한다면)
 *      if(AccessToken과 RefreshToken의 prefix를 확인했을 때 정상이 아니라면)
 *          더이상 진행하지 않음
 *      else
 *          AcessToken 검증
 *
 *          if(AccessToken 검증 결과로 잘못된 토큰이나 탈취된 토큰이 응답된다면)
 *              응답 이전 Redis 데이터를 삭제 한 뒤 응답하기 때문에
 *              응답 쿠키를 새로 만들어 반환함으로써 ino와 refreshToken이 삭제되도록 처리.
 *              클라이언트에 토큰 탈취 응답(status code = 800)
 *          else if(토큰 만료 응답이라면)
 *              if(요청 URI가 재발급 요청이라면)
 *                  더이상 검증하지 않고 넘겨 컨트롤러에서 대응하도록 처리
 *              else
 *                  클라이언트에 만료 응답 반환
 *          else
 *              토큰 검증 응답이 정상이므로 username 변수에 token claim을 담아줌
 *       else if(ino는 존재하지만 두 토큰이 null이라면)
 *          장기간 미접속으로 인한 두 토큰의 소실일 수 있기도 하고 사용자 아이디를 알아낼 수 없기 때문에 검증하지 않고 넘김
 *       else
 *          두 토큰 중 하나만 존재하기 때문에 탈취로 판단.
 *          존재하는 하나의 토큰을 decode해 claim을 알아내고 redis 데이터와 쿠키 삭제 처리 후 클라이언트에 탈취 응답
 *
 * if(username != null)
 *      username이 null이 아니라면 토큰 검증이 정상적으로 처리된 경우이기 때문에 데이터베이스에서 해당 사용자의 정보를 조회.
 *      가입 경로를 의미하는 provider를 통해 local, OAuth2 로그인을 구분하여 처리 후 Authentication 객체 생성.
 *      권한 관리를 Spring security에 맡기기 위해 SecurityContextHolder에 권한을 set
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
                        if(request.getRequestURI().equals("/api/reIssue")) {
                            chain.doFilter(request, response);
                            return;
                        }else
                            jwtTokenService.tokenExpirationResponse(response);

                        return;
                    }else {
                        username = claimByAccessToken;
                    }
                }
            }else if(accessToken == null && refreshToken == null) {
                chain.doFilter(request, response);
                return;
            }else{
                String decodeTokenClaim = null;

                if (accessToken != null)
                    decodeTokenClaim = jwtTokenProvider.decodeToken(accessToken.replace(tokenPrefix, ""));
                else
                    decodeTokenClaim = jwtTokenProvider.decodeToken(refreshToken.getValue().replace(tokenPrefix, ""));

                jwtTokenService.deleteTokenAndCookieAndThrowException(decodeTokenClaim, inoValue, response);
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

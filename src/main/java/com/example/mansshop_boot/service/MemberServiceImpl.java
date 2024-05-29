package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.config.security.CustomUser;
import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.domain.enumuration.Role;
import com.example.mansshop_boot.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final JWTTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("#{jwt['token.temporary.header']}")
    private String temporaryHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    /**
     *
     * @param joinDTO
     * @return
     *
     * 로컬 회원가입
     */
    @Override
    public long joinProc(JoinDTO joinDTO) {

        Member memberEntity = joinDTO.toEntity();
        memberEntity.addMemberAuth(new Auth().toMemberAuth());

        memberRepository.save(memberEntity);

        return 1L;
    }

    /**
     *
     * @param dto
     * @param request
     * @param response
     * @return
     *
     * 로컬 로그인
     *
     */
    @Override
    public long loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        log.info("loginProc");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.userId(), dto.userPw());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String userId = customUser.getMember().getUserId();

        if(userId != null) {
            if (checkInoAndIssueToken(userId, request, response)) {
                log.info("issuedToken");
                return 1L;
            }
        }

        throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());
    }

    /**
     *
     * @param request
     * @param response
     * @return
     *
     * OAuth2 로그인 후 발급받은 임시토큰을 통한 토큰 발행 요청
     * 임시 토큰 검증 후 토큰 발행
     */
    @Override
    public long oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie temporaryCookie = WebUtils.getCookie(request, temporaryHeader);

        if(temporaryCookie == null)
            throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());

        String temporaryValue = temporaryCookie.getValue();
        String temporaryClaimByUserId = jwtTokenProvider.verifyTemporaryToken(temporaryValue);

        if(temporaryClaimByUserId.equals(Result.WRONG_TOKEN.getResultKey())
                || temporaryClaimByUserId.equals(Result.TOKEN_EXPIRATION.getResultKey()))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        else if(temporaryClaimByUserId.equals(Result.TOKEN_STEALING.getResultKey()))
            throw new CustomTokenStealingException(ErrorCode.TOKEN_STEALING, ErrorCode.TOKEN_STEALING.getMessage());

        jwtTokenProvider.deleteTemporaryTokenAndCookie(temporaryClaimByUserId, response);

        if(checkInoAndIssueToken(temporaryClaimByUserId, request, response))
            return 1L;
        else
            throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());

    }

    /**
     *
     * @param userId
     * @param request
     * @param response
     * @return
     *
     * local, OAuth2 모두 로그인 시 ino가 존재한다면 AccessToken과 RefreshToken만 발급해야 하기 때문에 ino 체크가 필요.
     * ino Cookie의 존재여부와 그에 따른 토큰 발급을 담당
     */
    private boolean checkInoAndIssueToken(String userId, HttpServletRequest request, HttpServletResponse response){
        Cookie inoCookie = WebUtils.getCookie(request, inoHeader);

        if(inoCookie == null)
            jwtTokenProvider.issueAllTokens(userId, response);
        else
            jwtTokenProvider.issueTokens(userId, inoCookie.getValue(), response);

        return true;
    }
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.config.security.CustomUser;
import com.example.mansshop_boot.domain.dto.member.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.LogoutDTO;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.security.Principal;

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

    private static String checkDuplicatedResponseMessage = "duplicated";

    private static String checkNoDuplicatesResponseMessage = "No duplicates";

    /**
     *
     * @param joinDTO
     * @return
     *
     * 로컬 회원가입
     */
    @Override
    public ResponseEntity<?> joinProc(JoinDTO joinDTO) {

        Member memberEntity = joinDTO.toEntity();
        memberEntity.addMemberAuth(new Auth().toMemberAuth());

        memberRepository.save(memberEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ResponseMessageDTO(Result.OK.getResultKey())
                );
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
    public ResponseEntity<ResponseUserStatusDTO> loginProc(LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.userId(), dto.userPw());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String userId = customUser.getUsername();

        if(userId != null) {
            if (checkInoAndIssueToken(userId, request, response)) {
                String uid = customUser.getMember().getNickname() == null ?
                        customUser.getMember().getUserName() : customUser.getMember().getNickname();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(
                                new ResponseUserStatusDTO(new UserStatusDTO(uid))
                        );
            }
        }

        throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());
    }

    /**
     *
     * @param dto
     * @param response
     * @return
     *
     * 로그아웃 처리.
     * Redis 데이터 및 Token Cookie 만료 기간 0으로 초기화해서 Response에 담아 반환
     */
    @Override
    public ResponseEntity<?> logoutProc(LogoutDTO dto, HttpServletResponse response) {

        try{
            jwtTokenProvider.deleteRedisDataAndCookie(dto.userId(), dto.inoValue(), response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessageDTO(Result.OK.getResultKey()));
        }catch (Exception e) {
            log.warn("logout delete Data Exception");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessageDTO(Result.FAIL.getResultKey()));
        }
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
    public ResponseEntity<?> oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response) {

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
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessageDTO(Result.OK.getResultKey()));
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

    @Override
    public ResponseEntity<?> checkJoinId(String userId) {
        Member member = memberRepository.findById(userId).orElse(null);

        String responseMessage = checkDuplicatedResponseMessage;

        if(member == null)
            responseMessage = checkNoDuplicatesResponseMessage;


        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ResponseMessageDTO(responseMessage)
                );
    }

    @Override
    public ResponseEntity<?> checkNickname(String nickname, Principal principal) {

        Member member = memberRepository.findByNickname(nickname);

        String responseMessage = checkDuplicatedResponseMessage;

        if(member == null || principal != null && member.getUserId().equals(principal.getName()))
            responseMessage = checkNoDuplicatesResponseMessage;


        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ResponseMessageDTO(responseMessage)
                );
    }

}

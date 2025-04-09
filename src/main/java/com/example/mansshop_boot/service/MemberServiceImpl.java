package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.customException.exception.CustomTokenStealingException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.auth.user.CustomUser;
import com.example.mansshop_boot.domain.dto.member.business.LogoutDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.in.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserCertificationDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserResetPwDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserSearchIdResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseUserStatusDTO;
import com.example.mansshop_boot.domain.dto.response.UserStatusDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.domain.enumuration.Role;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final AuthRepository authRepository;

    private final JWTTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final StringRedisTemplate redisTemplate;

    private final JavaMailSender javaMailSender;

    @Value("#{jwt['token.temporary.header']}")
    private String temporaryHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    //중복 메세지
    private static final String checkDuplicatedResponseMessage = "duplicated";

    //중복되지 않음 메세지
    private static final String checkNoDuplicatesResponseMessage = "No duplicates";

    /**
     *
     * @param joinDTO
     * @return
     *
     * 로컬 회원가입
     */
    @Override
    public String joinProc(JoinDTO joinDTO) {

        Member memberEntity = joinDTO.toEntity();
        Auth auth = Auth.builder()
                        .auth(Role.MEMBER.getKey())
                        .build();
        memberEntity.addMemberAuth(auth);

        memberRepository.save(memberEntity);
        authRepository.save(auth);

        return Result.OK.getResultKey();
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
    public String logoutProc(LogoutDTO dto, HttpServletResponse response) {

        try{
            jwtTokenProvider.deleteRedisDataAndCookie(dto.userId(), dto.inoValue(), response);

            return Result.OK.getResultKey();
        }catch (Exception e) {
            log.warn("logout delete Data Exception");
            e.printStackTrace();
            return Result.FAIL.getResultKey();
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
    public ResponseEntity<ResponseMessageDTO> oAuthUserIssueToken(HttpServletRequest request, HttpServletResponse response) {

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

    /**
     *
     * @param userId
     *
     * 회원가입 시 아이디 중복 체크
     */
    @Override
    public String checkJoinId(String userId) {
        Member member = memberRepository.findById(userId).orElse(null);

        String responseMessage = checkDuplicatedResponseMessage;

        if(member == null)
            responseMessage = checkNoDuplicatesResponseMessage;


        return responseMessage;
    }

    /**
     *
     * @param nickname
     * @param principal
     *
     * 회원가입 또는 정보 수정 시 닉네임 중복 체크
     */
    @Override
    public String checkNickname(String nickname, Principal principal) {

        Member member = memberRepository.findByNickname(nickname);

        String responseMessage = checkDuplicatedResponseMessage;

        if(member == null || principal != null && member.getUserId().equals(principal.getName()))
            responseMessage = checkNoDuplicatesResponseMessage;


        return responseMessage;
    }

    /**
     *
     * @param searchDTO
     *
     * 아이디 찾기
     */
    @Override
    public UserSearchIdResponseDTO searchId(UserSearchDTO searchDTO) {
        String userId = memberRepository.searchId(searchDTO);
        String message = Result.OK.getResultKey();
        if(userId == null)
            message = Result.NOTFOUND.getResultKey();


        return new UserSearchIdResponseDTO(userId, message);
    }

    /**
     *
     * @param searchDTO
     *
     * 비밀번호 찾기
     * 사용자가 입력한 아이디, 이름, 이메일에 일치하는 데이터가 있다면 숫자 6자리의 인증번호를 Redis에 저장 및 이메일 전송 이후 OK 반환
     * 없다면 FAIL 반환
     */
    @Override
    public String searchPw(UserSearchPwDTO searchDTO) {

        Long count = memberRepository.findByPassword(searchDTO);

        if(count == 0)
            return Result.NOTFOUND.getResultKey();

        Random ran = new Random();
        int certificationNo = ran.nextInt(899999) + 100001;

        try{
            ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();;
            stringValueOperations.set(searchDTO.userId(), String.valueOf(certificationNo), 6L, TimeUnit.MINUTES);

            MimeMessage mailForm = createEmailForm(searchDTO.userEmail(), certificationNo);
            javaMailSender.send(mailForm);

            return Result.OK.getResultKey();
        }catch (Exception e) {
            log.warn("mail Send Exception");
            e.printStackTrace();
            return Result.FAIL.getResultKey();
        }

    }

    /**
     *
     * @param userEmail
     * @param certificationNo
     *
     * 인증번호 메일 전송 폼
     */
    public MimeMessage createEmailForm(String userEmail, int certificationNo) throws MessagingException {
        String mailTitle = "Man's Shop 비밀번호 변경";

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, userEmail);
        message.setSubject(mailTitle);

        String msgOfEmail="";
        msgOfEmail += "<div style='margin:20px;'>";
        msgOfEmail += "<h1> 안녕하세요 test 입니다. </h1>";
        msgOfEmail += "<br>";
        msgOfEmail += "<p>아래 코드를 입력해주세요<p>";
        msgOfEmail += "<br>";
        msgOfEmail += "<p>감사합니다.<p>";
        msgOfEmail += "<br>";
        msgOfEmail += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgOfEmail += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgOfEmail += "<div style='font-size:130%'>";
        msgOfEmail += "CODE : <strong>";
        msgOfEmail += certificationNo + "</strong><div><br/> ";
        msgOfEmail += "</div>";

        message.setText(msgOfEmail, "UTF-8", "html");

        return message;
    }

    /**
     *
     * @param certificationDTO
     *
     * 인증번호 확인
     * 비밀번호 변경 시 인증번호를 한번 더 확인하기 때문에 Redis 데이터와 검증만 하고 삭제하지 않는다.
     */
    @Override
    public String checkCertificationNo(UserCertificationDTO certificationDTO) {
        String result = null;

        try{
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            result = valueOperations.get(certificationDTO.userId());
        }catch (Exception e) {
            log.warn("certificationCheck Exception");
            e.printStackTrace();
            return Result.ERROR.getResultKey();
        }

        if(certificationDTO.certification().equals(result))
            return Result.OK.getResultKey();

        return Result.FAIL.getResultKey();
    }

    /**
     *
     * @param resetDTO
     *
     * 비밀번호 변경
     * 클라이언트에서 인증번호를 같이 담아 요청.
     * 인증번호가 일치하지 않는다면 FAIL을 반환한다.
     *
     * 일치한다면 비밀번호를 수정.
     */
    @Override
    public String resetPw(UserResetPwDTO resetDTO) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String certificationValue = valueOperations.get(resetDTO.userId());
        redisTemplate.delete(resetDTO.userId());

        if(certificationValue == null || !certificationValue.equals(resetDTO.certification()))
            return Result.FAIL.getResultKey();

        Member member = memberRepository.findById(resetDTO.userId()).orElseThrow(IllegalArgumentException::new);
        member.setUserPw(resetDTO.userPw());

        memberRepository.save(member);

        return Result.OK.getResultKey();
    }
}

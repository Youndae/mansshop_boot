package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.member.business.LogoutDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.in.LoginDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserCertificationDTO;
import com.example.mansshop_boot.domain.dto.member.in.UserResetPwDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserSearchIdResponseDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserStatusResponseDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.MemberService;
import com.example.mansshop_boot.util.MailHogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class MemberServiceIT {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private MailHogUtils mailHogUtils;

    private List<Member> memberList;

    //중복 메세지
    private static final String checkDuplicatedResponseMessage = "duplicated";

    //중복되지 않음 메세지
    private static final String checkNoDuplicatesResponseMessage = "No duplicates";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(1);
        memberList = memberAndAuthFixture.memberList();
        memberRepository.saveAll(memberList);
        authRepository.saveAll(memberAndAuthFixture.authList());
    }

    @Test
    @DisplayName(value = "회원가입 요청")
    void postJoin() {
        JoinDTO joinDTO = new JoinDTO(
                "joinTestUser",
                "joinTestPassword1",
                "joinTester",
                "joinNickname",
                "01098765432",
                "2000/01/01",
                "joinTester@join.com"
        );
        LocalDate birth = LocalDate.of(2000, 1, 1);
        String result = assertDoesNotThrow(() -> memberService.joinProc(joinDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Member joinMember = memberRepository.findByLocalUserId(joinDTO.userId());

        assertNotNull(joinMember);
        assertEquals(joinDTO.userId(), joinMember.getUserId());
        assertEquals(joinDTO.userName(), joinMember.getUserName());
        assertEquals(joinDTO.nickname(), joinMember.getNickname());
        assertEquals(joinDTO.phone(), joinMember.getPhone().replaceAll("-", ""));
        assertEquals(birth, joinMember.getBirth());
        assertEquals(joinDTO.userEmail(), joinMember.getUserEmail());
    }

    @Test
    @DisplayName(value = "로그인 요청")
    void postLogin() {
        Member member = memberList.get(0);
        LoginDTO loginDTO = new LoginDTO(member.getUserId(), "1234");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserStatusResponseDTO result = assertDoesNotThrow(() -> memberService.loginProc(loginDTO, request, response));

        assertNotNull(result);

        String accessToken = response.getHeader("Authorization").substring(6);
        Map<String, String> cookieMap = response.getHeaders("Set-Cookie").stream()
                .map(header -> header.split(";", 2)[0])
                .map(kv -> kv.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        String refreshToken = cookieMap.get("Authorization_Refresh").substring(6);
        String ino = cookieMap.get("Authorization_ino");

        String redisAtKey = "at" + ino + member.getUserId();
        String redisRtKey = "rt" + ino + member.getUserId();

        String redisAtValue = redisTemplate.opsForValue().get(redisAtKey);
        String redisRtValue = redisTemplate.opsForValue().get(redisRtKey);

        assertNotNull(redisAtValue);
        assertNotNull(redisRtValue);
        assertEquals(accessToken, redisAtValue);
        assertEquals(refreshToken, redisRtValue);

        redisTemplate.delete(redisAtKey);
        redisTemplate.delete(redisRtKey);
    }

    @Test
    @DisplayName(value = "로그인 요청. 유효하지 않는 요청")
    void postLoginBadCredentials() {
        LoginDTO loginDTO = new LoginDTO("noneUser", "1234");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(
                CustomBadCredentialsException.class,
                () -> memberService.loginProc(loginDTO, request, response)
        );
    }

    @Test
    @DisplayName(value = "로그아웃 요청")
    void postLogout() {
        String ino = "12341234";
        String accessToken = "testaccesstokenvalue";
        String refreshToken = "testrefreshtokenvalue";
        String userId = "logoutTester";
        String atKey = "at" + ino + userId;
        String rtKey = "rt" + ino + userId;

        redisTemplate.opsForValue().set(atKey, accessToken);
        redisTemplate.opsForValue().set(rtKey, refreshToken);

        LogoutDTO logoutDTO = new LogoutDTO(accessToken, ino, userId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = assertDoesNotThrow(() -> memberService.logoutProc(logoutDTO, response));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<String> cookies = response.getHeaders("Set-Cookie");

        boolean refreshCookie = cookies.stream()
                .anyMatch(v ->
                        v.startsWith("Authorization_Refresh=")
                                && v.contains("Max-Age=0")
        );

        boolean inoCookie = cookies.stream()
                .anyMatch(v ->
                        v.startsWith("Authorization_ino=")
                                && v.contains("Max-Age=0")
                );

        assertTrue(refreshCookie);
        assertTrue(inoCookie);

        String accessRedisValue = redisTemplate.opsForValue().get(atKey);
        String refreshRedisValue = redisTemplate.opsForValue().get(rtKey);

        assertNull(accessRedisValue);
        assertNull(refreshRedisValue);
    }

    @Test
    @DisplayName(value = "임시토큰을 발행받은 OAuth2 사용자 정식 토큰 발행")
    void oAuthUserIssueToken() {
        Member member = memberList.get(0);
        MockHttpServletResponse temporaryResponse = new MockHttpServletResponse();

        tokenProvider.createTemporaryToken(member.getUserId(), temporaryResponse);
        String temporaryToken = temporaryResponse.getHeader("Set-Cookie").split(";", 2)[0].split("=", 2)[1];

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("temporary", temporaryToken));
        MockHttpServletResponse newResponse = new MockHttpServletResponse();

        String result = assertDoesNotThrow(() -> memberService.oAuthUserIssueToken(request, newResponse));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        String accessToken = newResponse.getHeader("Authorization").substring(6);
        Map<String, String> cookieMap = newResponse.getHeaders("Set-Cookie").stream()
                .map(header -> header.split(";", 2)[0])
                .map(kv -> kv.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        String refreshToken = cookieMap.get("Authorization_Refresh").substring(6);
        String ino = cookieMap.get("Authorization_ino");

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertNotNull(ino);

        String redisAtKey = "at" + ino + member.getUserId();
        String redisRtKey = "rt" + ino + member.getUserId();

        String redisAtValue = redisTemplate.opsForValue().get(redisAtKey);
        String redisRtValue = redisTemplate.opsForValue().get(redisRtKey);

        assertEquals(accessToken, redisAtValue);
        assertEquals(refreshToken, redisRtValue);
    }

    @Test
    @DisplayName(value = "임시토큰을 발행받은 OAuth2 사용자 정식 토큰 발행. 임시 토큰 쿠키가 없는 경우")
    void oAuthUserIssueTokenTemporaryCookieIsNull() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(
                CustomBadCredentialsException.class,
                () -> memberService.oAuthUserIssueToken(request, response)
        );
    }

    @Test
    @DisplayName(value = "임시토큰을 발행받은 OAuth2 사용자 정식 토큰 발행. 임시 토큰 검증이 실패한 경우")
    void oAuthUserIssueTokenWrongToken() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("temporary", "wrongToken"));

        assertThrows(
                CustomAccessDeniedException.class,
                () -> memberService.oAuthUserIssueToken(request, response)
        );
    }

    @Test
    @DisplayName(value = "아이디 중복 체크")
    void checkJoinUserId() {
        String result = assertDoesNotThrow(() -> memberService.checkJoinId("newUserId"));

        assertNotNull(result);
        assertEquals(checkNoDuplicatesResponseMessage, result);
    }

    @Test
    @DisplayName(value = "아이디 중복 체크. 중복인 경우")
    void checkJoinUserIdDuplicated() {
        Member member = memberList.get(0);
        String result = assertDoesNotThrow(() -> memberService.checkJoinId(member.getUserId()));

        assertNotNull(result);
        assertEquals(checkDuplicatedResponseMessage, result);
    }

    @Test
    @DisplayName(value = "닉네임 중복 체크")
    void checkNickname() {
        String result = assertDoesNotThrow(() -> memberService.checkNickname("newUserNickname", null));

        assertNotNull(result);
        assertEquals(checkNoDuplicatesResponseMessage, result);
    }

    @Test
    @DisplayName(value = "닉네임 중복 체크. 닉네임이 존재하는 사용자가 자신의 닉네임을 그대로 체크하는 경우")
    void checkNicknameOriginNicknameCheck() {
        Member member = memberList.get(0);
        Principal principal = member::getUserId;
        String result = assertDoesNotThrow(() -> memberService.checkNickname(member.getNickname(), principal));

        assertNotNull(result);
        assertEquals(checkNoDuplicatesResponseMessage, result);
    }

    @Test
    @DisplayName(value = "닉네임 중복 체크. 중복인 경우")
    void checkNicknameDuplicated() {
        Member member = memberList.get(0);
        String result = assertDoesNotThrow(() -> memberService.checkNickname(member.getNickname(), null));

        assertNotNull(result);
        assertEquals(checkDuplicatedResponseMessage, result);
    }

    @Test
    @DisplayName(value = "아이디 찾기. 연락처 기반")
    void searchIdByPhone() {
        Member member = memberList.get(0);
        String memberPhone = member.getPhone().replaceAll("-", "");
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), memberPhone, null);

        UserSearchIdResponseDTO result = assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        assertNotNull(result);
        assertEquals(member.getUserId(), result.userId());
        assertEquals(Result.OK.getResultKey(), result.message());
    }

    @Test
    @DisplayName(value = "아이디 찾기. 이메일 기반")
    void searchIdByEmail() {
        Member member = memberList.get(0);
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), null, member.getUserEmail());

        UserSearchIdResponseDTO result = assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        assertNotNull(result);
        assertEquals(member.getUserId(), result.userId());
        assertEquals(Result.OK.getResultKey(), result.message());
    }

    @Test
    @DisplayName(value = "아이디 찾기. 연락처 기반. 데이터가 없는 경우")
    void searchIdByPhoneNotFound() {
        UserSearchDTO searchDTO = new UserSearchDTO("noneUser", "01011119999", null);

        UserSearchIdResponseDTO result = assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        assertNotNull(result);
        assertNull(result.userId());
        assertEquals(Result.NOTFOUND.getResultKey(), result.message());
    }

    @Test
    @DisplayName(value = "아이디 찾기. 이메일 기반. 데이터가 없는 경우")
    void searchIdByEmailNotFound() {
        UserSearchDTO searchDTO = new UserSearchDTO("noneUser", null, "noneUser@none.com");

        UserSearchIdResponseDTO result = assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        assertNotNull(result);
        assertNull(result.userId());
        assertEquals(Result.NOTFOUND.getResultKey(), result.message());
    }

    //메일전송은 mailhog로 처리
    @Test
    @DisplayName(value = "비밀번호 찾기 요청")
    void searchPW() {
        Member member = memberList.get(0);
        UserSearchPwDTO searchPwDTO = new UserSearchPwDTO(member.getUserId(), member.getUserName(), member.getUserEmail());

        ObjectMapper om = new ObjectMapper();
        String result = assertDoesNotThrow(() -> memberService.searchPw(searchPwDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        String redisCertificationValue = redisTemplate.opsForValue().get(member.getUserId());
        assertNotNull(redisCertificationValue);

        try {
            String mailCertification = mailHogUtils.getCertificationNumberByMailHog();
            assertEquals(redisCertificationValue, mailCertification);
            mailHogUtils.deleteMailHog();
            redisTemplate.delete(member.getUserId());
        }catch (Exception e) {
            fail("mail hog check fail");
        }
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 요청. 데이터가 없는 경우")
    void searchPWUserNotFound() {
        UserSearchPwDTO searchPwDTO = new UserSearchPwDTO("noneUserId", "noneUserName", "noneUserEmail");

        String result = assertDoesNotThrow(() -> memberService.searchPw(searchPwDTO));

        assertNotNull(result);
        assertEquals(Result.NOTFOUND.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 인증번호 확인")
    void checkCertificationNo() {
        Member member = memberList.get(0);
        String certificationFixture = "102030";
        UserCertificationDTO certificationDTO = new UserCertificationDTO(member.getUserId(), certificationFixture);
        redisTemplate.opsForValue().set(member.getUserId(), certificationFixture);

        String result = assertDoesNotThrow(() -> memberService.checkCertificationNo(certificationDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        redisTemplate.delete(member.getUserId());
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 인증번호 확인. 인증번호가 일치하지 않는 경우")
    void checkCertificationNoNotEquals() {
        Member member = memberList.get(0);
        String certificationFixture = "102030";
        UserCertificationDTO certificationDTO = new UserCertificationDTO(member.getUserId(), "102031");
        redisTemplate.opsForValue().set(member.getUserId(), certificationFixture);

        String result = assertDoesNotThrow(() -> memberService.checkCertificationNo(certificationDTO));

        assertNotNull(result);
        assertEquals(Result.FAIL.getResultKey(), result);

        redisTemplate.delete(member.getUserId());
    }

    @Test
    @DisplayName(value = "비밀번호 변경")
    void resetPw() {
        Member member = memberList.get(0);
        String certificationFixture = "102030";
        String newUserPw = "5678";
        UserResetPwDTO resetPwDTO = new UserResetPwDTO(member.getUserId(), certificationFixture, newUserPw);
        redisTemplate.opsForValue().set(member.getUserId(), certificationFixture);

        String result = assertDoesNotThrow(() -> memberService.resetPw(resetPwDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Member patchMember = memberRepository.findByLocalUserId(member.getUserId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(encoder.matches(newUserPw, patchMember.getUserPw()));

        String redisCertificationValue = redisTemplate.opsForValue().get(member.getUserId());

        assertNull(redisCertificationValue);
    }

    @Test
    @DisplayName(value = "비밀번호 변경. 인증번호가 일치하지 않는 경우")
    void resetPwCertificationNotEquals() {
        Member member = memberList.get(0);
        String certificationFixture = "102030";
        String newUserPw = "5678";
        UserResetPwDTO resetPwDTO = new UserResetPwDTO(member.getUserId(), "102031", newUserPw);
        redisTemplate.opsForValue().set(member.getUserId(), certificationFixture);

        String result = assertDoesNotThrow(() -> memberService.resetPw(resetPwDTO));

        assertNotNull(result);
        assertEquals(Result.FAIL.getResultKey(), result);

        Member patchMember = memberRepository.findByLocalUserId(member.getUserId());

        assertEquals(member.getUserPw(), patchMember.getUserPw());

        String redisCertificationValue = redisTemplate.opsForValue().get(member.getUserId());

        assertNull(redisCertificationValue);
    }

    @Test
    @DisplayName(value = "비밀번호 변경. 인증번호가 Redis에 존재하지 않는 경우")
    void resetPwCertificationIsNull() {
        Member member = memberList.get(0);
        String certificationFixture = "102030";
        String newUserPw = "5678";
        UserResetPwDTO resetPwDTO = new UserResetPwDTO(member.getUserId(), certificationFixture, newUserPw);

        String result = assertDoesNotThrow(() -> memberService.resetPw(resetPwDTO));

        assertNotNull(result);
        assertEquals(Result.FAIL.getResultKey(), result);

        Member patchMember = memberRepository.findByLocalUserId(member.getUserId());

        assertEquals(member.getUserPw(), patchMember.getUserPw());

        String redisCertificationValue = redisTemplate.opsForValue().get(member.getUserId());

        assertNull(redisCertificationValue);
    }

    @Test
    @DisplayName(value = "비밀번호 변경. 사용자 정보가 존재하지 않는 경우")
    void resetPwUserNotFound() {
        String noneUserId = "noneUser";
        String certificationFixture = "102030";
        String newUserPw = "5678";
        UserResetPwDTO resetPwDTO = new UserResetPwDTO(noneUserId, certificationFixture, newUserPw);
        redisTemplate.opsForValue().set(noneUserId, certificationFixture);

        assertThrows(
                IllegalArgumentException.class,
                () -> memberService.resetPw(resetPwDTO)
        );

        String redisCertificationValue = redisTemplate.opsForValue().get(noneUserId);

        assertNull(redisCertificationValue);
    }
}

package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.dto.member.out.UserSearchIdResponseDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.domain.enumeration.Role;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.MemberServiceImpl;
import com.example.mansshop_boot.service.unit.fixture.MemberUnitFixture;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {
    
    @InjectMocks
    @Spy
    private MemberServiceImpl memberService;
    
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JWTTokenProvider tokenProvider;
    
    @Mock
    private AuthRepository authRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ValueOperations<String, String> valueOperations;
    
    private static final String DUPLICATED_RESPONSE_MESSAGE = "duplicated";;
    
    private static final String NO_DUPLICATED_RESPONSE_MESSAGE = "No duplicates";

    @Test
    @DisplayName(value = "회원가입 요청")
    void joinProc() {
        JoinDTO joinDTO = new JoinDTO(
                "userId",
                "userPw",
                "userName",
                "nickname",
                "01012345678",
                "2000/01/01",
                "userEmail@email.com"
        );
        Member member = joinDTO.toEntity();
        Auth auth = Auth.builder()
                .auth(Role.MEMBER.getKey())
                .build();
        
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(authRepository.save(any(Auth.class))).thenReturn(auth);
        
        String result = Assertions.assertDoesNotThrow(() -> memberService.joinProc(joinDTO));
        
        verify(memberRepository).save(any(Member.class));
        verify(authRepository).save(any(Auth.class));
        Assertions.assertEquals(Result.OK.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "회원가입시 아이디 중복체크. 정상인 경우")
    void checkJoinId() {
        when(memberRepository.findById("userId")).thenReturn(null);

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkJoinId("userId"));

        Assertions.assertEquals(NO_DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "회원가입시 아이디 중복체크. 중복인 경우")
    void checkJoinIdIsDuplicated() {
        Member member = MemberUnitFixture.createMemberFixture();

        when(memberRepository.findById(member.getUserId())).thenReturn(Optional.of(member));

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkJoinId(member.getUserId()));

        Assertions.assertEquals(DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "회원가입시 닉네임 중복체크. 정상인 경우")
    void checkJoinNickname() {
        when(memberRepository.findByNickname("nickname")).thenReturn(null);

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkNickname("nickname", null));

        Assertions.assertEquals(NO_DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "회원가입시 닉네임 중복체크. 중복인 경우")
    void checkJoinNicknameIsDuplicated() {
        Member member = MemberUnitFixture.createMemberFixture();

        when(memberRepository.findByNickname("nickname")).thenReturn(member);

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkNickname(member.getUserId(), null));

        Assertions.assertEquals(DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "정보 수정시 닉네임 중복체크. 정상인 경우")
    void checkNickname() {
        Principal principal = mock(Principal.class);

        when(principal.getName()).thenReturn("nickname");
        when(memberRepository.findByNickname("nicknameElse")).thenReturn(null);

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkNickname("nickname", null));

        Assertions.assertEquals(NO_DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "정보 수정시 닉네임 중복체크. 중복인 경우")
    void checkNicknameIsDuplicated() {
        Member member = MemberUnitFixture.createMemberFixture();

        Principal principal = mock(Principal.class);

        when(principal.getName()).thenReturn("nickname");
        when(memberRepository.findByNickname("nickname")).thenReturn(member);

        String result = Assertions.assertDoesNotThrow(() -> memberService.checkNickname("nickname", null));

        Assertions.assertEquals(DUPLICATED_RESPONSE_MESSAGE, result);
    }

    @Test
    @DisplayName(value = "아이디 찾기 요청")
    void searchId() {
        String userId = "userId";
        UserSearchDTO searchDTO = new UserSearchDTO("userName", "01012345678", null);

        when(memberRepository.searchId(searchDTO)).thenReturn(userId);

        UserSearchIdResponseDTO result = Assertions.assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        Assertions.assertEquals(Result.OK.getResultKey(), result.message());
        Assertions.assertEquals(userId, result.userId());
    }

    @Test
    @DisplayName(value = "아이디 찾기 요청. 정보가 없는 경우")
    void searchIdIsNull() {
        UserSearchDTO searchDTO = new UserSearchDTO("userName", "01012345678", null);

        when(memberRepository.searchId(searchDTO)).thenReturn(null);

        UserSearchIdResponseDTO result = Assertions.assertDoesNotThrow(() -> memberService.searchId(searchDTO));

        Assertions.assertEquals(Result.NOTFOUND.getResultKey(), result.message());
        Assertions.assertNull(result.userId());
    }

    @Test
    @DisplayName("비밀번호 찾기 요청. 정보 확인 후 인증번호 메일 전송")
    void searchPw() throws MessagingException {
        UserSearchPwDTO searchDTO = new UserSearchPwDTO("userId", "userName", "userEmail@userEmail.com");
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(memberRepository.findByPassword(searchDTO)).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), eq(TimeUnit.MINUTES));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mimeMessage).setText(anyString(), eq("UTF-8"), eq("html"));;
        doNothing().when(javaMailSender).send(mimeMessage);
        doNothing().when(mimeMessage).addRecipients(MimeMessage.RecipientType.TO, searchDTO.userEmail());

        String result = Assertions.assertDoesNotThrow(() -> memberService.searchPw(searchDTO));

        Assertions.assertEquals(Result.OK.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 요청. 사용자 정보가 없는 경우")
    void searchPwUserNotFound() {
        UserSearchPwDTO searchDTO = new UserSearchPwDTO("userId", "userName", "userEmail@userEmail.com");

        when(memberRepository.findByPassword(searchDTO)).thenReturn(0L);

        String result = Assertions.assertDoesNotThrow(() -> memberService.searchPw(searchDTO));

        Assertions.assertEquals(Result.NOTFOUND.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "비밀번호 찾기 요청. 메일 전송 과정에서 Exception이 발생하는 경우.")
    void searchPwMessagingException() {
        UserSearchPwDTO searchDTO = new UserSearchPwDTO("userId", "userName", "userEmail@userEmail.com");

        when(memberRepository.findByPassword(searchDTO)).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), eq(TimeUnit.MINUTES));
        when(javaMailSender.createMimeMessage()).thenThrow(new MessagingException("Mail send Fail"));

        String result = Assertions.assertDoesNotThrow(() -> memberService.searchPw(searchDTO));

        Assertions.assertEquals(Result.FAIL.getResultKey(), result);
    }
}

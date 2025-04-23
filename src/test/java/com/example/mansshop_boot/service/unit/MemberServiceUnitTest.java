package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.domain.dto.member.in.JoinDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.domain.enumuration.Role;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.MemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {
    
    @InjectMocks
    private MemberServiceImpl memberService;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private AuthRepository authRepository;
    
    private final String DUPLICATED_RESPONSE_MESSAGE = "duplicated";;
    
    private final String NO_DUPLICATED_RESPONSE_MESSAGE = "No duplicates";

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
    void name() {
    }
}

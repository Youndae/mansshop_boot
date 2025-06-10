package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.admin.AdminMemberServiceImpl;
import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminMemberServiceUnitTest {

    @InjectMocks
    private AdminMemberServiceImpl adminMemberService;

    @Mock
    private MemberRepository memberRepository;

    private List<Member> memberList;

    private AdminOrderPageDTO pageDTO;

    @BeforeAll
    void init() {
        memberList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
    }

    @Test
    @DisplayName(value = "회원 목록 조회")
    void getMemberList() {
        List<AdminMemberDTO> memberResponseList = memberList.stream()
                                                            .map(v -> new AdminMemberDTO(
                                                                    v.getUserId(),
                                                                    v.getUserName(),
                                                                    v.getNickname(),
                                                                    v.getPhone(),
                                                                    v.getUserEmail(),
                                                                    v.getBirth(),
                                                                    v.getMemberPoint(),
                                                                    LocalDateTime.now()
                                                            ))
                                                            .limit(20)
                                                            .toList();
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("createdAt").descending()
        );
        when(memberRepository.findMember(pageDTO, pageable))
                .thenReturn(new PageImpl<>(memberResponseList, pageable, memberList.size()));

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertEquals(memberResponseList.size(), result.getContent().size());
        Assertions.assertEquals(2, result.getTotalPages());
        Assertions.assertEquals(memberList.size(), result.getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 목록 조회. 회원이 없는 경우")
    void getMemberListEmpty() {
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("createdAt").descending()
        );

        when(memberRepository.findMember(pageDTO, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalPages());
        Assertions.assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 포인트 지급. 회원이 없는 경우")
    void postPointNotFoundMember() {
        AdminPostPointDTO adminPostPointDTO = new AdminPostPointDTO("user1", 1000);
        when(memberRepository.findById(adminPostPointDTO.userId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminMemberService.postPoint(adminPostPointDTO)
        );

        verify(memberRepository, never()).save(any(Member.class));
    }
}

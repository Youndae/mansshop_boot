package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.Fixture.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.service.admin.AdminMemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminMemberServiceIT {

    @Autowired
    private AdminMemberService adminMemberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    private List<Member> memberList;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberFixture = MemberAndAuthFixture.createDefaultMember(30);
        memberList = memberFixture.memberList();
        List<Auth> authList = memberFixture.authList();

        for(int i = 0; i < memberList.size(); i++) {
            try {
                Thread.sleep(5);
                memberRepository.save(memberList.get(i));
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        authRepository.saveAll(authList);
    }

    @Test
    @DisplayName(value = "회원 목록 조회")
    void getMemberList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        int totalPages = PaginationUtils.getTotalPages(memberList.size(), pageDTO.amount());

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.getTotalElements() < 1);
        Assertions.assertEquals(memberList.size(), result.getTotalElements());
        Assertions.assertEquals(pageDTO.amount(), result.getContent().size());
        Assertions.assertEquals(totalPages, result.getTotalPages());
        Assertions.assertEquals(pageDTO.page() - 1, result.getNumber());
    }

    @Test
    @DisplayName(value = "회원 목록 조회. 사용자 아이디 기반 검색")
    void getMemberListSearchUserId() {
        Member searchMember = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMember.getUserId(), "userId", 1);

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.getTotalElements() < 1);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(pageDTO.page() - 1, result.getNumber());

        AdminMemberDTO resultContent = result.getContent().get(0);

        Assertions.assertEquals(searchMember.getUserId(), resultContent.userId());
        Assertions.assertEquals(searchMember.getUserName(), resultContent.userName());
        Assertions.assertEquals(searchMember.getNickname(), resultContent.nickname());
        Assertions.assertEquals(searchMember.getUserEmail(), resultContent.email());
        Assertions.assertEquals(searchMember.getBirth(), resultContent.birth());
    }

    @Test
    @DisplayName(value = "회원 목록 조회. 사용자 이름 기반 검색")
    void getMemberListSearchUserName() {
        Member searchMember = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMember.getUserName(), "userName", 1);

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.getTotalElements() < 1);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(pageDTO.page() - 1, result.getNumber());

        AdminMemberDTO resultContent = result.getContent().get(0);

        Assertions.assertEquals(searchMember.getUserId(), resultContent.userId());
        Assertions.assertEquals(searchMember.getUserName(), resultContent.userName());
        Assertions.assertEquals(searchMember.getNickname(), resultContent.nickname());
        Assertions.assertEquals(searchMember.getUserEmail(), resultContent.email());
        Assertions.assertEquals(searchMember.getBirth(), resultContent.birth());
    }

    @Test
    @DisplayName(value = "회원 목록 조회. 사용자 닉네임 기반 검색")
    void getMemberListSearchNickName() {
        Member searchMember = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(searchMember.getNickname(), "nickname", 1);

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getContent().isEmpty());
        Assertions.assertFalse(result.getTotalElements() < 1);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(pageDTO.page() - 1, result.getNumber());

        AdminMemberDTO resultContent = result.getContent().get(0);

        Assertions.assertEquals(searchMember.getUserId(), resultContent.userId());
        Assertions.assertEquals(searchMember.getUserName(), resultContent.userName());
        Assertions.assertEquals(searchMember.getNickname(), resultContent.nickname());
        Assertions.assertEquals(searchMember.getUserEmail(), resultContent.email());
        Assertions.assertEquals(searchMember.getBirth(), resultContent.birth());
    }

    @Test
    @DisplayName(value = "회원 목록 조회. 데이터가 없는 경우")
    void getMemberListEmpty() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO("noneMember", "nickname", 1);

        Page<AdminMemberDTO> result = Assertions.assertDoesNotThrow(() -> adminMemberService.getMemberList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName(value = "회원 포인트 지급")
    void postPoint() {
        Member member = memberList.get(0);
        AdminPostPointDTO pointDTO = new AdminPostPointDTO(member.getUserId(), 100);

        String result = Assertions.assertDoesNotThrow(() -> adminMemberService.postPoint(pointDTO));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        Member resultMember = memberRepository.findById(member.getUserId()).orElse(null);

        Assertions.assertNotNull(resultMember);
        Assertions.assertEquals(100, resultMember.getMemberPoint());
    }

    @Test
    @DisplayName(value = "회원 포인트 지급. 사용자가 존재하지 않는 경우")
    void postPointMemberNotFound() {
        AdminPostPointDTO pointDTO = new AdminPostPointDTO("noneMember", 100);

        Assertions.assertThrows(IllegalArgumentException.class, () -> adminMemberService.postPoint(pointDTO));
    }
}

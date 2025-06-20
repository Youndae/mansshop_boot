package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@EntityScan(basePackages = "com.example")
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    private List<Member> memberList;

    private Member admin;

    @BeforeAll
    void init() {
        MemberAndAuthFixtureDTO fixtureDTO = MemberAndAuthFixture.createDefaultMember(10);
        memberRepository.saveAll(fixtureDTO.memberList());
        authRepository.saveAll(fixtureDTO.authList());

        MemberAndAuthFixtureDTO adminDTO = MemberAndAuthFixture.createAdmin();
        memberRepository.saveAll(adminDTO.memberList());
        authRepository.saveAll(adminDTO.authList());

        memberList = fixtureDTO.memberList();
        admin = adminDTO.memberList().get(0);
    }

    @Test
    @DisplayName(value = "사용자의 닉네임 조회")
    void findByNickname() {
        Member member = memberList.get(0);

        Member result = memberRepository.findByNickname(member.getNickname());
        System.out.println("test createdAt : " + result.getCreatedAt());
        assertNotNull(result);
        assertEquals(member.getNickname(), result.getNickname());
    }

    @Test
    @DisplayName(value = "로컬 사용자(직접 회원가입한) 조회")
    void findByLocalUserId() {
        Member member = memberList.get(0);

        Member result = memberRepository.findByLocalUserId(member.getUserId());

        assertNotNull(result);
        assertEquals(member.getUserId(), result.getUserId());
        assertEquals(member.getUserName(), result.getUserName());
        assertNotNull(result.getAuths());
        assertEquals(1, result.getAuths().size());
    }

    @Test
    @DisplayName(value = "관리자 조회")
    void findByLocalUserIdToAdmin() {
        Member result = memberRepository.findByLocalUserId(admin.getUserId());

        assertNotNull(result);
        assertEquals(admin.getUserId(), result.getUserId());
        assertEquals(admin.getUserName(), result.getUserName());
        assertEquals(3, result.getAuths().size());
    }

    @Test
    @DisplayName(value = "관리자의 사용자 조회")
    void findMember() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("createdAt").descending());

        Page<AdminMemberDTO> result = memberRepository.findMember(pageDTO, pageable);

        assertNotNull(result);
        assertEquals(memberList.size() + 1, result.getTotalElements());
        assertEquals(memberList.size() + 1, result.getContent().size());
    }

    @Test
    @DisplayName(value = "아이디 찾기. 존재하는 경우 아이디 반환. 연락처 기반 검색")
    void searchIdByPhone() {
        Member member = memberList.get(0);
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), member.getPhone(), null);
        String result = memberRepository.searchId(searchDTO);

        assertNotNull(result);
        assertEquals(member.getUserId(), result);
    }

    @Test
    @DisplayName(value = "아이디 찾기. 존재하는 경우 아이디 반환. 이메일 기반 검색")
    void searchIdByEmail() {
        Member member = memberList.get(0);
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), null, member.getUserEmail());
        String result = memberRepository.searchId(searchDTO);

        assertNotNull(result);
        assertEquals(member.getUserId(), result);
    }

    @Test
    @DisplayName(value = "아이디 찾기. 존재하지 않는 데이터인 경우. 연락처 기반 검색")
    void searchIdByPhoneFail() {
        Member member = memberList.get(0);
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), "010-0000-0000", null);
        String result = memberRepository.searchId(searchDTO);

        assertNull(result);
    }

    @Test
    @DisplayName(value = "아이디 찾기. 존재하지 않는 데이터인 경우. 이메일 기반 검색")
    void searchIdByEmailFail() {
        Member member = memberList.get(0);
        UserSearchDTO searchDTO = new UserSearchDTO(member.getUserName(), null, "fail@fail.com");
        String result = memberRepository.searchId(searchDTO);

        assertNull(result);
    }

    @Test
    @DisplayName(value = "비밀번호 찾기. 데이터가 존재한다면 1을 반환")
    void findByPassword() {
        Member member = memberList.get(0);
        UserSearchPwDTO searchPwDTO = new UserSearchPwDTO(member.getUserId(), member.getUserName(), member.getUserEmail());
        Long result = memberRepository.findByPassword(searchPwDTO);

        assertEquals(1L, result);
    }

    @Test
    @DisplayName(value = "비밀번호 찾기. 데이터가 존재하지 않는다면 0을 반환")
    void findByPasswordFail() {
        Member member = memberList.get(0);
        UserSearchPwDTO searchPwDTO = new UserSearchPwDTO(member.getUserId(), member.getUserName(), "fail@fail.com");
        Long result = memberRepository.findByPassword(searchPwDTO);

        assertNotNull(result);
        assertEquals(0L, result);
    }


}

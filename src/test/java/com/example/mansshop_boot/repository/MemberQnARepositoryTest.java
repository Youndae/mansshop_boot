package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.MemberQnAFixture;
import com.example.mansshop_boot.Fixture.QnAClassificationFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import com.example.mansshop_boot.domain.entity.QnAClassification;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class MemberQnARepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private QnAClassificationRepository qnAClassificationRepository;

    @Autowired
    private MemberQnARepository memberQnARepository;

    private Member member;

    private MemberQnA memberQnA;

    private int dataTotalCount;

    private int newDataCount;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberFixtureDTO = MemberAndAuthFixture.createDefaultMember(5);
        List<Member> memberList = memberFixtureDTO.memberList();
        List<Auth> authList = memberFixtureDTO.authList();
        List<QnAClassification> qnAClassifications = QnAClassificationFixture.createQnAClassificationList();

        memberRepository.saveAll(memberList);
        authRepository.saveAll(authList);
        qnAClassificationRepository.saveAll(qnAClassifications);

        List<MemberQnA> completedAnswerMemberQnAList = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassifications, memberList);
        List<MemberQnA> newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassifications, memberList);
        completedAnswerMemberQnAList.addAll(newMemberQnAList);

        memberQnARepository.saveAll(completedAnswerMemberQnAList);

        member = memberList.get(0);
        memberQnA = completedAnswerMemberQnAList.get(0);
        dataTotalCount = completedAnswerMemberQnAList.size();
        newDataCount = newMemberQnAList.size();
    }

    @Test
    @DisplayName(value = "사용자의 전체 문의 목록 조회")
    void findAllByUserId() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                    , pageDTO.amount()
                                    , Sort.by("id").descending());

        Page<MemberQnAListDTO> result = memberQnARepository.findAllByUserId(member.getUserId(), pageable);

        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(6, result.getContent().size());
    }

    @Test
    @DisplayName(value = "문의 상세 내역 조회")
    void findByQnAId() {
        MemberQnADTO result = memberQnARepository.findByQnAId(memberQnA.getId());

        assertNotNull(result);
        assertEquals(memberQnA.getId(), result.memberQnAId());
        assertEquals(memberQnA.getQnAClassification().getQnaClassificationName(), result.qnaClassification());
        assertEquals(memberQnA.getMemberQnATitle(), result.qnaTitle());
        assertEquals(memberQnA.getMemberQnAContent(), result.qnaContent());
    }

    @Test
    @DisplayName(value = "문의 수정을 위한 조회 요청")
    void findModifyDataByIdAndUserId() {
        MemberQnA result = memberQnARepository.findModifyDataByIdAndUserId(memberQnA.getId(), memberQnA.getMember().getUserId());

        assertNotNull(result);
        assertEquals(memberQnA.getId(), result.getId());
        assertEquals(memberQnA.getMemberQnAContent(), result.getMemberQnAContent());
        assertEquals(memberQnA.getMemberQnATitle(), result.getMemberQnATitle());
        assertEquals(memberQnA.getQnAClassification().getId(), result.getQnAClassification().getId());
    }

    @Test
    @DisplayName(value = "문의 수정을 위한 조회 요청. id는 일치하지만 userId가 다른 경우")
    void findModifyDataByIdAndUserIdFail() {
        MemberQnA result = memberQnARepository.findModifyDataByIdAndUserId(memberQnA.getId(), "FakeUser");

        assertNull(result);
    }

    @Test
    @DisplayName(value = "관리자의 전체 문의 목록 조회")
    void findAllByAllAdminMemberQnA() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);

        List<AdminQnAListResponseDTO> result = memberQnARepository.findAllByAdminMemberQnA(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "관리자의 전체 문의 목록 중 검색")
    void findAllByAllAdminMemberQnASearch() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("tester1", "all", 1);

        List<AdminQnAListResponseDTO> result = memberQnARepository.findAllByAdminMemberQnA(pageDTO);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName(value = "관리자의 새로운(미처리된) 문의 목록 조회")
    void findAllByNewAdminMemberQnA() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "new", 1);

        List<AdminQnAListResponseDTO> result = memberQnARepository.findAllByAdminMemberQnA(pageDTO);

        assertNotNull(result);
        assertEquals(newDataCount, result.size());
    }

    @Test
    @DisplayName(value = "관리자의 새로운(미처리된) 문의 목록 조회")
    void findAllByNewAdminMemberQnASearch() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("tester1", "new", 1);

        List<AdminQnAListResponseDTO> result = memberQnARepository.findAllByAdminMemberQnA(pageDTO);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName(value = "관리자의 전체 문의 목록 count")
    void findAllByAllAdminMemberQnACount() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);
        Long result = memberQnARepository.findAllByAdminMemberQnACount(pageDTO);

        assertNotNull(result);
        assertEquals(dataTotalCount, result);
    }

    @Test
    @DisplayName(value = "관리자의 새로운(미처리된) 문의 목록 count")
    void findAllByNewAdminMemberQnACount() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "new", 1);

        Long result = memberQnARepository.findAllByAdminMemberQnACount(pageDTO);

        assertNotNull(result);
        assertEquals(newDataCount, result);
    }
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.MemberQnAFixture;
import com.example.mansshop_boot.Fixture.QnAClassificationFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class MemberQnAReplyRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private QnAClassificationRepository qnAClassificationRepository;

    @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private MemberQnAReplyRepository memberQnAReplyRepository;

    private List<MemberQnAReply> memberQnAReplies;

    @BeforeAll
    void init() {
        MemberAndAuthFixtureDTO memberFixtureDTO = MemberAndAuthFixture.createDefaultMember(5);
        List<Member> memberList = memberFixtureDTO.memberList();
        List<Auth> authList = memberFixtureDTO.authList();
        List<QnAClassification> qnAClassifications = QnAClassificationFixture.createQnAClassificationList();
        MemberAndAuthFixtureDTO adminFixtureDTO = MemberAndAuthFixture.createAdmin();
        Member admin = adminFixtureDTO.memberList().get(0);
        memberList.add(admin);
        authList.addAll(adminFixtureDTO.authList());

        memberRepository.saveAll(memberList);
        authRepository.saveAll(authList);
        qnAClassificationRepository.saveAll(qnAClassifications);

        List<MemberQnA> completedAnswerMemberQnAList = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassifications, memberList);
        List<MemberQnAReply> memberQnAReplyList = MemberQnAFixture.createMemberQnAReply(completedAnswerMemberQnAList, admin);
        List<MemberQnA> newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassifications, memberList);
        completedAnswerMemberQnAList.addAll(newMemberQnAList);

        memberQnARepository.saveAll(completedAnswerMemberQnAList);
        memberQnAReplyRepository.saveAll(memberQnAReplyList);
        Long memberQnAId = completedAnswerMemberQnAList.get(0).getId();

        memberQnAReplies = memberQnAReplyList.stream()
                                            .filter(v -> v.getMemberQnA().getId() == memberQnAId)
                                            .toList();
    }

    @Test
    @DisplayName(value = "해당 문의의 모든 Reply 조회")
    void findAllByQnAId() {
        Long qnaId = memberQnAReplies.get(0).getId();

        List<MyPageQnAReplyDTO> result = memberQnAReplyRepository.findAllByQnAId(qnaId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(memberQnAReplies.size(), result.size());
    }
}

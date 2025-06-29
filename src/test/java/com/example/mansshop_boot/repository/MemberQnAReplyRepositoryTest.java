package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.MemberQnAFixture;
import com.example.mansshop_boot.Fixture.QnAClassificationFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
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

    private List<MemberQnA> completedAnswerMemberQnAList;

    private List<MemberQnAReply> memberQnAReplyList;

    @BeforeEach
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

        completedAnswerMemberQnAList = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassifications, memberList);
        memberQnAReplyList = MemberQnAFixture.createMemberQnAReply(completedAnswerMemberQnAList, admin);
        List<MemberQnA> newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassifications, memberList);
        completedAnswerMemberQnAList.addAll(newMemberQnAList);

        memberQnARepository.saveAll(completedAnswerMemberQnAList);
        memberQnAReplyRepository.saveAll(memberQnAReplyList);
    }

    @Test
    @DisplayName(value = "해당 문의의 모든 Reply 조회")
    void findAllByQnAId() {
        Long qnaId = completedAnswerMemberQnAList.get(0).getId();
        List<MemberQnAReply> resultFixture = memberQnAReplyList.stream()
                                                .filter(v ->
                                                        v.getMemberQnA().getId().equals(qnaId)
                                                )
                                                .toList();

        List<MyPageQnAReplyDTO> result = memberQnAReplyRepository.findAllByQnAId(qnaId);

        assertNotNull(result);
        assertEquals(resultFixture.size(), result.size());
    }
}

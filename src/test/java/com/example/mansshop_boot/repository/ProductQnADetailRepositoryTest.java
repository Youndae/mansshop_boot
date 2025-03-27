package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductQnAFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductQnAReplyListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductQnADetailRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private ProductQnAReplyRepository productQnAReplyRepository;

    private static final int PRODUCT_SIZE = 30;

    private static final int MEMBER_SIZE = 5;

    private List<ProductQnA> completedQnAList;

    private List<ProductQnA> newQnAList;

    private List<ProductQnA> allProductQnA;

    private List<ProductQnAReply> completedQnAReplyList;

    @BeforeAll
    void init() {
        List<Classification> classificationList = ClassificationFixture.createClassification();
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(MEMBER_SIZE);
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<Member> memberFixtureList = memberAndAuthFixtureDTO.memberList();
        List<Auth> authFixtureList = memberAndAuthFixtureDTO.authList();
        List<ProductOption> productOptionFixtureList = productFixtureList.stream()
                                                                        .flatMap(v -> v.getProductOptions().stream())
                                                                        .toList();
        MemberAndAuthFixtureDTO adminFixtureDTO = MemberAndAuthFixture.createAdmin();
        memberFixtureList.add(adminFixtureDTO.memberList().get(0));
        authFixtureList.addAll(adminFixtureDTO.authList());

        memberRepository.saveAll(memberFixtureList);
        authRepository.saveAll(memberAndAuthFixtureDTO.authList());
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(productOptionFixtureList);

        Member admin = adminFixtureDTO.memberList().get(0);
        List<ProductQnA> productQnACompletedFixtureList = ProductQnAFixture.createProductQnACompletedAnswer(memberFixtureList, productFixtureList);
        completedQnAList = new ArrayList<>(productQnACompletedFixtureList);
        List<ProductQnAReply> productQnAReplies = ProductQnAFixture.createDefaultProductQnaReply(admin, productQnACompletedFixtureList);
        List<ProductQnA> productQnAFixtureList = ProductQnAFixture.createProductQnACompletedAnswer(memberFixtureList, productFixtureList);

        productQnACompletedFixtureList.addAll(productQnAFixtureList);

        productQnARepository.saveAll(productQnACompletedFixtureList);
        productQnAReplyRepository.saveAll(productQnAReplies);

        completedQnAReplyList = productQnAReplies;
        newQnAList = productQnAFixtureList;
        allProductQnA = productQnACompletedFixtureList;
    }

    @Test
    @DisplayName(value = "상품 문의 아이디 리스트 기반 답변 리스트 조회")
    void getQnAReply() {
        List<Long> productQnAIds = allProductQnA.stream().mapToLong(ProductQnA::getId).boxed().toList();
        List<ProductQnAReplyListDTO> result = productQnAReplyRepository.getQnAReply(productQnAIds);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(completedQnAReplyList.size(), result.size());
    }

    @Test
    @DisplayName(value = "상품 문의 아이디 리스트 기반 답변 리스트 조회. 답변이 없는 경우")
    void getQnAReplyEmpty() {
        List<Long> productQnAIds = newQnAList.stream().mapToLong(ProductQnA::getId).boxed().toList();
        List<ProductQnAReplyListDTO> result = productQnAReplyRepository.getQnAReply(productQnAIds);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName(value = "상품 문의 아이디 기반 모든 답변 조회")
    void findAllByQnAId() {
        List<ProductQnAReply> replyDataList = completedQnAReplyList.stream()
                                                            .filter(v ->
                                                                    v.getProductQnA().getId() == completedQnAList.get(0).getId()
                                                            )
                                                            .toList();
        List<MyPageQnAReplyDTO> result = productQnAReplyRepository.findAllByQnAId(replyDataList.get(0).getProductQnA().getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(replyDataList.size(), result.size());
    }
}

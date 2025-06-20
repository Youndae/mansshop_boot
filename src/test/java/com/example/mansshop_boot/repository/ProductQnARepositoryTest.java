package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductQnAFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.ProductQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductQnADTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class ProductQnARepositoryTest {

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

    private static final int PRODUCT_SIZE = 30;

    private static final int MEMBER_SIZE = 5;

    private Product product;

    private Member member;

    private List<ProductQnA> qnaList;

    @BeforeAll
    void init() {
        List<Classification> classificationList = ClassificationFixture.createClassification();
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(MEMBER_SIZE);
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<Member> memberFixtureList = memberAndAuthFixtureDTO.memberList();
        List<ProductOption> productOptionFixtureList = productFixtureList.stream()
                                                                        .flatMap(v -> v.getProductOptions().stream())
                                                                        .toList();
        memberRepository.saveAll(memberFixtureList);
        authRepository.saveAll(memberAndAuthFixtureDTO.authList());
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(productOptionFixtureList);

        List<ProductQnA> productQnACompletedFixtureList = ProductQnAFixture.createProductQnACompletedAnswer(memberFixtureList, productFixtureList);
        List<ProductQnA> productQnAFixtureList = ProductQnAFixture.createDefaultProductQnA(memberFixtureList, productFixtureList);
        productQnACompletedFixtureList.addAll(productQnAFixtureList);

        productQnARepository.saveAll(productQnACompletedFixtureList);

        product = productFixtureList.get(0);
        member = memberFixtureList.get(0);
        qnaList = productQnACompletedFixtureList;
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 상품 문의 조회")
    void findByProductId() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable qnaPageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.qnaAmount()
                , Sort.by("createdAt").descending()
        );
        List<Long> dataList = qnaList.stream()
                                        .filter(v -> v.getProduct().getId().equals(product.getId()))
                                        .mapToLong(ProductQnA::getId)
                                        .boxed()
                                        .toList();
        Page<ProductQnADTO> result = productQnARepository.findByProductId(product.getId(), qnaPageable);

        assertNotNull(result);
        assertEquals(dataList.size(), result.getTotalElements());
        result.getContent().forEach(v -> assertTrue(dataList.contains(v.qnaId())));
    }

    @Test
    @DisplayName(value = "사용자 아이디 기반 상품 문의 조회")
    void findByUserId() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        List<ProductQnA> dataList = qnaList.stream()
                                            .filter(v ->
                                                    v.getMember().getUserId().equals(member.getUserId())
                                            )
                                            .toList();
        List<Long> dataQnAIds = dataList.stream()
                                        .mapToLong(ProductQnA::getId)
                                        .boxed()
                                        .toList();
        Page<ProductQnAListDTO> result = productQnARepository.findByUserId(member.getUserId(), pageable);

        assertNotNull(result);
        assertEquals(dataList.size(), result.getTotalElements());
        result.forEach(v -> assertTrue(dataQnAIds.contains(v.productQnAId())));
    }

    @Test
    @DisplayName(value = "사용자 아이디 기반 상품 문의 조회. 데이터가 존재하지 않는 경우")
    void findByUserIdEmpty() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        Page<ProductQnAListDTO> result = productQnARepository.findByUserId("fakeUser", pageable);

        assertNotNull(result);
        assertEquals(0L, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "상품 문의 상세 데이터 조회")
    void findByQnAId() {
        ProductQnA productQnA = qnaList.get(0);
        MyPageProductQnADTO result = productQnARepository.findByQnAId(productQnA.getId());

        assertNotNull(result);
        assertEquals(productQnA.getId(), result.productQnAId());
        assertEquals(productQnA.getProduct().getProductName(), result.productName());
        assertEquals(productQnA.getQnaContent(), result.qnaContent());
    }

    @Test
    @DisplayName(value = "모든 상품 문의 조회")
    void findAllByAdminProductQnA() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);
        List<AdminQnAListResponseDTO> result = productQnARepository.findAllByAdminProductQnA(pageDTO);

        assertNotNull(result);
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "모든 상품 문의 조회. 검색")
    void findAllByAdminProductQnASearch() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "all", 1);
        List<AdminQnAListResponseDTO> result = productQnARepository.findAllByAdminProductQnA(pageDTO);


        assertNotNull(result);
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "모든 상품 문의 count")
    void findAllByAdminProductQnACount() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, "all", 1);
        Long result = productQnARepository.findAllByAdminProductQnACount(pageDTO);

        assertNotNull(result);
        assertEquals(qnaList.size(), result);
    }

    @Test
    @DisplayName(value = "모든 상품 문의 count. 검색")
    void findAllByAdminProductQnACountSearch() {
        List<ProductQnA> memberData = qnaList.stream()
                                            .filter(v ->
                                                    v.getMember().getUserId().equals(member.getUserId())
                                            )
                                            .toList();
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getUserId(), "all", 1);
        Long result = productQnARepository.findAllByAdminProductQnACount(pageDTO);


        assertNotNull(result);
        assertEquals(memberData.size(), result);
    }
}

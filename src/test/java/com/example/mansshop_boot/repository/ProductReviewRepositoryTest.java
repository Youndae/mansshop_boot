package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductReviewFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.MyPageReviewDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.AdminListType;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProductReviewRepositoryTest {

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
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductReviewReplyRepository productReviewReplyRepository;

    private static final int MEMBER_SIZE = 5;

    private static final int PRODUCT_SIZE = 30;

    private List<ProductReview> allProductReviewList;

    private List<ProductReview> completedProductReviewList;

    private List<ProductReviewReply> productReviewReplyList;

    private List<ProductReview> newProductReviewList;

    private Member member;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(MEMBER_SIZE);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        List<Member> memberFixtureList = memberAndAuthFixture.memberList();
        List<Auth> memberAuthFixtureList = memberAndAuthFixture.authList();
        Member adminData = adminFixture.memberList().get(0);
        memberFixtureList.add(adminData);
        memberAuthFixtureList.addAll(adminFixture.authList());

        List<Classification> classificationList = ClassificationFixture.createClassification();
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(PRODUCT_SIZE);
        List<ProductOption> productOptionFixtureList = productFixtureList.stream()
                .flatMap(v -> v.getProductOptions().stream())
                .toList();

        memberRepository.saveAll(memberFixtureList);
        authRepository.saveAll(memberAuthFixtureList);
        classificationRepository.saveAll(classificationList);
        productRepository.saveAll(productFixtureList);
        productOptionRepository.saveAll(productOptionFixtureList);

        List<ProductReview> completedReviewFixtureList = ProductReviewFixture.createReviewWithCompletedAnswer(memberFixtureList, productOptionFixtureList);
        productReviewRepository.saveAll(completedReviewFixtureList);
        List<ProductReviewReply> reviewReplyFixtureList = ProductReviewFixture.createDefaultReviewReply(completedReviewFixtureList, adminData);
        productReviewReplyRepository.saveAll(reviewReplyFixtureList);

        List<ProductReview> newReviewFixtureList = ProductReviewFixture.createDefaultReview(memberFixtureList, productOptionFixtureList);
        productReviewRepository.saveAll(newReviewFixtureList);

        completedProductReviewList = new ArrayList<>(completedReviewFixtureList);
        productReviewReplyList = reviewReplyFixtureList;
        newProductReviewList = newReviewFixtureList;

        completedReviewFixtureList.addAll(newReviewFixtureList);

        allProductReviewList = completedReviewFixtureList;
        member = memberFixtureList.get(0);
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 리뷰 검색")
    void findByProductId() {
        String productId = allProductReviewList.get(0).getProduct().getId();
        List<ProductReviewDTO> dataList = allProductReviewList.stream()
                                                        .filter(v -> v.getProduct().getId().equals(productId))
                                                        .sorted(Comparator.comparing(ProductReview::getId).reversed())
                                                        .map(v -> {
                                                            ProductReviewReply reply = productReviewReplyList.stream()
                                                                                                            .filter(r ->
                                                                                                                    r.getProductReview().getId() == v.getId()
                                                                                                            )
                                                                                                            .findFirst()
                                                                                                            .orElse(null);

                                                            String replyContent = null;
                                                            LocalDateTime replyCreatedAt = null;

                                                            if(reply != null) {
                                                                replyContent = reply.getReplyContent();
                                                                replyCreatedAt = reply.getCreatedAt();
                                                            }

                                                            return new ProductReviewDTO(v.getMember().getNickname(),
                                                                                        v.getReviewContent(),
                                                                                        v.getCreatedAt(),
                                                                                        replyContent,
                                                                                        replyCreatedAt
                                                                                );
                                                        })
                                                        .toList();
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                                    , pageDTO.reviewAmount()
                                                    , Sort.by("createdAt").descending()
                                            );
        Page<ProductReviewDTO> result = productReviewRepository.findByProductId(productId, pageable);

        assertNotNull(result);
        assertEquals(dataList.size(), result.getTotalElements());

        for(int i = 0; i < result.getContent().size(); i++) {
            ProductReviewDTO data = dataList.get(i);
            ProductReviewDTO resultData = result.getContent().get(i);

            assertEquals(data.reviewContent(), resultData.reviewContent());
            assertEquals(data.answerContent(), resultData.answerContent());
            assertEquals(data.answerCreatedAt(), resultData.answerCreatedAt());
        }
    }

    @Test
    @DisplayName(value = "상품 아이디 기반 리뷰 검색. 리뷰가 없는 경우")
    void findByProductIdEmpty() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.reviewAmount()
                , Sort.by("createdAt").descending()
        );
        Page<ProductReviewDTO> result = productReviewRepository.findByProductId("fackProductId", pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "사용자의 리뷰 목록 조회")
    void findAllByUserId() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        List<MyPageReviewDTO> dataList = allProductReviewList.stream()
                                                            .filter(v -> v.getMember().getUserId().equals(member.getUserId()))
                                                            .sorted(Comparator.comparing(ProductReview::getId).reversed())
                                                            .map(v -> {
                                                                ProductReviewReply reply = productReviewReplyList.stream()
                                                                                                        .filter(r ->
                                                                                                                r.getProductReview().getId().equals(v.getId())
                                                                                                        )
                                                                                                        .findFirst()
                                                                                                        .orElse(null);
                                                                String replyContent = null;
                                                                LocalDateTime replyUpdatedAt = null;
                                                                if(reply != null) {
                                                                    replyContent = reply.getReplyContent();
                                                                    replyUpdatedAt = reply.getUpdatedAt();
                                                                }

                                                                return new MyPageReviewDTO(v.getId(),
                                                                        v.getProduct().getThumbnail(),
                                                                        v.getProduct().getProductName(),
                                                                        v.getReviewContent(),
                                                                        v.getCreatedAt(),
                                                                        v.getUpdatedAt(),
                                                                        replyContent,
                                                                        replyUpdatedAt);
                                                            })
                                                            .toList();

        Page<MyPageReviewDTO> result = productReviewRepository.findAllByUserId(member.getUserId(), pageable);

        assertNotNull(result);
        assertEquals(dataList.size(), result.getTotalElements());

        for(int i = 0; i < result.getContent().size(); i++) {
            MyPageReviewDTO data = dataList.get(i);
            MyPageReviewDTO resultData = result.getContent().get(i);

            assertEquals(data.reviewId(), resultData.reviewId());
            assertEquals(data.productName(), resultData.productName());
            assertEquals(data.content(), resultData.content());
            assertEquals(data.replyContent(), resultData.replyContent());
            assertEquals(data.replyUpdatedAt(), resultData.replyUpdatedAt());
        }
    }

    @Test
    @DisplayName(value = "사용자의 리뷰 목록 조회. 데이터가 없는 경우")
    void findAllByUserIdEmpty() {
        MyPagePageDTO pageDTO = new MyPagePageDTO(1);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.amount()
                , Sort.by("id").descending());

        Page<MyPageReviewDTO> result = productReviewRepository.findAllByUserId("fakeUser", pageable);

        assertNotNull(result);
        assertEquals(0L, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName(value = "모든 리뷰 목록 조회")
    void findAllByAdminReviewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(pageDTO.amount(), result.size());
    }

    @Test
    @DisplayName(value = "모든 리뷰 목록 조회. 사용자 이름 || 닉네임 기반 검색")
    void findAllByAdminReviewListSearchUser() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getNickname(), "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(pageDTO.amount(), result.size());
        result.forEach(v -> assertEquals(member.getNickname(), v.writer()));
    }

    @Test
    @DisplayName(value = "모든 리뷰 목록 조회. 상품명 기반 검색")
    void findAllByAdminReviewListSearchProductName() {
        String productName = allProductReviewList.get(0).getProduct().getProductName();
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(productName, "product", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(pageDTO.amount(), result.size());
        result.forEach(v -> assertTrue(v.productName().contains(productName)));
    }

    @Test
    @DisplayName(value = "모든 리뷰 목록 조회. 사용자 이름 || 닉네임 기반 검색. 데이터가 없는 경우")
    void findAllByAdminReviewListSearchUserEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "모든 리뷰 목록 조회. 상품명 기반 검색. 데이터가 없는 경우")
    void findAllByAdminReviewListSearchProductNameEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeProduct", "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 목록 조회")
    void findAllNewByAdminReviewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(v -> assertFalse(v.status()));
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 목록 조회. 사용자 이름 || 닉네임 기반 검색")
    void findAllNewByAdminReviewListSearchUser() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getNickname(), "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(v -> {
            assertEquals(member.getNickname(), v.writer());
            assertFalse(v.status());
        });
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 목록 조회. 상품명 기반 검색")
    void findAllNewByAdminReviewListSearchProductName() {
        String productName = allProductReviewList.get(0).getProduct().getProductName();
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(productName, "product", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(v -> {
            assertTrue(v.productName().contains(productName));
            assertFalse(v.status());
        });
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 목록 조회. 사용자 이름 || 닉네임 기반 검색. 데이터가 없는 경우")
    void findAllNewByAdminReviewListSearchUserEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 목록 조회. 상품명 기반 검색. 데이터가 없는 경우")
    void findAllNewByAdminReviewListSearchProductNameEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeProduct", "user", 1);
        List<AdminReviewDTO> result = productReviewRepository.findAllByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "모든 리뷰 count")
    void countByAdminReviewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertEquals(allProductReviewList.size(), result);
    }

    @Test
    @DisplayName(value = "사용자 이름 || 닉네임 기반 모든 리뷰 count")
    void countByAdminReviewListSearchUser() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getNickname(), "user", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.ALL.name());
        Long count = allProductReviewList.stream().filter(v -> v.getMember().getNickname().equals(member.getNickname())).count();

        assertNotNull(result);
        assertEquals(result, count);
    }

    @Test
    @DisplayName(value = "사용자 이름 || 닉네임 기반 모든 리뷰 count. 데이터가 없는 경우")
    void countByAdminReviewListSearchUserEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "user", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "상품명 기반 모든 리뷰 count")
    void countByAdminReviewListSearchProduct() {
        String productName = allProductReviewList.get(0).getProduct().getProductName();
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(productName, "product", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.ALL.name());
        Long count = allProductReviewList.stream().filter(v -> v.getProduct().getProductName().contains(productName)).count();

        assertNotNull(result);
        assertEquals(count, result);
    }

    @Test
    @DisplayName(value = "상품명 기반 모든 리뷰 count. 데이터가 없는 경우")
    void countByAdminReviewListSearchProductEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeProductId", "product", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.ALL.name());

        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "모든 미처리 리뷰 count")
    void countByNewAdminReviewList() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertEquals(newProductReviewList.size(), result);
    }

    @Test
    @DisplayName(value = "사용자 이름 || 닉네임 기반 모든 미처리 리뷰 count")
    void countByNewAdminReviewListSearchUser() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(member.getNickname(), "user", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.NEW.name());
        Long count = newProductReviewList.stream().filter(v -> v.getMember().getNickname().equals(member.getNickname())).count();

        assertNotNull(result);
        assertEquals(count, result);
    }

    @Test
    @DisplayName(value = "사용자 이름 || 닉네임 기반 모든 미처리 리뷰 count. 데이터가 없는 경우")
    void countByNewAdminReviewListSearchUserEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeUser", "user", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "상품명 기반 모든 미처리 리뷰 count")
    void countByNewAdminReviewListSearchProduct() {
        String productName = allProductReviewList.get(0).getProduct().getProductName();
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(productName, "product", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.NEW.name());
        Long count = newProductReviewList.stream().filter(v -> v.getProduct().getProductName().contains(productName)).count();

        assertNotNull(result);
        assertEquals(count, result);
    }

    @Test
    @DisplayName(value = "상품명 기반 모든 미처리 리뷰 count. 데이터가 없는 경우")
    void countByNewAdminReviewListSearchProductEmpty() {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO("fakeProduct", "product", 1);
        Long result = productReviewRepository.countByAdminReviewList(pageDTO, AdminListType.NEW.name());

        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName(value = "리뷰 상세 데이터 조회")
    void findByAdminReviewDetail() {
        ProductReview productReview = completedProductReviewList.get(0);
        ProductReviewReply productReviewReply = productReviewReplyList.stream()
                                                                    .filter(v ->
                                                                            v.getProductReview().getId().equals(productReview.getId())
                                                                    )
                                                                    .findFirst()
                                                                    .orElse(null);

        AdminReviewDetailDTO result = productReviewRepository.findByAdminReviewDetail(productReview.getId());

        assertNotNull(result);
        assertEquals(productReview.getId(), result.reviewId());
        assertEquals(productReview.getProduct().getProductName(), result.productName());
        assertEquals(productReview.getProductOption().getSize(), result.size());
        assertEquals(productReview.getProductOption().getColor(), result.color());
        assertEquals(productReview.getMember().getNickname(), result.writer());
        assertEquals(productReview.getReviewContent(), result.content());
        assertEquals(productReviewReply.getReplyContent(), result.replyContent());
    }

    @Test
    @DisplayName(value = "리뷰 상세 데이터 조회. 데이터가 없는 경우")
    void findByAdminReviewDetailEmpty() {
        AdminReviewDetailDTO result = productReviewRepository.findByAdminReviewDetail(Long.MAX_VALUE);

        assertNull(result);
    }
}

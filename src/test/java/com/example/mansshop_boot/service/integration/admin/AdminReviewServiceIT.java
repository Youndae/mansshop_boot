package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.AdminListType;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.service.admin.AdminReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminReviewServiceIT {

    @Autowired
    private AdminReviewService adminReviewService;

    @Autowired
    private MemberRepository memberRepository;
    
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
    
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;
    
    private List<Member> memberList;
    
    private List<Product> productList;
    
    private List<ProductReview> newProductReviewList;
    
    private List<ProductReview> allProductReviewList;

    private List<ProductReviewReply> productReviewReplyList;

    private Member admin;
    
    private Principal principal;

    private static String SEARCH_TYPE_BY_USER = "user";

    private static String SEARCH_TYPE_BY_PRODUCT_NAME = "product";

    private static String REVIEW_CACHING_KEY = RedisCaching.ADMIN_REVIEW_COUNT.getKey();

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(10);
        memberList = memberAndAuthFixture.memberList();
        memberRepository.saveAll(memberList);
        
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        admin = adminFixture.memberList().get(0);
        memberRepository.save(admin);
        
        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);
        productList = ProductFixture.createSaveProductList(5, classificationList.get(0));
        List<ProductOption> optionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionList);
        
        newProductReviewList = ProductReviewFixture.createDefaultReview(memberList, optionList);
        List<ProductReview> answerCompleteReviewList = ProductReviewFixture.createReviewWithCompletedAnswer(memberList, optionList);
        allProductReviewList = new ArrayList<>(answerCompleteReviewList);
        allProductReviewList.addAll(newProductReviewList);
        
        productReviewRepository.saveAll(allProductReviewList);
        
        productReviewReplyList = ProductReviewFixture.createDefaultReviewReply(answerCompleteReviewList, admin);
        productReviewReplyRepository.saveAll(productReviewReplyList);
        
        principal = () -> admin.getUserId();
    }

    @Test
    @DisplayName(value = "전체 리뷰 리스트 조회")
    void getAllReviewList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        AdminListType listType = AdminListType.ALL;
        int totalPages = (int) Math.ceil((double) allProductReviewList.size() / pageDTO.amount());
        int resultContentSize = Math.min(allProductReviewList.size(), pageDTO.amount());
        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(allProductReviewList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNotNull(cachingResult);
        Assertions.assertEquals(allProductReviewList.size(), cachingResult);

        redisTemplate.delete(REVIEW_CACHING_KEY);
    }

    @Test
    @DisplayName(value = "전체 리뷰 리스트 조회. 데이터가 없는 경우")
    void getAllReviewListEmpty() {
        productReviewReplyRepository.deleteAll();
        productReviewRepository.deleteAll();
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        AdminListType listType = AdminListType.ALL;

        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "리뷰 리스트 조회. 사용자 이름 또는 닉네임 기반 검색")
    void getAllReviewListSearchUser() {
        Member searchMember = memberList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(
                searchMember.getUserName(),
                SEARCH_TYPE_BY_USER,
                1
        );
        AdminListType listType = AdminListType.ALL;
        List<ProductReview> reviewFixtureList = allProductReviewList.stream()
                .filter(v ->
                        v.getMember().getUserName().equals(searchMember.getUserName())
                )
                .toList();
        int totalPages = (int) Math.ceil((double) reviewFixtureList.size() / pageDTO.amount());
        int resultContentSize = Math.min(reviewFixtureList.size(), pageDTO.amount());

        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(reviewFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "리뷰 리스트 조회. 상품명 기반 검색")
    void getAllReviewListSearchProductName() {
        Product searchProduct = productList.get(0);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminOrderPageDTO(
                searchProduct.getProductName(),
                SEARCH_TYPE_BY_PRODUCT_NAME,
                1
        );
        AdminListType listType = AdminListType.ALL;
        List<ProductReview> reviewFixtureList = allProductReviewList.stream()
                .filter(v ->
                        v.getProduct().getProductName().equals(searchProduct.getProductName())
                )
                .toList();
        int totalPages = (int) Math.ceil((double) reviewFixtureList.size() / pageDTO.amount());
        int resultContentSize = Math.min(reviewFixtureList.size(), pageDTO.amount());

        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(reviewFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "미답변 리뷰 리스트 조회")
    void getNewReviewList() {
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        AdminListType listType = AdminListType.NEW;
        int totalPages = (int) Math.ceil((double) newProductReviewList.size() / pageDTO.amount());
        int resultContentSize = Math.min(newProductReviewList.size(), pageDTO.amount());
        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(resultContentSize, result.content().size());
        Assertions.assertEquals(newProductReviewList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(totalPages, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "미답변 리뷰 리스트 조회. 데이터가 없는 경우")
    void getNewReviewListEmpty() {
        List<Long> newReviewListIds = newProductReviewList.stream().map(ProductReview::getId).toList();
        productReviewRepository.deleteAllById(newReviewListIds);
        AdminOrderPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminOrderPageDTO();
        AdminListType listType = AdminListType.NEW;

        PagingListDTO<AdminReviewDTO> result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewList(pageDTO, listType));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());

        Long cachingResult = redisTemplate.opsForValue().get(REVIEW_CACHING_KEY);

        Assertions.assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "리뷰 상세 조회")
    void getReviewDetail() {
        ProductReview reviewFixture = allProductReviewList.stream().filter(ProductReview::isStatus).findFirst().get();
        ProductReviewReply reviewReply = productReviewReplyList.stream()
                .filter(v ->
                        v.getProductReview().getId().equals(reviewFixture.getId())
                )
                .findFirst()
                .get();
        AdminReviewDetailDTO result = Assertions.assertDoesNotThrow(() -> adminReviewService.getReviewDetail(reviewFixture.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(reviewFixture.getId(), result.reviewId());
        Assertions.assertEquals(reviewFixture.getProduct().getProductName(), result.productName());
        Assertions.assertEquals(reviewFixture.getProductOption().getSize(), result.size());
        Assertions.assertEquals(reviewFixture.getProductOption().getColor(), result.color());
        Assertions.assertEquals(reviewFixture.getMember().getNickname(), result.writer());
        Assertions.assertEquals(reviewFixture.getReviewContent(), result.content());
        Assertions.assertEquals(reviewReply.getReplyContent(), result.replyContent());
    }

    @Test
    @DisplayName(value = "리뷰 상세 조회. 데이터가 없는 경우")
    void getReviewDetailNotFound() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminReviewService.getReviewDetail(0L)
        );
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성")
    void postReviewReply() {
        ProductReview reviewFixture = allProductReviewList.stream().filter(v -> !v.isStatus()).findFirst().get();
        String content = "test Review Reply";
        AdminReviewRequestDTO postDTO = new AdminReviewRequestDTO(reviewFixture.getId(), content);
        String result = Assertions.assertDoesNotThrow(() -> adminReviewService.postReviewReply(postDTO, principal));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Result.OK.getResultKey(), result);

        ProductReview reviewEntity = productReviewRepository.findById(reviewFixture.getId()).orElse(null);

        Assertions.assertNotNull(reviewEntity);
        Assertions.assertTrue(reviewEntity.isStatus());

        AdminReviewDetailDTO checkDTO = adminReviewService.getReviewDetail(reviewFixture.getId());

        Assertions.assertNotNull(checkDTO);
        Assertions.assertEquals(content, checkDTO.replyContent());
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성. ProductReview 데이터가 없는 경우.")
    void postReviewReplyReviewNotFound() {
        String content = "test Review Reply";
        AdminReviewRequestDTO postDTO = new AdminReviewRequestDTO(0L, content);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> adminReviewService.postReviewReply(postDTO, principal)
        );
    }
}

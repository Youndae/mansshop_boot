package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.in.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    @Autowired
    private ProductInfoImageRepository productInfoImageRepository;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductReviewReplyRepository productReviewReplyRepository;

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private ProductQnAReplyRepository productQnAReplyRepository;

    private Member member;

    private List<Member> memberList;

    private Product product;

    private ProductLike productLike;

    private List<ProductReview> answerReviewList;

    private List<ProductReview> newReviewList;

    private List<ProductReview> allReviewList;

    private List<ProductReviewReply> reviewReplyList;

    private List<ProductQnA> answerProductQnAList;

    private List<ProductQnA> newProductQnAList;

    private List<ProductQnA> allProductQnAList;

    private List<ProductQnAReply> productQnAReplyList;

    private Principal principal;

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(10);
        memberList = memberAndAuthFixture.memberList();
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        Member admin = adminFixture.memberList().get(0);
        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.add(admin);
        memberRepository.saveAll(saveMemberList);
        member = memberList.get(0);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        product = ProductFixture.createSaveProductList(1, classificationList.get(0)).get(0);
        productRepository.save(product);
        productOptionRepository.saveAll(product.getProductOptions());
        productThumbnailRepository.saveAll(product.getProductThumbnails());
        productInfoImageRepository.saveAll(product.getProductInfoImages());

        productLike = ProductLikeFixture.createDefaultProductLike(List.of(member), List.of(product)).get(0);
        productLikeRepository.save(productLike);

        answerReviewList = ProductReviewFixture.createReviewWithCompletedAnswer(memberList, product.getProductOptions());
        newReviewList = ProductReviewFixture.createDefaultReview(List.of(member), product.getProductOptions());
        reviewReplyList = ProductReviewFixture.createDefaultReviewReply(answerReviewList, admin);

        allReviewList = new ArrayList<>(answerReviewList);
        allReviewList.addAll(newReviewList);
        productReviewRepository.saveAll(allReviewList);
        productReviewReplyRepository.saveAll(reviewReplyList);

        answerProductQnAList = ProductQnAFixture.createProductQnACompletedAnswer(memberList, List.of(product));
        newProductQnAList = ProductQnAFixture.createDefaultProductQnA(memberList, List.of(product));
        productQnAReplyList = ProductQnAFixture.createDefaultProductQnaReply(admin, answerProductQnAList);

        allProductQnAList = new ArrayList<>(answerProductQnAList);
        allProductQnAList.addAll(newProductQnAList);
        productQnARepository.saveAll(allProductQnAList);
        productQnAReplyRepository.saveAll(productQnAReplyList);

        principal = () -> member.getUserId();
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회. 로그인 상태고 해당 상품을 관심상품으로 등록한 경우")
    void getDetailLikeProduct() {
        int discountPrice = (int) (product.getProductPrice() * (1 - ((double) product.getProductDiscount() / 100)));
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        int reviewContentSize = Math.min(allReviewList.size(), pageDTO.reviewAmount());
        int reviewTotalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.reviewAmount());
        int qnaContentSize = Math.min(allProductQnAList.size(), pageDTO.qnaAmount());
        int qnaTotalPages = PaginationUtils.getTotalPages(allProductQnAList.size(), pageDTO.qnaAmount());
        List<ProductOption> options = product.getProductOptions()
                .stream()
                .filter(ProductOption::isOpen)
                .toList();

        ProductDetailDTO result = assertDoesNotThrow(() -> productService.getDetail(product.getId(), principal));

        assertNotNull(result);
        assertEquals(product.getId(), result.productId());
        assertEquals(product.getProductName(), result.productName());
        assertEquals(product.getProductPrice(), result.productPrice());
        assertEquals(product.getThumbnail(), result.productImageName());
        assertTrue(result.likeStat());
        assertEquals(product.getProductDiscount(), result.discount());
        assertEquals(discountPrice, result.discountPrice());
        assertEquals(options.size(), result.productOptionList().size());
        assertEquals(product.getProductThumbnails().size(), result.productThumbnailList().size());
        for(int i = 0; i < product.getProductThumbnails().size(); i++) {
            String thumbnailName = product.getProductThumbnails().get(i).getImageName();
            String resultThumbnailName = result.productThumbnailList().get(i);

            assertEquals(thumbnailName, resultThumbnailName);
        }
        assertEquals(product.getProductInfoImages().size(), result.productInfoImageList().size());
        for(int i = 0; i < product.getProductInfoImages().size(); i++) {
            String infoImageName = product.getProductInfoImages().get(i).getImageName();
            String resultInfoImageName = result.productInfoImageList().get(i);

            assertEquals(infoImageName, resultInfoImageName);
        }
        assertFalse(result.productReviewList().empty());
        assertFalse(result.productReviewList().content().isEmpty());
        assertEquals(reviewContentSize, result.productReviewList().content().size());
        assertEquals(reviewTotalPages, result.productReviewList().totalPages());
        assertEquals(allReviewList.size(), result.productReviewList().totalElements());

        assertFalse(result.productQnAList().empty());
        assertFalse(result.productQnAList().content().isEmpty());
        assertEquals(qnaContentSize, result.productQnAList().content().size());
        assertEquals(qnaTotalPages, result.productQnAList().totalPages());
        assertEquals(allProductQnAList.size(), result.productQnAList().totalElements());
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회. 로그인 상태고 해당 상품을 관심상품으로 등록하지 않은 경우")
    void getDetailDeLikeProduct() {
        int discountPrice = (int) (product.getProductPrice() * (1 - ((double) product.getProductDiscount() / 100)));
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        int reviewContentSize = Math.min(allReviewList.size(), pageDTO.reviewAmount());
        int reviewTotalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.reviewAmount());
        int qnaContentSize = Math.min(allProductQnAList.size(), pageDTO.qnaAmount());
        int qnaTotalPages = PaginationUtils.getTotalPages(allProductQnAList.size(), pageDTO.qnaAmount());
        List<ProductOption> options = product.getProductOptions()
                .stream()
                .filter(ProductOption::isOpen)
                .toList();
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();

        ProductDetailDTO result = assertDoesNotThrow(() -> productService.getDetail(product.getId(), fixturePrincipal));


        assertNotNull(result);
        assertEquals(product.getId(), result.productId());
        assertEquals(product.getProductName(), result.productName());
        assertEquals(product.getProductPrice(), result.productPrice());
        assertEquals(product.getThumbnail(), result.productImageName());
        assertFalse(result.likeStat());
        assertEquals(product.getProductDiscount(), result.discount());
        assertEquals(discountPrice, result.discountPrice());
        assertEquals(options.size(), result.productOptionList().size());
        assertEquals(product.getProductThumbnails().size(), result.productThumbnailList().size());
        for(int i = 0; i < product.getProductThumbnails().size(); i++) {
            String thumbnailName = product.getProductThumbnails().get(i).getImageName();
            String resultThumbnailName = result.productThumbnailList().get(i);

            assertEquals(thumbnailName, resultThumbnailName);
        }
        assertEquals(product.getProductInfoImages().size(), result.productInfoImageList().size());
        for(int i = 0; i < product.getProductInfoImages().size(); i++) {
            String infoImageName = product.getProductInfoImages().get(i).getImageName();
            String resultInfoImageName = result.productInfoImageList().get(i);

            assertEquals(infoImageName, resultInfoImageName);
        }
        assertFalse(result.productReviewList().empty());
        assertFalse(result.productReviewList().content().isEmpty());
        assertEquals(reviewContentSize, result.productReviewList().content().size());
        assertEquals(reviewTotalPages, result.productReviewList().totalPages());
        assertEquals(allReviewList.size(), result.productReviewList().totalElements());

        assertFalse(result.productQnAList().empty());
        assertFalse(result.productQnAList().content().isEmpty());
        assertEquals(qnaContentSize, result.productQnAList().content().size());
        assertEquals(qnaTotalPages, result.productQnAList().totalPages());
        assertEquals(allProductQnAList.size(), result.productQnAList().totalElements());
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회. 비회원인 경우")
    void getDetailAnonymous() {
        int discountPrice = (int) (product.getProductPrice() * (1 - ((double) product.getProductDiscount() / 100)));
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        int reviewContentSize = Math.min(allReviewList.size(), pageDTO.reviewAmount());
        int reviewTotalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.reviewAmount());
        int qnaContentSize = Math.min(allProductQnAList.size(), pageDTO.qnaAmount());
        int qnaTotalPages = PaginationUtils.getTotalPages(allProductQnAList.size(), pageDTO.qnaAmount());
        List<ProductOption> options = product.getProductOptions()
                .stream()
                .filter(ProductOption::isOpen)
                .toList();

        ProductDetailDTO result = assertDoesNotThrow(() -> productService.getDetail(product.getId(), null));

        assertNotNull(result);
        assertEquals(product.getId(), result.productId());
        assertEquals(product.getProductName(), result.productName());
        assertEquals(product.getProductPrice(), result.productPrice());
        assertEquals(product.getThumbnail(), result.productImageName());
        assertFalse(result.likeStat());
        assertEquals(product.getProductDiscount(), result.discount());
        assertEquals(discountPrice, result.discountPrice());
        assertEquals(options.size(), result.productOptionList().size());
        assertEquals(product.getProductThumbnails().size(), result.productThumbnailList().size());
        for(int i = 0; i < product.getProductThumbnails().size(); i++) {
            String thumbnailName = product.getProductThumbnails().get(i).getImageName();
            String resultThumbnailName = result.productThumbnailList().get(i);

            assertEquals(thumbnailName, resultThumbnailName);
        }
        assertEquals(product.getProductInfoImages().size(), result.productInfoImageList().size());
        for(int i = 0; i < product.getProductInfoImages().size(); i++) {
            String infoImageName = product.getProductInfoImages().get(i).getImageName();
            String resultInfoImageName = result.productInfoImageList().get(i);

            assertEquals(infoImageName, resultInfoImageName);
        }
        assertFalse(result.productReviewList().empty());
        assertFalse(result.productReviewList().content().isEmpty());
        assertEquals(reviewContentSize, result.productReviewList().content().size());
        assertEquals(reviewTotalPages, result.productReviewList().totalPages());
        assertEquals(allReviewList.size(), result.productReviewList().totalElements());

        assertFalse(result.productQnAList().empty());
        assertFalse(result.productQnAList().content().isEmpty());
        assertEquals(qnaContentSize, result.productQnAList().content().size());
        assertEquals(qnaTotalPages, result.productQnAList().totalPages());
        assertEquals(allProductQnAList.size(), result.productQnAList().totalElements());
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회. 상품 아이디가 잘못 된 경우")
    void getDetailWrongId() {
        assertThrows(
                CustomNotFoundException.class,
                () -> productService.getDetail("noneProductId", null)
        );
    }

    @Test
    @DisplayName(value = "리뷰 리스트 조회")
    void getDetailReview() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        int contentSize = Math.min(allReviewList.size(), pageDTO.reviewAmount());
        int totalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.reviewAmount());

        Page<ProductReviewDTO> result = assertDoesNotThrow(() -> productService.getDetailReview(pageDTO, product.getId()));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(contentSize, result.getContent().size());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(allReviewList.size(), result.getTotalElements());
    }

    @Test
    @DisplayName(value = "리뷰 리스트 조회. 데이터가 없는 경우")
    void getDetailReviewEmpty() {
        productReviewRepository.deleteAll();
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Page<ProductReviewDTO> result = assertDoesNotThrow(() -> productService.getDetailReview(pageDTO, product.getId()));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 문의 리스트 조회")
    void getDetailQnA() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        int contentSize = Math.min(allProductQnAList.size(), pageDTO.qnaAmount());
        int totalPages = PaginationUtils.getTotalPages(allProductQnAList.size(), pageDTO.qnaAmount());

        Page<ProductQnAResponseDTO> result = assertDoesNotThrow(() -> productService.getDetailQnA(pageDTO, product.getId()));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertFalse(result.isEmpty());
        assertEquals(contentSize, result.getContent().size());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(allProductQnAList.size(), result.getTotalElements());
    }

    @Test
    @DisplayName(value = "상품 문의 리스트 조회. 데이터가 없는 경우")
    void getDetailQnAEmpty() {
        productQnARepository.deleteAll();
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Page<ProductQnAResponseDTO> result = assertDoesNotThrow(() -> productService.getDetailQnA(pageDTO, product.getId()));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName(value = "관심상품 등록")
    void likeProduct() {
        Map<String, String> productIdMap = new HashMap<>();
        productIdMap.put("productId", product.getId());
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();

        String result = assertDoesNotThrow(() -> productService.likeProduct(productIdMap, fixturePrincipal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductLike saveProductLike = productLikeRepository.findByMember_UserId(fixturePrincipal.getName()).get(0);

        assertNotNull(saveProductLike);
    }

    @Test
    @DisplayName(value = "관심상품 등록. Map 데이터가 없는 경우 ")
    void likeProductWrongProductIdMap() {
        Map<String, String> productIdMap = new HashMap<>();
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.likeProduct(productIdMap, fixturePrincipal)
        );

        List<ProductLike> productLikeList = productLikeRepository.findByMember_UserId(fixturePrincipal.getName());

        assertTrue(productLikeList.isEmpty());
    }

    @Test
    @DisplayName(value = "관심상품 등록. Principal이 null인 경우")
    void likeProductPrincipalIsNull() {
        Map<String, String> productIdMap = new HashMap<>();
        productIdMap.put("productId", product.getId());

        assertThrows(
                CustomAccessDeniedException.class,
                () -> productService.likeProduct(productIdMap, null)
        );
    }

    @Test
    @DisplayName(value = "관심상품 등록. 상품 아이디가 잘못 된 경우")
    void likeProductWrongProductId() {
        Map<String, String> productIdMap = new HashMap<>();
        productIdMap.put("productId", "noneProductId");
        Principal fixturePrincipal = () -> memberList.get(1).getUserId();

        assertThrows(
                CustomNotFoundException.class,
                () -> productService.likeProduct(productIdMap, fixturePrincipal)
        );

        List<ProductLike> productLikeList = productLikeRepository.findByMember_UserId(fixturePrincipal.getName());

        assertTrue(productLikeList.isEmpty());
    }

    @Test
    @DisplayName(value = "관심상품 해제")
    void deLikeProduct() {
        String productId = productLike.getProduct().getId();

        String result = assertDoesNotThrow(() -> productService.deLikeProduct(productId, principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<ProductLike> productLikeList = productLikeRepository.findByMember_UserId(principal.getName());

        assertTrue(productLikeList.isEmpty());
    }

    @Test
    @DisplayName(value = "관심상품 해제. Principal이 null인 경우")
    void deLikeProductPrincipalIsNull() {
        String productId = productLike.getProduct().getId();

        assertThrows(
                CustomAccessDeniedException.class,
                () -> productService.deLikeProduct(productId, null)
        );

        List<ProductLike> productLikeList = productLikeRepository.findByMember_UserId(principal.getName());

        assertFalse(productLikeList.isEmpty());
    }

    @Test
    @DisplayName(value = "관심상품 해제. 상품 아이디가 잘못 된 경우")
    void deLikeProductWrongId() {
        assertThrows(
                CustomNotFoundException.class,
                () -> productService.deLikeProduct("noneProductId", principal)
        );

        List<ProductLike> productLikeList = productLikeRepository.findByMember_UserId(principal.getName());

        assertFalse(productLikeList.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 문의 작성")
    void postProductQnA() {
        ProductQnAPostDTO postDTO = new ProductQnAPostDTO(product.getId(), "test post product QnA content");

        String result = assertDoesNotThrow(() -> productService.postProductQnA(postDTO, principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        ProductQnA saveList = productQnARepository.findAllByMember_UserIdOrderByIdDesc(principal.getName()).get(0);

        assertNotNull(saveList);
        assertEquals(postDTO.content(), saveList.getQnaContent());
    }

    @Test
    @DisplayName(value = "상품 문의 작성. 상품 아이디가 잘못 된 경우")
    void postProductQnAWrongProductId() {
        ProductQnAPostDTO postDTO = new ProductQnAPostDTO("noneProductId", "test post product QnA content");

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.postProductQnA(postDTO, principal)
        );
    }
}

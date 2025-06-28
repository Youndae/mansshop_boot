package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.ExceptionEntity;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.util.PaginationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AdminReviewControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private TokenFixture tokenFixture;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisTemplate<String, Long> cacheRedisTemplate;

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

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    private Map<String, String> tokenMap;

    private String accessTokenValue;

    private String refreshTokenValue;

    private String inoValue;

    private Member admin;

    private Member member;

    private List<ProductReview> answerCompleteReviewList;

    private List<ProductReview> newReviewList;

    private List<ProductReview> allReviewList;

    private List<ProductReviewReply> reviewReplyList;

    private Product product;

    private static final String REVIEW_CACHING_KEY = RedisCaching.ADMIN_REVIEW_COUNT.getKey();

    private static final String URL_PREFIX = "/api/admin/";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(10);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        List<Member> memberList = memberAndAuthFixtureDTO.memberList();
        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.addAll(adminFixture.memberList());
        List<Auth> saveAuthList = new ArrayList<>(adminFixture.authList());
        saveAuthList.addAll(adminFixture.authList());
        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);

        member = memberList.get(0);
        admin = adminFixture.memberList().get(0);

        tokenMap = tokenFixture.createAndSaveAllToken(admin);
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        List<Product> productList = ProductFixture.createSaveProductList(10, classificationList.get(0));
        List<ProductOption> productOptionList = productList.stream()
                                            .flatMap(v ->
                                                    v.getProductOptions().stream()
                                            )
                                            .toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(productOptionList);
        product = productList.get(0);

        answerCompleteReviewList = ProductReviewFixture.createReviewWithCompletedAnswer(memberList, productOptionList);
        newReviewList = ProductReviewFixture.createDefaultReview(memberList, productOptionList);
        allReviewList = new ArrayList<>(answerCompleteReviewList);
        allReviewList.addAll(newReviewList);
        productReviewRepository.saveAll(allReviewList);
        reviewReplyList = ProductReviewFixture.createDefaultReviewReply(answerCompleteReviewList, admin);
        productReviewReplyRepository.saveAll(reviewReplyList);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUP() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);

        cacheRedisTemplate.delete(REVIEW_CACHING_KEY);
    }

    @Test
    @DisplayName(value = "미처리 리뷰 목록 조회")
    void getNewReviewList() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> fixture = newReviewList.stream()
                .limit(pageDTO.amount())
                .toList();
        int contentSize = Math.min(fixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(newReviewList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "미처리 리뷰 목록 조회. 데이터가 없는 경우")
    void getNewReviewListEmpty() throws Exception {
        productReviewRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "미처리 리뷰 목록 조회. 상품명 기반 검색")
    void getNewReviewListSearchProductName() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> filterFixtureList = newReviewList.stream()
                                                .filter(v ->
                                                        v.getProduct().getProductName().contains(product.getProductName())
                                                )
                                                .toList();
        List<ProductReview> contentFixture = filterFixtureList.stream()
                                            .limit(pageDTO.amount())
                                            .toList();
        int contentSize = Math.min(contentFixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(filterFixtureList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", product.getProductName())
                        .param("searchType", "product"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "미처리 리뷰 목록 조회. 사용자 이름 또는 닉네임 기반 검색")
    void getNewReviewListSearchUser() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> filterFixtureList = newReviewList.stream()
                .filter(v ->
                        v.getMember().getNickname().contains(member.getNickname()) ||
                                v.getMember().getUserName().contains(member.getNickname())
                )
                .toList();
        List<ProductReview> contentFixture = filterFixtureList.stream()
                .limit(pageDTO.amount())
                .toList();
        int contentSize = Math.min(contentFixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(filterFixtureList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", member.getNickname())
                        .param("searchType", "user"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "전체 리뷰 목록 조회")
    void getAllReviewList() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> fixture = allReviewList.stream()
                .limit(pageDTO.amount())
                .toList();
        int contentSize = Math.min(fixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(REVIEW_CACHING_KEY);
        assertNotNull(cachingResult);
        assertEquals(allReviewList.size(), cachingResult);
    }

    @Test
    @DisplayName(value = "전체 리뷰 목록 조회. 데이터가 없는 경우")
    void getAllReviewListEmpty() throws Exception {
        productReviewRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(REVIEW_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "전체 리뷰 목록 조회. 상품명 기반 검색")
    void getAllReviewListSearchProductName() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> filterFixtureList = allReviewList.stream()
                .filter(v ->
                        v.getProduct().getProductName().contains(product.getProductName())
                )
                .toList();
        List<ProductReview> contentFixture = filterFixtureList.stream()
                .limit(pageDTO.amount())
                .toList();
        int contentSize = Math.min(contentFixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(filterFixtureList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", product.getProductName())
                        .param("searchType", "product"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(REVIEW_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "전체 리뷰 목록 조회. 사용자 이름 또는 닉네임 기반 검색")
    void getAllReviewListSearchUser() throws Exception {
        AdminOrderPageDTO pageDTO = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        List<ProductReview> filterFixtureList = allReviewList.stream()
                .filter(v ->
                        v.getMember().getNickname().contains(member.getNickname()) ||
                                v.getMember().getUserName().contains(member.getNickname())
                )
                .toList();
        List<ProductReview> contentFixture = filterFixtureList.stream()
                .limit(pageDTO.amount())
                .toList();
        int contentSize = Math.min(contentFixture.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(filterFixtureList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", member.getNickname())
                        .param("searchType", "user"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminReviewDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(REVIEW_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "답변 완료된 리뷰 상세 조회")
    void getAnswerReviewDetail() throws Exception {
        ProductReview fixture = answerCompleteReviewList.get(0);
        ProductReviewReply replyFixture = reviewReplyList.stream()
                                    .filter(v ->
                                            v.getProductReview().getId().equals(fixture.getId())
                                    )
                                    .findFirst()
                                    .get();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/detail/" + fixture.getId())
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        AdminReviewDetailDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.reviewId());
        assertEquals(fixture.getProduct().getProductName(), response.productName());
        assertEquals(fixture.getProductOption().getSize(), response.size());
        assertEquals(fixture.getProductOption().getColor(), response.color());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getReviewContent(), response.content());
        assertEquals(replyFixture.getReplyContent(), response.replyContent());
    }

    @Test
    @DisplayName(value = "미답변의 리뷰 상세 조회")
    void getNewReviewDetail() throws Exception {
        ProductReview fixture = newReviewList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/detail/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        AdminReviewDetailDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.reviewId());
        assertEquals(fixture.getProduct().getProductName(), response.productName());
        assertEquals(fixture.getProductOption().getSize(), response.size());
        assertEquals(fixture.getProductOption().getColor(), response.color());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getReviewContent(), response.content());
        assertNull(response.replyUpdatedAt());
        assertNull(response.replyContent());
    }

    @Test
    @DisplayName(value = "리뷰 상세 조회. 리뷰 아이디가 잘못된 경우")
    void getReviewDetailWrongId() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/detail/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성")
    void postReviewReply() throws Exception {
        ProductReview fixture = newReviewList.get(0);
        AdminReviewRequestDTO insertDTO = new AdminReviewRequestDTO(fixture.getId(), "test insert review reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestDTO))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        ProductReview patchData = productReviewRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertTrue(patchData.isStatus());

        ProductReviewReply saveReply = productReviewReplyRepository.findByReviewId(fixture.getId());
        assertNotNull(saveReply);
        assertEquals(insertDTO.content(), saveReply.getReplyContent());
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성. 리뷰 아이디가 잘못된 경우")
    void postReviewReplyWrongId() throws Exception {
        AdminReviewRequestDTO insertDTO = new AdminReviewRequestDTO(0L, "test insert review reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 답변 작성. 이미 답변이 작성된 리뷰인 경우")
    void postReviewReplyAlreadyExist() throws Exception {
        ProductReview fixture = answerCompleteReviewList.get(0);
        AdminReviewRequestDTO insertDTO = new AdminReviewRequestDTO(fixture.getId(), "test insert review reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());

        ProductReview patchData = productReviewRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertTrue(patchData.isStatus());

        ProductReviewReply originReply = productReviewReplyRepository.findByReviewId(fixture.getId());
        assertNotNull(originReply);
        assertNotEquals(insertDTO.content(), originReply.getReplyContent());
    }
}

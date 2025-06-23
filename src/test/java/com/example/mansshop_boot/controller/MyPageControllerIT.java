package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.ExceptionEntity;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.MailSuffix;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MyPageControllerIT {

    @Autowired
    private WebApplicationContext context;

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
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductReviewReplyRepository productReviewReplyRepository;

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Autowired
    private QnAClassificationRepository qnAClassificationRepository;

    @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    private Member member;

    private Member admin;

    private Member noneMember;

    private Map<String, String> tokenMap;

    private String accessTokenValue;

    private String refreshTokenValue;

    private String inoValue;

    private List<ProductLike> productLikeList;

    private List<ProductOrder> productOrderList;

    private List<ProductReviewReply> reviewReplyList;

    private List<ProductReview> newReviewList;

    private List<ProductReview> allReviewList;

    private ProductReview noneMemberReview;

    private List<ProductQnAReply> productQnAReplyList;

    private List<ProductQnA> newProductQnAList;

    private List<ProductQnA> allProductQnAList;

    private ProductQnA noneMemberProductQnA;

    private List<QnAClassification> qnAClassificationList;

    private List<MemberQnAReply> memberQnAReplyList;

    private List<MemberQnA> newMemberQnAList;

    private List<MemberQnA> allMemberQnAList;

    private MemberQnA noneMemberQnA;

    private static final String URL_PREFIX = "/api/my-page/";

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(2);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        List<Member> saveMemberList = new ArrayList<>(memberAndAuthFixtureDTO.memberList());
        saveMemberList.addAll(adminFixture.memberList());
        List<Auth> saveAuthList = new ArrayList<>(memberAndAuthFixtureDTO.authList());
        saveAuthList.addAll(adminFixture.authList());
        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);
        member = memberAndAuthFixtureDTO.memberList().get(0);
        noneMember = memberAndAuthFixtureDTO.memberList().get(1);
        admin = adminFixture.memberList().get(0);

        tokenMap = tokenFixture.createAndSaveAllToken(member);
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        List<Product> productList = ProductFixture.createSaveProductList(30, classificationList.get(0));
        List<ProductOption> productOptionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(productOptionList);

        productLikeList = ProductLikeFixture.createDefaultProductLike(List.of(member), productList);
        productLikeRepository.saveAll(productLikeList);

        productOrderList = ProductOrderFixture.createDefaultProductOrder(List.of(member), productOptionList);
        productOrderRepository.saveAll(productOrderList);

        List<ProductReview> completeAnswerReviewList = ProductReviewFixture.createReviewWithCompletedAnswer(List.of(member), productOptionList);
        noneMemberReview = ProductReviewFixture.createDefaultReview(List.of(noneMember), List.of(productOptionList.get(0))).get(0);
        reviewReplyList = ProductReviewFixture.createDefaultReviewReply(completeAnswerReviewList, admin);
        newReviewList = ProductReviewFixture.createDefaultReview(List.of(member), productOptionList);
        allReviewList = new ArrayList<>(completeAnswerReviewList);
        allReviewList.addAll(newReviewList);
        productReviewRepository.save(noneMemberReview);
        productReviewRepository.saveAll(allReviewList);
        productReviewReplyRepository.saveAll(reviewReplyList);


        List<ProductQnA> completeAnswerProductQnAList = ProductQnAFixture.createProductQnACompletedAnswer(List.of(member), productList);
        noneMemberProductQnA = ProductQnAFixture.createDefaultProductQnA(List.of(noneMember), List.of(productList.get(0))).get(0);
        productQnAReplyList = ProductQnAFixture.createDefaultProductQnaReply(admin, completeAnswerProductQnAList);
        newProductQnAList = ProductQnAFixture.createDefaultProductQnA(List.of(member), productList);
        allProductQnAList = new ArrayList<>(completeAnswerProductQnAList);
        allProductQnAList.addAll(newProductQnAList);
        productQnARepository.save(noneMemberProductQnA);
        productQnARepository.saveAll(allProductQnAList);
        productQnAReplyRepository.saveAll(productQnAReplyList);

        qnAClassificationList = QnAClassificationFixture.createQnAClassificationList();
        qnAClassificationRepository.saveAll(qnAClassificationList);

        List<MemberQnA> completeAnswerMemberQnAList = MemberQnAFixture.createMemberQnACompletedAnswer(qnAClassificationList, List.of(member));
        noneMemberQnA = MemberQnAFixture.createDefaultMemberQnA(qnAClassificationList, List.of(noneMember)).get(0);
        memberQnAReplyList = MemberQnAFixture.createMemberQnAReply(completeAnswerMemberQnAList, admin);
        newMemberQnAList = MemberQnAFixture.createDefaultMemberQnA(qnAClassificationList, List.of(member));
        allMemberQnAList = new ArrayList<>(completeAnswerMemberQnAList);
        allMemberQnAList.addAll(newMemberQnAList);
        memberQnARepository.save(noneMemberQnA);
        memberQnARepository.saveAll(allMemberQnAList);
        memberQnAReplyRepository.saveAll(memberQnAReplyList);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUp() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);
    }

    @Test
    @DisplayName(value = "회원 주문 내역 조회")
    void getOrderList() throws Exception {
        OrderPageDTO pageDTO = PageDTOFixture.createDefaultOrderPageDTO("3");
        int totalPages = PaginationUtils.getTotalPages(productOrderList.size(), pageDTO.orderAmount());
        int contentSize = Math.min(productOrderList.size(), pageDTO.orderAmount());
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/3")
                .header(accessHeader, accessTokenValue)
                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<MyPageOrderDTO> response = om.readValue(
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
    @DisplayName(value = "회원 주문 내역 조회. 데이터가 없는 경우")
    void getOrderListEmpty() throws Exception {
        productOrderRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/3")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<MyPageOrderDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 관심 상품 목록 조회")
    void getLikeProductList() throws Exception {
        LikePageDTO pageDTO = PageDTOFixture.createDefaultLikePageDTO(1);
        int totalPages = PaginationUtils.getTotalPages(productLikeList.size(), pageDTO.likeAmount());
        int contentSize = Math.min(productLikeList.size(), pageDTO.likeAmount());
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "like")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductLikeDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 관심 상품 목록 조회. 데이터가 없는 경우")
    void getLikeProductListEmpty() throws Exception {
        productLikeRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "like")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductLikeDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 목록 조회")
    void getProductQnAList() throws Exception {
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        int totalPages = PaginationUtils.getTotalPages(allProductQnAList.size(), pageDTO.amount());
        int contentSize = Math.min(allProductQnAList.size(), pageDTO.amount());
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product")
                                            .header(accessHeader, accessTokenValue)
                                            .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                            .cookie(new Cookie(inoHeader, inoValue)))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductQnAListDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 목록 조회. 데이터가 없는 경우")
    void getProductQnAListEmpty() throws Exception {
        productQnARepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductQnAListDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 미답변 상품 문의 상세 조회")
    void getProductQnADetail() throws Exception {
        ProductQnA fixture = newProductQnAList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product/detail/" + fixture.getId())
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ProductQnADetailDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.productQnAId());
        assertEquals(fixture.getProduct().getProductName(), response.productName());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getQnaContent(), response.qnaContent());
        assertFalse(response.productQnAStat());
        assertTrue(response.replyList().isEmpty());
    }

    @Test
    @DisplayName(value = "회원의 답변 완료 상품 문의 상세 조회")
    void getCompleteProductQnADetail() throws Exception {
        ProductQnAReply replyFixture = productQnAReplyList.get(0);
        ProductQnA fixture = replyFixture.getProductQnA();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product/detail/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ProductQnADetailDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.productQnAId());
        assertEquals(fixture.getProduct().getProductName(), response.productName());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getQnaContent(), response.qnaContent());
        assertTrue(response.productQnAStat());
        assertFalse(response.replyList().isEmpty());

        MyPageQnAReplyDTO responseReply = response.replyList().get(0);

        assertEquals(replyFixture.getId(), responseReply.replyId());
        assertEquals(replyFixture.getMember().getNickname(), responseReply.writer());
        assertEquals(replyFixture.getReplyContent(), responseReply.replyContent());
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 상세 조회. 작성자가 일치하지 않는 경우")
    void getProductQnADetailWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product/detail/" + noneMemberProductQnA.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 상세 조회. 데이터가 없는 경우")
    void getProductQnADetailNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/product/detail/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 삭제")
    void deleteProductQnA() throws Exception {
        Long fixtureId = allProductQnAList.get(0).getId();

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/product/" + fixtureId)
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        ProductQnA deleteData = productQnARepository.findById(fixtureId).orElse(null);
        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 삭제. 작성자가 일치하지 않는 경우")
    void deleteProductQnAWriterNotEquals() throws Exception {
        Long fixtureId = noneMemberProductQnA.getId();
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/product/" + fixtureId)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());

        ProductQnA deleteData = productQnARepository.findById(fixtureId).orElse(null);
        assertNotNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원의 상품 문의 삭제. 데이터가 없는 경우")
    void deleteProductQnANotFound() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/product/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원의 문의 목록 조회")
    void getMemberQnA() throws Exception {
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        int totalPages = PaginationUtils.getTotalPages(allMemberQnAList.size(), pageDTO.amount());
        int contentSize = Math.min(allMemberQnAList.size(), pageDTO.amount());
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductQnAListDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원의 문의 목록 조회. 데이터가 없는 경우")
    void getMemberQnAEmpty() throws Exception {
        memberQnARepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<ProductQnAListDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "회원 문의 작성")
    void postMemberQnA() throws Exception {
        MemberQnAInsertDTO insertDTO = new MemberQnAInsertDTO(
                "test insert title",
                "test insert content",
                qnAClassificationList.get(0).getId()
        );
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "qna/member")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseIdDTO<Long> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);

        Long saveId = response.id();

        MemberQnA saveEntity = memberQnARepository.findById(saveId).orElse(null);
        assertNotNull(saveEntity);
        assertEquals(insertDTO.title(), saveEntity.getMemberQnATitle());
        assertEquals(insertDTO.content(), saveEntity.getMemberQnAContent());
        assertEquals(insertDTO.classificationId(), saveEntity.getQnAClassification().getId());
        assertEquals(member.getUserId(), saveEntity.getMember().getUserId());
    }

    @Test
    @DisplayName(value = "회원 문의 작성. 분류 아이디가 잘못된 경우")
    void postMemberQnAWrongQnAClassificationId() throws Exception {
        MemberQnAInsertDTO insertDTO = new MemberQnAInsertDTO(
                "test insert title",
                "test insert content",
                0L
        );
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "답변 완료 회원 문의 상세 조회")
    void getMemberQnADetail() throws Exception {
        MemberQnA fixture = allMemberQnAList.stream()
                                        .filter(MemberQnA::isMemberQnAStat)
                                        .findFirst()
                                        .get();
        List<MemberQnAReply> replyFixture = memberQnAReplyList.stream()
                                                    .filter(v ->
                                                            v.getMemberQnA().getId().equals(fixture.getId())
                                                    )
                                                    .toList();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/detail/" + fixture.getId())
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        MemberQnADetailDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.memberQnAId());
        assertEquals(fixture.getQnAClassification().getQnaClassificationName(), response.qnaClassification());
        assertEquals(fixture.getMemberQnATitle(), response.qnaTitle());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getMemberQnAContent(), response.qnaContent());
        assertEquals(fixture.isMemberQnAStat(), response.memberQnAStat());
        assertEquals(replyFixture.size(), response.replyList().size());

        for(MyPageQnAReplyDTO responseReply : response.replyList()) {
            for(MemberQnAReply reply : replyFixture) {
                if(responseReply.replyId() == reply.getId()) {
                    assertEquals(reply.getMember().getNickname(), responseReply.writer());
                    assertEquals(reply.getReplyContent(), responseReply.replyContent());
                }
            }
        }
    }

    @Test
    @DisplayName(value = "미답변 회원 문의 상세 조회")
    void getNewMemberQnADetail() throws Exception {
        MemberQnA fixture = newMemberQnAList.get(0);
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/detail/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        MemberQnADetailDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.memberQnAId());
        assertEquals(fixture.getQnAClassification().getQnaClassificationName(), response.qnaClassification());
        assertEquals(fixture.getMemberQnATitle(), response.qnaTitle());
        assertEquals(fixture.getMember().getNickname(), response.writer());
        assertEquals(fixture.getMemberQnAContent(), response.qnaContent());
        assertFalse(response.memberQnAStat());
        assertTrue(response.replyList().isEmpty());
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 작성자가 일치하지 않는 경우")
    void getMemberQnADetailWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/detail/" + noneMemberQnA.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 상세 조회. 데이터가 없는 경우")
    void getMemberQnADetailNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/detail/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성")
    void postMemberQnAReply() throws Exception {
        MemberQnA fixture = allMemberQnAList.stream()
                                        .filter(MemberQnA::isMemberQnAStat)
                                        .findFirst()
                                        .get();
        int replySize = memberQnAReplyList.stream()
                                        .filter(v ->
                                                v.getMemberQnA().getId().equals(fixture.getId())
                                        )
                                        .toList()
                                        .size();
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(fixture.getId(), "test reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "qna/member/reply")
                                        .header(accessHeader, accessTokenValue)
                                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                        .cookie(new Cookie(inoHeader, inoValue))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestDTO))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        MemberQnA patchMemberQnA = memberQnARepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchMemberQnA);
        assertFalse(patchMemberQnA.isMemberQnAStat());

        List<MyPageQnAReplyDTO> replyList = memberQnAReplyRepository.findAllByQnAId(fixture.getId());
        assertNotNull(replyList);
        assertFalse(replyList.isEmpty());
        assertEquals(replySize + 1, replyList.size());

        MyPageQnAReplyDTO saveReply = replyList.get(replyList.size() - 1);
        assertEquals(member.getNickname(), saveReply.writer());
        assertEquals(insertDTO.content(), saveReply.replyContent());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성. 회원 문의 데이터가 없는 경우")
    void postMemberQnAReplyNotFound() throws Exception {
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(0L, "test reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "qna/member/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 작성. 작성자가 일치하지 않는 경우")
    void postMemberQnAReplyWriterNotEquals() throws Exception {
        QnAReplyInsertDTO insertDTO = new QnAReplyInsertDTO(noneMemberQnA.getId(), "test reply content");
        String requestDTO = om.writeValueAsString(insertDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "qna/member/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정")
    void patchMemberQnAReply() throws Exception {
        MemberQnAReply fixture = memberQnAReplyList.stream()
                                            .filter(v ->
                                                    v.getMember().getUserId().equals(member.getUserId())
                                            )
                                            .findFirst()
                                            .get();
        QnAReplyDTO replyDTO = new QnAReplyDTO(fixture.getId(), "modify memberQnA Reply content");
        String requestDTO = om.writeValueAsString(replyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member/reply")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        MemberQnAReply patchData = memberQnAReplyRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertEquals(replyDTO.content(), patchData.getReplyContent());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 답변 데이터가 없는 경우")
    void patchMemberQnAReplyEmpty() throws Exception {
        QnAReplyDTO replyDTO = new QnAReplyDTO(0L, "modify memberQnA Reply content");
        String requestDTO = om.writeValueAsString(replyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 답변 수정. 작성자가 일치하지 않는 경우")
    void patchMemberQnAReplyWriterNotEquals() throws Exception {
        MemberQnAReply fixture = memberQnAReplyList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(admin.getUserId())
                )
                .findFirst()
                .get();
        QnAReplyDTO replyDTO = new QnAReplyDTO(fixture.getId(), "modify memberQnA Reply content");
        String requestDTO = om.writeValueAsString(replyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member/reply")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 데이터 조회")
    void getModifyData() throws Exception {
        MemberQnA fixture = allMemberQnAList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/modify/" + fixture.getId())
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue)))
                            .andExpect(status().isOk())
                            .andReturn();
        String content = result.getResponse().getContentAsString();
        MemberQnAModifyDataDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.qnaId());
        assertEquals(fixture.getMemberQnATitle(), response.qnaTitle());
        assertEquals(fixture.getMemberQnAContent(), response.qnaContent());
        assertEquals(fixture.getQnAClassification().getId(), response.qnaClassificationId());
        assertEquals(qnAClassificationList.size(), response.classificationList().size());
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 데이터 조회. 데이터가 없는 경우")
    void getModifyDataEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/modify/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 수정을 위한 데이터 조회. 작성자가 일치하지 않는 경우")
    void getModifyDataWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/member/modify/" + noneMemberQnA.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 수정")
    void patchMemberQnA() throws Exception {
        MemberQnA fixture = allMemberQnAList.stream()
                                        .filter(v ->
                                                v.getMember().getUserId().equals(member.getUserId())
                                        )
                                        .findFirst()
                                        .get();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                fixture.getId(),
                "test modify title",
                "test modify content",
                fixture.getQnAClassification().getId()
        );
        String requestDTO = om.writeValueAsString(modifyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        MemberQnA patchData = memberQnARepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertEquals(modifyDTO.title(), patchData.getMemberQnATitle());
        assertEquals(modifyDTO.content(), patchData.getMemberQnAContent());
        assertEquals(modifyDTO.classificationId(), patchData.getQnAClassification().getId());
    }

    @Test
    @DisplayName(value = "회원 문의 수정. 데이터가 없는 경우")
    void patchMemberQnANotFound() throws Exception {
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                0L,
                "test modify title",
                "test modify content",
                qnAClassificationList.get(0).getId()
        );
        String requestDTO = om.writeValueAsString(modifyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 수정. 작성자가 일치하지 않는 경우")
    void patchMemberQnAWriterNotEquals() throws Exception {
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                noneMemberQnA.getId(),
                "test modify title",
                "test modify content",
                noneMemberQnA.getQnAClassification().getId()
        );
        String requestDTO = om.writeValueAsString(modifyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 수정. 문의 분류 아이디가 잘못 된 경우")
    void patchMemberQnAWrongQnAClassificationId() throws Exception {
        MemberQnA fixture = allMemberQnAList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        MemberQnAModifyDTO modifyDTO = new MemberQnAModifyDTO(
                fixture.getId(),
                "test modify title",
                "test modify content",
                0L
        );
        String requestDTO = om.writeValueAsString(modifyDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "qna/member")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 삭제")
    void deleteMemberQnA() throws Exception {
        long deleteId = allMemberQnAList.stream()
                                .filter(v ->
                                        v.getMember().getUserId().equals(member.getUserId())
                                )
                                .findFirst()
                                .get()
                                .getId();
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/member/" + deleteId)
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        MemberQnA deleteData = memberQnARepository.findById(deleteId).orElse(null);
        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원 문의 삭제. 데이터가 없는 경우")
    void deleteMemberQnANotFound() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/member/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "회원 문의 삭제. 작성자가 일치하지 않는 경우")
    void deleteMemberQnAWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "qna/member/" + noneMemberQnA.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());

        MemberQnA checkData = memberQnARepository.findById(noneMemberQnA.getId()).orElse(null);
        assertNotNull(checkData);
    }

    @Test
    @DisplayName(value = "회원 문의 분류 목록 조회")
    void getQnAClassification() throws Exception {
        List<QnAClassificationDTO> fixture = qnAClassificationList.stream()
                .map(v ->
                        new QnAClassificationDTO(v.getId(), v.getQnaClassificationName())
                )
                .toList();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/classification")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<QnAClassificationDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);

        response.forEach(v -> assertTrue(fixture.contains(v)));
    }

    @Test
    @DisplayName(value = "회원 문의 분류 목록 조회. 데이터가 없는 경우")
    void getQnAClassificationEmpty() throws Exception {
        qnAClassificationRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "qna/classification")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<QnAClassificationDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName(value = "작성한 리뷰 목록 조회")
    void getReview() throws Exception {
        MyPagePageDTO pageDTO = PageDTOFixture.createDefaultMyPagePageDTO(1);
        int contentSize = Math.min(allReviewList.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(allReviewList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<MyPageReviewDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(totalPages, response.totalPages());
        assertEquals(contentSize, response.content().size());
    }

    @Test
    @DisplayName(value = "작성한 리뷰 목록 조회. 데이터가 없는 경우")
    void getReviewEmpty() throws Exception {
        productReviewRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<MyPageReviewDTO> response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 데이터 조회")
    void getModifyReviewData() throws Exception {
        ProductReview fixture = allReviewList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/modify/" + fixture.getId())
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        MyPagePatchReviewDataDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.reviewId());
        assertEquals(fixture.getReviewContent(), response.content());
        assertEquals(fixture.getProduct().getProductName(), response.productName());
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 데이터 조회. 데이터가 없는 경우")
    void getModifyReviewDataNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/modify/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertNotNull(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 수정을 위한 데이터 조회. 작성자가 일치하지 않는 경우")
    void getModifyReviewDataWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "review/modify/" + noneMemberReview.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertNotNull(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 작성")
    void postReview() throws Exception {
        productReviewRepository.deleteAll();
        ProductOrder fixture = productOrderList.stream()
                                        .filter(v ->
                                                v.getMember().getUserId().equals(member.getUserId())
                                        )
                                        .findFirst()
                                        .get();
        fixture.setOrderStat(OrderStatus.COMPLETE.getStatusStr());
        productOrderRepository.save(fixture);

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                                                .filter(v ->
                                                        !v.isOrderReviewStatus()
                                                )
                                                .findFirst()
                                                .get();
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                detailFixture.getProduct().getId(),
                "test insert review content",
                detailFixture.getProductOption().getId(),
                detailFixture.getId()
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<ProductReview> reviewList = productReviewRepository.findAll();
        assertFalse(reviewList.isEmpty());
        ProductReview saveReview = reviewList.get(0);

        assertEquals(member.getUserId(), saveReview.getMember().getUserId());
        assertEquals(reviewDTO.productId(), saveReview.getProduct().getId());
        assertEquals(reviewDTO.optionId(), saveReview.getProductOption().getId());

        ProductOrderDetail patchData = productOrderDetailRepository.findById(detailFixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertTrue(patchData.isOrderReviewStatus());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 상품 아이디가 잘못 된 경우")
    void postReviewWrongProductId() throws Exception {
        ProductOrder fixture = productOrderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                .filter(v ->
                        !v.isOrderReviewStatus()
                )
                .findFirst()
                .get();
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                "noneProductId",
                "test insert review content",
                detailFixture.getProductOption().getId(),
                detailFixture.getId()
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 상품 옵션 아이디가 잘못 된 경우")
    void postReviewWrongProductOptionId() throws Exception {
        ProductOrder fixture = productOrderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                .filter(v ->
                        !v.isOrderReviewStatus()
                )
                .findFirst()
                .get();
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                detailFixture.getProduct().getId(),
                "test insert review content",
                0L,
                detailFixture.getId()
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 주문 상세 아이디가 잘못 된 경우")
    void postReviewWrongOrderDetailId() throws Exception {
        ProductOrder fixture = productOrderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        fixture.setOrderStat(OrderStatus.COMPLETE.getStatusStr());
        productOrderRepository.save(fixture);

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                .filter(v ->
                        !v.isOrderReviewStatus()
                )
                .findFirst()
                .get();
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                detailFixture.getProduct().getId(),
                "test insert review content",
                detailFixture.getProductOption().getId(),
                0L
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 주문 상태가 배송 완료가 아닌 경우")
    void postReviewOrderStatusIsNotComplete() throws Exception {
        ProductOrder fixture = productOrderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        fixture.setOrderStat(OrderStatus.ORDER.getStatusStr());
        productOrderRepository.save(fixture);

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                .filter(v ->
                        !v.isOrderReviewStatus()
                )
                .findFirst()
                .get();
        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                detailFixture.getProduct().getId(),
                "test insert review content",
                detailFixture.getProductOption().getId(),
                detailFixture.getId()
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 작성. 주문 상세 데이터의 리뷰 상태값이 true인 경우")
    void postReviewOrderDetailReviewStatusIsTrue() throws Exception {
        ProductOrder fixture = productOrderList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        fixture.setOrderStat(OrderStatus.COMPLETE.getStatusStr());
        productOrderRepository.save(fixture);

        ProductOrderDetail detailFixture = fixture.getProductOrderDetailSet().stream()
                .filter(v ->
                        !v.isOrderReviewStatus()
                )
                .findFirst()
                .get();
        detailFixture.setOrderReviewStatus(true);
        productOrderDetailRepository.save(detailFixture);

        MyPagePostReviewDTO reviewDTO = new MyPagePostReviewDTO(
                detailFixture.getProduct().getId(),
                "test insert review content",
                detailFixture.getProductOption().getId(),
                detailFixture.getId()
        );
        String requestDTO = om.writeValueAsString(reviewDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 수정")
    void patchReview() throws Exception {
        ProductReview fixture = allReviewList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get();
        MyPagePatchReviewDTO patchDTO = new MyPagePatchReviewDTO(fixture.getId(), "test modify review content");
        String requestDTO = om.writeValueAsString(patchDTO);
        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "review")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        ProductReview patchData = productReviewRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertEquals(patchDTO.content(), patchData.getReviewContent());
    }

    @Test
    @DisplayName(value = "리뷰 수정. 데이터가 없는 경우")
    void patchReviewNotFound() throws Exception {
        MyPagePatchReviewDTO patchDTO = new MyPagePatchReviewDTO(0L, "test modify review content");
        String requestDTO = om.writeValueAsString(patchDTO);
        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 수정. 작성자가 일치하지 않는 경우")
    void patchReviewWriterNotEquals() throws Exception {
        MyPagePatchReviewDTO patchDTO = new MyPagePatchReviewDTO(noneMemberReview.getId(), "test modify review content");
        String requestDTO = om.writeValueAsString(patchDTO);
        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "review")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 삭제")
    void deleteReview() throws Exception {
        long deleteId = allReviewList.stream()
                .filter(v ->
                        v.getMember().getUserId().equals(member.getUserId())
                )
                .findFirst()
                .get()
                .getId();
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "review/" + deleteId)
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        ProductReview deleteReview = productReviewRepository.findById(deleteId).orElse(null);
        assertNull(deleteReview);
    }

    @Test
    @DisplayName(value = "리뷰 삭제. 데이터가 없는 경우")
    void deleteReviewNotFound() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "review/0")
                                        .header(accessHeader, accessTokenValue)
                                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                        .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().is(400))
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "리뷰 삭제. 작성자가 일치하지 않는 경우")
    void deleteReviewWriterNotEquals() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "review/" + noneMemberReview.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(403))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.ACCESS_DENIED.getMessage(), response.errorMessage());

        ProductReview checkData = productReviewRepository.findById(noneMemberReview.getId()).orElse(null);
        assertNotNull(checkData);
    }

    @Test
    @DisplayName(value = "회원 정보 수정을 위한 데이터 조회")
    void getInfo() throws Exception {
        String[] splitMail = member.getUserEmail().split("@");
        String mailSuffix = splitMail[1].substring(0, splitMail[1].indexOf('.'));
        String type = MailSuffix.findSuffixType(mailSuffix);
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "info")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        MyPageInfoDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );
        assertNotNull(response);

        assertEquals(member.getNickname(), response.nickname());
        assertEquals(member.getPhone().replaceAll("-", ""), response.phone());
        assertEquals(splitMail[0], response.mailPrefix());
        assertEquals(splitMail[1], response.mailSuffix());
        assertEquals(type, response.mailType());
    }

    @Test
    @DisplayName(value = "회원 정보 수정")
    void patchInfo() throws Exception {
        MyPageInfoPatchDTO patchDTO = new MyPageInfoPatchDTO(
                "modifyNickname",
                "01098981212",
                "modify@modify.com"
        );
        String requestDTO = om.writeValueAsString(patchDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "info")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>(){}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Member patchMember = memberRepository.findByUserId(member.getUserId());
        assertNotNull(patchMember);
        assertEquals(patchDTO.nickname(), patchMember.getNickname());
        assertEquals(patchDTO.phone(), patchMember.getPhone().replaceAll("-", ""));
        assertEquals(patchDTO.mail(), patchMember.getUserEmail());
    }
}

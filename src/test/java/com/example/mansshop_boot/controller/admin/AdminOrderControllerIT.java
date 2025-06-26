package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.ExceptionEntity;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

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
public class AdminOrderControllerIT {

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
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisTemplate<String, Long> cacheRedisTemplate;

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

    private List<ProductOrder> allProductOrderList;

    private List<ProductOrder> newProductOrderList;

    private static final String ORDER_CACHING_KEY = RedisCaching.ADMIN_ORDER_COUNT.getKey();

    private static final String URL_PREFIX = "/api/admin/";

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(40);
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        List<Member> memberList = memberAndAuthFixtureDTO.memberList();
        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.addAll(adminFixture.memberList());
        List<Auth> saveAuthList = new ArrayList<>(adminFixture.authList());
        saveAuthList.addAll(adminFixture.authList());
        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);

        Member admin = adminFixture.memberList().get(0);

        tokenMap = tokenFixture.createAndSaveAllToken(admin);
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        List<Product> productList = ProductFixture.createSaveProductList(5, classificationList.get(0));
        List<ProductOption> productOptionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(productOptionList);

        List<ProductOrder> completeProductOrderList = ProductOrderFixture.createCompleteProductOrder(memberList, productOptionList);
        newProductOrderList = ProductOrderFixture.createDefaultProductOrder(memberList, productOptionList);
        allProductOrderList = new ArrayList<>(completeProductOrderList);
        allProductOrderList.addAll(newProductOrderList);
        productOrderRepository.saveAll(allProductOrderList);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUP() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);

        cacheRedisTemplate.delete(ORDER_CACHING_KEY);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회")
    void getAllOrder() throws Exception {
        AdminOrderPageDTO pageDTOFixture = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        int contentSize = Math.min(allProductOrderList.size(), pageDTOFixture.amount());
        int totalPages = PaginationUtils.getTotalPages(allProductOrderList.size(), pageDTOFixture.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/all")
                                        .header(accessHeader, accessTokenValue)
                                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                        .cookie(new Cookie(inoHeader, inoValue)))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(ORDER_CACHING_KEY);
        assertNotNull(cachingResult);
        assertEquals(allProductOrderList.size(), cachingResult);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 받는 사람 기준 검색. 검색은 like가 아닌 equal")
    void getAllOrderSearchRecipient() throws Exception {
        ProductOrder fixture = allProductOrderList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("searchType", "recipient")
                        .param("keyword", fixture.getRecipient()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(2, response.content().size());
        assertEquals(1, response.totalPages());
        response.content().forEach(v -> assertEquals(fixture.getRecipient(), v.recipient()));

        Long cachingResult = cacheRedisTemplate.opsForValue().get(ORDER_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 사용자 아이디 기준 검색. 검색은 like가 아닌 equal")
    void getAllOrderSearchUserId() throws Exception {
        ProductOrder fixture = allProductOrderList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("searchType", "userId")
                        .param("keyword", fixture.getMember().getUserId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(2, response.content().size());
        assertEquals(1, response.totalPages());
        response.content().forEach(v -> assertEquals(fixture.getMember().getUserId(), v.userId()));

        Long cachingResult = cacheRedisTemplate.opsForValue().get(ORDER_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "전체 주문 목록 조회. 데이터가 없는 경우")
    void getAllOrderListEmpty() throws Exception {
        productOrderRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());

        Long cachingResult = cacheRedisTemplate.opsForValue().get(ORDER_CACHING_KEY);
        assertNull(cachingResult);
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회")
    void getNewOrderList() throws Exception {
        AdminOrderPageDTO pageDTOFixture = PageDTOFixture.createDefaultAdminOrderPageDTO(1);
        int contentSize = Math.min(newProductOrderList.size(), pageDTOFixture.amount());
        int totalPages = PaginationUtils.getTotalPages(newProductOrderList.size(), pageDTOFixture.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/new")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());

        response.content()
                .forEach(v ->
                        assertEquals(OrderStatus.ORDER.getStatusStr(), v.orderStatus())
                );
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 받는 사람 기준 검색. 검색은 like가 아닌 equal")
    void getNewOrderSearchRecipient() throws Exception {
        ProductOrder fixture = newProductOrderList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/new")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("searchType", "recipient")
                        .param("keyword", fixture.getRecipient()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalPages());
        response.content().forEach(v -> {
            assertEquals(fixture.getRecipient(), v.recipient());
            assertEquals(OrderStatus.ORDER.getStatusStr(), v.orderStatus());
        });
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 사용자 아이디 기준 검색. 검색은 like가 아닌 equal")
    void getNewOrderSearchUserId() throws Exception {
        ProductOrder fixture = newProductOrderList.get(0);

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/new")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("searchType", "userId")
                        .param("keyword", fixture.getMember().getUserId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalPages());
        response.content().forEach(v -> {
            assertEquals(fixture.getMember().getUserId(), v.userId());
            assertEquals(OrderStatus.ORDER.getStatusStr(), v.orderStatus());
        });
    }

    @Test
    @DisplayName(value = "미처리 주문 목록 조회. 데이터가 없는 경우")
    void getNewOrderListEmpty() throws Exception {
        productOrderRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "order/new")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminOrderResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "주문 확인 처리")
    void patchOrderStatus() throws Exception {
        ProductOrder fixture = newProductOrderList.get(0);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "order/" + fixture.getId())
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        ProductOrder patchData = productOrderRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchData);
        assertEquals(OrderStatus.PREPARATION.getStatusStr(), patchData.getOrderStat());
    }

    @Test
    @DisplayName(value = "주문 확인 처리. 주문 아이디가 잘못된 경우")
    void patchOrderStatusWrongId() throws Exception {
        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "order/0")
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
}

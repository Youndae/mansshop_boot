package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CartControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    @Value("#{jwt['cookie.cart.header']}")
    private String cartCookieHeader;

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
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String accessTokenValue;

    private String refreshTokenValue;

    private String inoValue;

    private static final String ANONYMOUS_CART_COOKIE = "testAnonymousCookieValue";

    private Map<String, String> tokenMap;

    private Member member;

    private Member anonymous;

    private Cart memberCart;

    private Cart anonymousCart;

    private List<Product> productList;

    private static final String URL_PREFIX = "/api/cart/";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(1);
        MemberAndAuthFixtureDTO anonymousFixtureDTO = MemberAndAuthFixture.createAnonymous();
        List<Member> memberList = memberAndAuthFixtureDTO.memberList();
        anonymous = anonymousFixtureDTO.memberList().get(0);
        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.add(anonymous);
        member = memberList.get(0);
        List<Auth> saveAuthList = new ArrayList<>(memberAndAuthFixtureDTO.authList());
        saveAuthList.addAll(anonymousFixtureDTO.authList());
        memberRepository.saveAll(saveMemberList);
        authRepository.saveAll(saveAuthList);

        tokenMap = tokenFixture.createAndSaveAllToken(memberList.get(0));
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        productList = ProductFixture.createSaveProductList(10, classificationList.get(0));
        List<ProductOption> optionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();

        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionList);
        List<ProductOption> saveCartProductOptionList = productList.get(0).getProductOptions();
        memberCart = CartFixture.createDefaultMemberCart(memberList, saveCartProductOptionList).get(0);
        CartDetail count1MemberCartDetail = memberCart.getCartDetailList().get(0);
        int count = -Math.abs(count1MemberCartDetail.getCartCount() - 1);
        count1MemberCartDetail.addCartCount(count);
        anonymousCart = CartFixture.createSaveAnonymousCart(saveCartProductOptionList.get(0), anonymous, ANONYMOUS_CART_COOKIE);

        List<Cart> saveCartList = new ArrayList<>();
        saveCartList.add(memberCart);
        saveCartList.add(anonymousCart);

        cartRepository.saveAll(saveCartList);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUP() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);
    }

    @Test
    @DisplayName(value = "장바구니 데이터 조회. 회원인 경우")
    void getCartListByMember() throws Exception{
        List<CartDetail> detailFixture = memberCart.getCartDetailList()
                                                .stream()
                                                .sorted(Comparator.comparingLong(CartDetail::getId).reversed())
                                                .toList();
        MvcResult result = mockMvc.perform(get(URL_PREFIX)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<CartDetailDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(detailFixture.size(), response.size());

        for(int i = 0; i < detailFixture.size(); i++) {
            CartDetail fixture = detailFixture.get(i);
            CartDetailDTO responseDTO = response.get(i);

            Product fixtureProduct = fixture.getProductOption().getProduct();
            int originPrice = fixtureProduct.getProductPrice() * fixture.getCartCount();
            int price = (fixtureProduct.getProductPrice() - (fixtureProduct.getProductPrice() * fixtureProduct.getProductDiscount() / 100)) * fixture.getCartCount();

            assertEquals(fixture.getId(), responseDTO.cartDetailId());
            assertEquals(fixtureProduct.getId(), responseDTO.productId());
            assertEquals(fixture.getProductOption().getId(), responseDTO.optionId());
            assertEquals(fixtureProduct.getProductName(), responseDTO.productName());
            assertEquals(fixtureProduct.getThumbnail(), responseDTO.productThumbnail());
            assertEquals(fixture.getProductOption().getSize(), responseDTO.size());
            assertEquals(fixture.getProductOption().getColor(), responseDTO.color());
            assertEquals(fixture.getCartCount(), responseDTO.count());
            assertEquals(originPrice, responseDTO.originPrice());
            assertEquals(price, responseDTO.price());
            assertEquals(fixtureProduct.getProductDiscount(), responseDTO.discount());
        }
    }

    @Test
    @DisplayName(value = "장바구니 데이터 조회. 회원인 경우. 데이터가 없는 경우")
    void getCartListByMemberEmpty() throws Exception{
        cartRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<CartDetailDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName(value = "장바구니 데이터 조회. 비회원인 경우.")
    void getCartListByAnonymous() throws Exception{
        List<CartDetail> detailFixture = anonymousCart.getCartDetailList()
                                                .stream()
                                                .sorted(Comparator.comparingLong(CartDetail::getId).reversed())
                                                .toList();
        MvcResult result = mockMvc.perform(get(URL_PREFIX)
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<CartDetailDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(detailFixture.size(), response.size());

        for(int i = 0; i < detailFixture.size(); i++) {
            CartDetail fixture = detailFixture.get(i);
            CartDetailDTO responseDTO = response.get(i);

            Product fixtureProduct = fixture.getProductOption().getProduct();
            int originPrice = fixtureProduct.getProductPrice() * fixture.getCartCount();
            int price = (fixtureProduct.getProductPrice() - (fixtureProduct.getProductPrice() * fixtureProduct.getProductDiscount() / 100)) * fixture.getCartCount();

            assertEquals(fixture.getId(), responseDTO.cartDetailId());
            assertEquals(fixtureProduct.getId(), responseDTO.productId());
            assertEquals(fixture.getProductOption().getId(), responseDTO.optionId());
            assertEquals(fixtureProduct.getProductName(), responseDTO.productName());
            assertEquals(fixtureProduct.getThumbnail(), responseDTO.productThumbnail());
            assertEquals(fixture.getProductOption().getSize(), responseDTO.size());
            assertEquals(fixture.getProductOption().getColor(), responseDTO.color());
            assertEquals(fixture.getCartCount(), responseDTO.count());
            assertEquals(originPrice, responseDTO.originPrice());
            assertEquals(price, responseDTO.price());
            assertEquals(fixtureProduct.getProductDiscount(), responseDTO.discount());
        }
    }

    @Test
    @DisplayName(value = "장바구니 데이터 조회. 비회원인 경우. 데이터가 없는 경우")
    void getCartListByAnonymousEmpty() throws Exception{
        cartRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX)
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<CartDetailDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 있는 회원인 경우. 이미 장바구니에 담겨있는 상품 옵션을 추가하는 경우")
    void addCartExistsProductOptionByMember() throws Exception{
        Map<Long, AddCartDTO> fixtureMap = memberCart.getCartDetailList().stream()
                .collect(Collectors.toMap(
                        CartDetail::getId,
                        v -> new AddCartDTO(v.getProductOption().getId(), v.getCartCount())
                ));
        List<AddCartDTO> requestDTO = memberCart.getCartDetailList().stream()
                .map(v -> new AddCartDTO(v.getProductOption().getId(), 2))
                .toList();

        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                                        .header(accessHeader, accessTokenValue)
                                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                        .cookie(new Cookie(inoHeader, inoValue))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(addCartRequestBody)
                                    )
                                    .andExpect(status().isOk())
                                    .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(memberCart.getId());

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(fixtureMap.size(), patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            AddCartDTO fixtureDTO = fixtureMap.getOrDefault(dataDTO.cartDetailId(), null);

            assertNotNull(fixtureDTO);
            assertEquals(fixtureDTO.optionId(), dataDTO.optionId());
            assertEquals(fixtureDTO.count() + 2, dataDTO.count());
        }
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 있는 회원인 경우. 새로운 상품 옵션을 추가하는 경우")
    void addCartNewProductOptionByMember() throws Exception {
        Map<Long, AddCartDTO> fixtureMap = memberCart.getCartDetailList().stream()
                                                .collect(Collectors.toMap(
                                                        CartDetail::getId,
                                                        v -> new AddCartDTO(v.getProductOption().getId(), v.getCartCount())
                                                ));
        List<Long> optionFixtureList = productList.get(1).getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> requestDTO = optionFixtureList.stream()
                                        .map(v -> new AddCartDTO(v, 2))
                                        .toList();
        int fixtureDetailCount = memberCart.getCartDetailList().size() + optionFixtureList.size();
        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(memberCart.getId());

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(fixtureDetailCount, patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            AddCartDTO fixture = fixtureMap.getOrDefault(dataDTO.cartDetailId(), null);

            if(fixture == null) {
                assertTrue(optionFixtureList.contains(dataDTO.optionId()));
                assertEquals(2, dataDTO.count());
            }else {
                assertEquals(fixture.optionId(), dataDTO.optionId());
                assertEquals(fixture.count(), dataDTO.count());
            }
        }
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 없는 회원인 경우.")
    void addCartNotExistsCartByMember() throws Exception {
        cartRepository.deleteAll();
        List<Long> optionFixtureList = productList.get(1).getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> requestDTO = optionFixtureList.stream()
                .map(v -> new AddCartDTO(v, 2))
                .toList();

        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);

        Long saveCartId = cartRepository.findIdByUserId(cartMemberDTO);

        assertNotNull(saveCartId);

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(saveCartId);

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(optionFixtureList.size(), patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            assertTrue(optionFixtureList.contains(dataDTO.optionId()));
            assertEquals(2, dataDTO.count());
        }
    }

    @Test
    @DisplayName(value = "장바구니 추가. 잘못된 옵션 아이디인 경우.")
    void addCartExistsCartByMemberWrongProductOptionId() throws Exception {
        List<AddCartDTO> requestDTO = List.of(new AddCartDTO(0L, 2));

        String addCartRequestBody = om.writeValueAsString(requestDTO);

        mockMvc.perform(post(URL_PREFIX)
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 있는 비회원인 경우. 이미 장바구니에 담겨있는 상품 옵션을 추가하는 경우")
    void addCartExistsProductOptionByAnonymous() throws Exception{
        Map<Long, AddCartDTO> fixtureMap = anonymousCart.getCartDetailList().stream()
                .collect(Collectors.toMap(
                        CartDetail::getId,
                        v -> new AddCartDTO(v.getProductOption().getId(), v.getCartCount())
                ));
        List<AddCartDTO> requestDTO = anonymousCart.getCartDetailList().stream()
                .map(v -> new AddCartDTO(v.getProductOption().getId(), 2))
                .toList();

        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(anonymousCart.getId());

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(fixtureMap.size(), patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            AddCartDTO fixtureDTO = fixtureMap.getOrDefault(dataDTO.cartDetailId(), null);

            assertNotNull(fixtureDTO);
            assertEquals(fixtureDTO.optionId(), dataDTO.optionId());
            assertEquals(fixtureDTO.count() + 2, dataDTO.count());
        }
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 있는 비회원인 경우. 새로운 상품 옵션을 추가하는 경우")
    void addCartNewProductOptionByAnonymous() throws Exception {
        Map<Long, AddCartDTO> fixtureMap = anonymousCart.getCartDetailList().stream()
                .collect(Collectors.toMap(
                        CartDetail::getId,
                        v -> new AddCartDTO(v.getProductOption().getId(), v.getCartCount())
                ));
        List<Long> optionFixtureList = productList.get(1).getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> requestDTO = optionFixtureList.stream()
                .map(v -> new AddCartDTO(v, 2))
                .toList();
        int fixtureDetailCount = anonymousCart.getCartDetailList().size() + optionFixtureList.size();
        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(anonymousCart.getId());

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(fixtureDetailCount, patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            AddCartDTO fixture = fixtureMap.getOrDefault(dataDTO.cartDetailId(), null);

            if(fixture == null) {
                assertTrue(optionFixtureList.contains(dataDTO.optionId()));
                assertEquals(2, dataDTO.count());
            }else {
                assertEquals(fixture.optionId(), dataDTO.optionId());
                assertEquals(fixture.count(), dataDTO.count());
            }
        }
    }

    @Test
    @DisplayName(value = "장바구니 추가. 쿠키와 장바구니가 없는 비회원인 경우.")
    void addCartNotExistsCartByAnonymous() throws Exception {
        cartRepository.deleteAll();
        List<Long> optionFixtureList = productList.get(1).getProductOptions().stream().mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> requestDTO = optionFixtureList.stream()
                .map(v -> new AddCartDTO(v, 2))
                .toList();

        String addCartRequestBody = om.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post(URL_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCartRequestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        String cartCookie = result.getResponse().getHeaders("Set-Cookie")
                .stream()
                .filter(v -> v.startsWith(cartCookieHeader + "="))
                .findFirst()
                .map(cookie -> {
                    int start = (cartCookieHeader + "=").length();
                    int end = cookie.indexOf(';');
                    return (end != -1) ? cookie.substring(start, end) : cookie.substring(start);
                })
                .orElse(null);

        assertNotNull(cartCookie);

        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous.getUserId(), cartCookie);

        Long saveCartId = cartRepository.findIdByUserId(cartMemberDTO);

        assertNotNull(saveCartId);

        List<CartDetailDTO> patchDataList = cartDetailRepository.findAllByCartId(saveCartId);

        assertNotNull(patchDataList);
        assertFalse(patchDataList.isEmpty());
        assertEquals(optionFixtureList.size(), patchDataList.size());

        for(CartDetailDTO dataDTO : patchDataList) {
            assertTrue(optionFixtureList.contains(dataDTO.optionId()));
            assertEquals(2, dataDTO.count());
        }
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 회원인 경우")
    void cartCountUpByMember() throws Exception {
        CartDetail fixture = memberCart.getCartDetailList().get(0);
        int originCount = fixture.getCartCount();

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "count-up/" + fixture.getId())
                                                    .header(accessHeader, accessTokenValue)
                                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                                    .cookie(new Cookie(inoHeader, inoValue))
                                            )
                                            .andExpect(status().isOk())
                                            .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartDetail patchData = cartDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(originCount + 1, patchData.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 회원인 경우. 장바구니 상세 아이디가 잘못된 경우")
    void cartCountUpByMemberWrongDetailId() throws Exception {
        mockMvc.perform(patch(URL_PREFIX + "count-up/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 회원인 경우. 장바구니 상세의 장바구니 아이디가 자신의 아이디와 일치하지 않는 경우")
    void cartCountUpByMemberNotMatchedId() throws Exception {
        CartDetail fixture = anonymousCart.getCartDetailList().get(0);
        mockMvc.perform(patch(URL_PREFIX + "count-up/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 비회원인 경우")
    void cartCountUpByAnonymous() throws Exception {
        CartDetail fixture = anonymousCart.getCartDetailList().get(0);
        int originCount = fixture.getCartCount();

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "count-up/" + fixture.getId())
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartDetail patchData = cartDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(originCount + 1, patchData.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 비회원인 경우. 장바구니가 없는 비회원인 경우")
    void cartCountUpByAnonymousNotExistCart() throws Exception {
        mockMvc.perform(patch(URL_PREFIX + "count-up/1"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }


    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 회원인 경우")
    void cartCountDownByMember() throws Exception {
        CartDetail fixture = memberCart.getCartDetailList().stream()
                .filter(v -> v.getCartCount() > 1)
                .findFirst()
                .get();
        int originCount = fixture.getCartCount();

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "count-down/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartDetail patchData = cartDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(originCount - 1, patchData.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 회원인 경우. 기존 수량이 1이라 감소되지 않는 경우")
    void cartCountDownByMemberAt1() throws Exception {
        CartDetail fixture = memberCart.getCartDetailList().stream()
                .filter(v -> v.getCartCount() == 1)
                .findFirst()
                .get();

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "count-down/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartDetail patchData = cartDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(1, patchData.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 회원인 경우. 장바구니 상세 아이디가 잘못된 경우")
    void cartCountDownByMemberWrongDetailId() throws Exception {
        mockMvc.perform(patch(URL_PREFIX + "count-down/0")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 회원인 경우. 장바구니 상세의 장바구니 아이디가 자신의 아이디와 일치하지 않는 경우")
    void cartCountDownByMemberNotMatchedId() throws Exception {
        CartDetail fixture = anonymousCart.getCartDetailList().get(0);
        mockMvc.perform(patch(URL_PREFIX + "count-down/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 비회원인 경우")
    void cartCountDownByAnonymous() throws Exception {
        CartDetail fixture = anonymousCart.getCartDetailList().get(0);
        int originCount = fixture.getCartCount();

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "count-down/" + fixture.getId())
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        CartDetail patchData = cartDetailRepository.findById(fixture.getId()).orElse(null);

        assertNotNull(patchData);
        assertEquals(originCount - 1, patchData.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 비회원인 경우. 장바구니가 없는 비회원인 경우")
    void cartCountDownByAnonymousNotExistCart() throws Exception {
        mockMvc.perform(patch(URL_PREFIX + "count-down/1"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "회원의 장바구니 선택 상품 삭제")
    void deleteSelectCartByMember() throws Exception {
        List<CartDetail> fixtureList = memberCart.getCartDetailList()
                .stream()
                .limit(memberCart.getCartDetailList().size() - 1)
                .toList();
        List<Long> deleteIds = fixtureList.stream().mapToLong(CartDetail::getId).boxed().toList();

        String deleteSelectId = om.writeValueAsString(deleteIds);

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "select")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteSelectId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<CartDetailDTO> existData = cartDetailRepository.findAllByCartId(memberCart.getId());

        assertNotNull(existData);
        assertFalse(existData.isEmpty());
        assertEquals(1, existData.size());
        assertFalse(deleteIds.contains(existData.get(0).cartDetailId()));
    }

    @Test
    @DisplayName(value = "회원의 장바구니 선택 상품 삭제. 잘못된 장바구니 상세 아이디가 있는 경우")
    void deleteSelectCartByMemberNotExistsDetailId() throws Exception{
        List<CartDetail> fixtureList = memberCart.getCartDetailList()
                .stream()
                .limit(memberCart.getCartDetailList().size() - 2)
                .toList();
        List<Long> deleteIds = new ArrayList<>(fixtureList.stream().mapToLong(CartDetail::getId).boxed().toList());
        deleteIds.add(0L);
        String deleteSelectId = om.writeValueAsString(deleteIds);

        mockMvc.perform(delete(URL_PREFIX + "select")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteSelectId)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();

        List<CartDetailDTO> existData = cartDetailRepository.findAllByCartId(memberCart.getId());

        assertNotNull(existData);
        assertFalse(existData.isEmpty());
        assertEquals(memberCart.getCartDetailList().size(), existData.size());
    }

    @Test
    @DisplayName(value = "회원의 장바구니 선택 상품 삭제. 선택 상품 삭제지만 모든 상품을 선택한 경우.")
    void deleteSelectCartByMemberAllSelect() throws Exception{
        List<Long> deleteIds = memberCart.getCartDetailList()
                .stream()
                .mapToLong(CartDetail::getId)
                .boxed()
                .toList();
        long cartId = memberCart.getId();
        String deleteSelectId = om.writeValueAsString(deleteIds);

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "select")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteSelectId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Cart deleteData = cartRepository.findById(cartId).orElse(null);

        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 삭제. 장바구니 데이터가 없는 경우")
    void deleteSelectCartNotExistsCartData() throws Exception{
        cartRepository.deleteAll();
        List<Long> deleteIds = List.of(1L, 2L, 3L);
        String deleteSelectId = om.writeValueAsString(deleteIds);

        mockMvc.perform(delete(URL_PREFIX + "select")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteSelectId)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "비회원의 장바구니 선택 상품 삭제. 선택 상품 삭제지만 모든 상품을 선택한 경우.")
    void deleteSelectCartByAnonymousAllSelect() throws Exception{
        List<Long> deleteIds = anonymousCart.getCartDetailList()
                .stream()
                .mapToLong(CartDetail::getId)
                .boxed()
                .toList();
        long cartId = anonymousCart.getId();
        String deleteSelectId = om.writeValueAsString(deleteIds);

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "select")
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteSelectId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Cart deleteData = cartRepository.findById(cartId).orElse(null);

        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "회원의 장바구니 상품 전체 삭제")
    void deleteCartByMember() throws Exception{
        long cartId = memberCart.getId();

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Cart deleteData = cartRepository.findById(cartId).orElse(null);

        assertNull(deleteData);
    }

    @Test
    @DisplayName(value = "장바구니 상품 전체 삭제. 장바구니 데이터가 존재하지 않는 경우")
    void deleteCartNotExistsCartData() throws Exception{
        cartRepository.deleteAll();

        mockMvc.perform(delete(URL_PREFIX + "all")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @DisplayName(value = "비회원의 장바구니 상품 전체 삭제")
    void deleteCartByAnonymous() throws Exception{
        long cartId = anonymousCart.getId();

        MvcResult result = mockMvc.perform(delete(URL_PREFIX + "all")
                        .cookie(new Cookie(cartCookieHeader, ANONYMOUS_CART_COOKIE))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Cart deleteData = cartRepository.findById(cartId).orElse(null);

        assertNull(deleteData);
    }
}

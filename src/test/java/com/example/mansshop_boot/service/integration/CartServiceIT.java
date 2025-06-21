package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.service.CartService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class CartServiceIT {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberRepository memberRepository;

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
    private EntityManager entityManager;

    private List<Member> memberList;

    private Member noneCartMember;

    private Member anonymous;

    private List<Product> productList;

    private List<ProductOption> optionList;

    private List<Cart> cartList;

    private Cart anonymousCart;

    private static String ANONYMOUS_CART_COOKIE = "anonymousCartCookieValue";


    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixture = MemberAndAuthFixture.createDefaultMember(30);
        MemberAndAuthFixtureDTO anonymousFixture = MemberAndAuthFixture.createAnonymous();
        MemberAndAuthFixtureDTO noneCartMemberFixture = MemberAndAuthFixture.createDefaultMember(1);
        memberList = memberAndAuthFixture.memberList();
        anonymous = anonymousFixture.memberList().get(0);
        noneCartMember = noneCartMemberFixture.memberList().get(0);

        List<Member> saveMemberList = new ArrayList<>(memberList);
        saveMemberList.add(anonymous);
        saveMemberList.add(noneCartMember);

        memberRepository.saveAll(saveMemberList);

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        productList = ProductFixture.createSaveProductList(10, classificationList.get(0));
        optionList = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();

        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionList);

        cartList = CartFixture.createDefaultMemberCart(memberList, optionList);
        anonymousCart = CartFixture.createSaveAnonymousCart(optionList.get(0), anonymous, ANONYMOUS_CART_COOKIE);

        List<Cart> saveCartList = new ArrayList<>(cartList);
        saveCartList.add(anonymousCart);

        cartRepository.saveAll(saveCartList);

        entityManager.flush();
        entityManager.clear();
    }

    private Principal getPrincipal(Member member) {
        return member::getUserId;
    }

    private CartMemberDTO createCartMemberDTO(String userId, String cartCookie) {
        return new CartMemberDTO(userId, cartCookie);
    }

    @Test
    @DisplayName(value = "장바구니 목록 조회. 회원인 경우")
    void getCartListByUser() {
        Member member = memberList.get(0);
        Cart memberCart = cartList.stream().filter(v -> v.getMember().getUserId().equals(member.getUserId())).findFirst().get();
        List<CartDetail> memberCartDetailList = memberCart.getCartDetailList().stream().sorted(Comparator.comparingLong(CartDetail::getId).reversed()).toList();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        List<CartDetailDTO> result = assertDoesNotThrow(() -> cartService.getCartList(cartMemberDTO));

        assertNotNull(result);
        assertEquals(memberCartDetailList.size(), result.size());

        for(int i = 0; i < result.size(); i++) {
            CartDetailDTO resultDTO = result.get(i);
            CartDetail fixture = memberCartDetailList.get(i);
            Product fixtureProduct = fixture.getProductOption().getProduct();
            int originPrice = fixtureProduct.getProductPrice() * fixture.getCartCount();
            int price = (fixtureProduct.getProductPrice() - (fixtureProduct.getProductPrice() * fixtureProduct.getProductDiscount() / 100)) * fixture.getCartCount();

            assertEquals(fixture.getId(), resultDTO.cartDetailId());
            assertEquals(fixtureProduct.getId(), resultDTO.productId());
            assertEquals(fixture.getProductOption().getId(), resultDTO.optionId());
            assertEquals(fixtureProduct.getProductName(), resultDTO.productName());
            assertEquals(fixtureProduct.getThumbnail(), resultDTO.productThumbnail());
            assertEquals(fixture.getProductOption().getSize(), resultDTO.size());
            assertEquals(fixture.getProductOption().getColor(), resultDTO.color());
            assertEquals(fixture.getCartCount(), resultDTO.count());
            assertEquals(originPrice, resultDTO.originPrice());
            assertEquals(price, resultDTO.price());
            assertEquals(fixtureProduct.getProductDiscount(), resultDTO.discount());
        }
    }

    @Test
    @DisplayName(value = "장바구니 목록 조회. 비회원인 경우")
    void getCartListByAnonymous() {
        List<CartDetail> memberCartDetailList  = anonymousCart.getCartDetailList().stream().sorted(Comparator.comparingLong(CartDetail::getId).reversed()).toList();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(anonymous.getUserId(), ANONYMOUS_CART_COOKIE);

        List<CartDetailDTO> result = assertDoesNotThrow(() -> cartService.getCartList(cartMemberDTO));

        assertNotNull(result);
        assertEquals(memberCartDetailList.size(), result.size());

        for(int i = 0; i < result.size(); i++) {
            CartDetailDTO resultDTO = result.get(i);
            CartDetail fixture = memberCartDetailList.get(i);
            Product fixtureProduct = fixture.getProductOption().getProduct();
            int originPrice = fixtureProduct.getProductPrice() * fixture.getCartCount();
            int price = (fixtureProduct.getProductPrice() - (fixtureProduct.getProductPrice() * fixtureProduct.getProductDiscount() / 100)) * fixture.getCartCount();

            assertEquals(fixture.getId(), resultDTO.cartDetailId());
            assertEquals(fixtureProduct.getId(), resultDTO.productId());
            assertEquals(fixture.getProductOption().getId(), resultDTO.optionId());
            assertEquals(fixtureProduct.getProductName(), resultDTO.productName());
            assertEquals(fixtureProduct.getThumbnail(), resultDTO.productThumbnail());
            assertEquals(fixture.getProductOption().getSize(), resultDTO.size());
            assertEquals(fixture.getProductOption().getColor(), resultDTO.color());
            assertEquals(fixture.getCartCount(), resultDTO.count());
            assertEquals(originPrice, resultDTO.originPrice());
            assertEquals(price, resultDTO.price());
            assertEquals(fixtureProduct.getProductDiscount(), resultDTO.discount());
        }
    }

    @Test
    @DisplayName(value = "장바구니 목록 조회. 데이터가 없는 경우")
    void getCartListEmpty() {
        Member member = memberList.get(0);
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        List<CartDetailDTO> result = assertDoesNotThrow(() -> cartService.getCartList(cartMemberDTO));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니 데이터가 존재하지 않고 처음 추가하는 경우")
    void addCart() {
        Member member = memberList.get(0);
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();
        int optionCount = 3;
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);
        List<Long> optionIds = optionList.stream().limit(optionCount).mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> addList = new ArrayList<>();
        for(int i = 1; i < optionCount; i++) {
            addList.add(
                    new AddCartDTO(
                            optionIds.get(i),
                            i
                    )
            );
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        Principal principal = getPrincipal(member);

        String result = assertDoesNotThrow(() -> cartService.addCart(addList, cartMemberDTO, response, principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Long saveCartId = cartRepository.findIdByUserId(cartMemberDTO);
        assertNotNull(saveCartId);

        List<CartDetailDTO> detailList = cartDetailRepository.findAllByCartId(saveCartId);
        assertEquals(addList.size(), detailList.size());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 존재하고 추가하는 경우")
    void addCartExists() {
        Member member = memberList.get(0);
        cartDetailRepository.deleteAll();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);
        int optionCount = 3;
        List<Long> optionIds = optionList.stream().limit(optionCount).mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> addList = new ArrayList<>();
        for(int i = 1; i < optionCount; i++) {
            addList.add(
                    new AddCartDTO(
                            optionIds.get(i),
                            i
                    )
            );
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        Principal principal = getPrincipal(member);
        Long memberCartId = cartList.stream()
                .filter(v -> v.getMember().getUserId().equals(member.getUserId()))
                .findFirst()
                .get()
                .getId();

        String result = assertDoesNotThrow(() -> cartService.addCart(addList, cartMemberDTO, response, principal));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<CartDetailDTO> detailList = cartDetailRepository.findAllByCartId(memberCartId);
        assertFalse(detailList.isEmpty());
        assertEquals(addList.size(), detailList.size());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 비회원이 처음 장바구니 추가를 하는 경우 쿠키 반환")
    void addCartAnonymous() {
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(anonymous.getUserId(), null);
        int optionCount = 3;
        List<Long> optionIds = optionList.stream().limit(optionCount).mapToLong(ProductOption::getId).boxed().toList();
        List<AddCartDTO> addList = new ArrayList<>();
        for(int i = 1; i < optionCount; i++) {
            addList.add(
                    new AddCartDTO(
                            optionIds.get(i),
                            i
                    )
            );
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = assertDoesNotThrow(() -> cartService.addCart(addList, cartMemberDTO, response, null));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);
        String cookie = response.getHeader("Set-Cookie").toString();
        assertNotNull(cookie);

        Long saveCartId = cartRepository.findIdByUserId(cartMemberDTO);
        assertNotNull(saveCartId);

        List<CartDetailDTO> detailList = cartDetailRepository.findAllByCartId(saveCartId);
        assertEquals(addList.size(), detailList.size());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 추가하는 상품 옵션이 존재하지 않는 경우")
    void addCartCookieProductOptionNotFound() {
        Member member = memberList.get(0);
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);
        List<AddCartDTO> addList = List.of(new AddCartDTO(0L, 3));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Principal principal = getPrincipal(member);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addCart(addList, cartMemberDTO, response, principal)
        );
    }

    @Test
    @DisplayName(value = "장바구니 전체 삭제")
    void deleteAllCart() {
        Member member = memberList.get(0);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        String result = assertDoesNotThrow(() -> cartService.deleteAllCart(cartMemberDTO, response));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Long cartId = cartRepository.findIdByUserId(cartMemberDTO);
        assertNull(cartId);
    }

    @Test
    @DisplayName(value = "장바구니 전체 삭제. 장바구니 데이터가 존재하지 않는 경우")
    void deleteAllCartNotFound() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        CartMemberDTO cartMemberDTO = createCartMemberDTO("noneMember", null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.deleteAllCart(cartMemberDTO, response)
        );
    }

    @Test
    @DisplayName(value = "장바구니 수량 증가")
    void countUp() {
        Member member = memberList.get(0);
        Cart memberCart = cartList.stream().filter(v -> v.getMember().getUserId().equals(member.getUserId())).findFirst().get();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);
        long detailId = memberCart.getCartDetailList().get(0).getId();
        int detailCount = memberCart.getCartDetailList().get(0).getCartCount();

        String result = assertDoesNotThrow(() -> cartService.countUp(cartMemberDTO, detailId));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        CartDetail patchDetail = cartDetailRepository.findById(detailId).orElse(null);
        assertNotNull(patchDetail);
        assertEquals(detailCount + 1, patchDetail.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 수량 증가. 사용자 장바구니가 존재하지 않는 경우")
    void countUpCartNotFound() {
        CartMemberDTO cartMemberDTO = createCartMemberDTO("noneMember", null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.countUp(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 수량 증가. 사용자 장바구니 상세 데이터가 존재하지 않는 경우")
    void countUpCartDetailNotFound() {
        Member member = memberList.get(0);
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.countUp(cartMemberDTO, 0L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 수량 감소")
    void countDown() {
        Member member = memberList.get(0);
        Cart memberCart = cartList.stream().filter(v -> v.getMember().getUserId().equals(member.getUserId())).findFirst().get();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);
        long detailId = memberCart.getCartDetailList().get(0).getId();
        int detailCount = memberCart.getCartDetailList().get(0).getCartCount();
        int detailCountResult = detailCount == 1 ? 1 : detailCount - 1;

        String result = assertDoesNotThrow(() -> cartService.countDown(cartMemberDTO, detailId));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        CartDetail patchDetail = cartDetailRepository.findById(detailId).orElse(null);
        assertNotNull(patchDetail);
        assertEquals(detailCountResult, patchDetail.getCartCount());
    }

    @Test
    @DisplayName(value = "장바구니 수량 감소. 사용자 장바구니가 존재하지 않는 경우")
    void countDownCartNotFound() {
        CartMemberDTO cartMemberDTO = createCartMemberDTO("noneMember", null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.countDown(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 수량 감소. 사용자 장바구니 상세 데이터가 존재하지 않는 경우")
    void countDownCartDetailNotFound() {
        Member member = memberList.get(0);
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.countDown(cartMemberDTO, 0L)
        );
    }

    @Test
    @DisplayName(value = "선택 상품 제거")
    void deleteCartSelect() {
        Member member = memberList.get(0);
        Cart memberCart = cartList.stream().filter(v -> v.getMember().getUserId().equals(member.getUserId())).findFirst().get();
        List<Long> detailIds = memberCart.getCartDetailList()
                .stream()
                .limit(memberCart.getCartDetailList().size() - 1)
                .mapToLong(CartDetail::getId)
                .boxed()
                .toList();
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        String result = assertDoesNotThrow(() -> cartService.deleteCartSelect(cartMemberDTO, detailIds));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<Long> memberCartDetailIds = cartDetailRepository.findAllIdByCartId(memberCart.getId());
        assertFalse(memberCartDetailIds.isEmpty());
        assertEquals(1, memberCartDetailIds.size());
    }

    @Test
    @DisplayName(value = "선택 상품 제거. 사용자 장바구니가 존재하지 않는 경우")
    void deleteCartSelectCartNotFound() {
        CartMemberDTO cartMemberDTO = createCartMemberDTO("noneMember", null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.deleteCartSelect(cartMemberDTO, List.of(1L))
        );
    }

    @Test
    @DisplayName(value = "선택 상품 제거. 삭제할 장바구니 상세 데이터가 존재하지 않는 경우")
    void deleteCartSelectCartDetailNotFound() {
        Member member = memberList.get(0);
        CartMemberDTO cartMemberDTO = createCartMemberDTO(member.getUserId(), null);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.deleteCartSelect(cartMemberDTO, List.of(0L))
        );
    }

    @Test
    @DisplayName(value = "장바구니 데이터 처리를 위한 사용자 정보 DTO 생성 및 반환. 회원인 경우")
    void getCartMemberDTO() {
        Member member = memberList.get(0);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Principal principal = getPrincipal(member);

        CartMemberDTO result = assertDoesNotThrow(() -> cartService.getCartMemberDTO(request, principal));

        assertNotNull(result);
        assertEquals(member.getUserId(), result.uid());
        assertNull(result.cartCookieValue());
    }

    @Test
    @DisplayName(value = "장바구니 데이터 처리를 위한 사용자 정보 DTO 생성 및 반환. 비회원이고 Cookie가 없는 경우")
    void getCartMemberDTOAnonymousEmptyCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        CartMemberDTO result = assertDoesNotThrow(() -> cartService.getCartMemberDTO(request, null));

        assertNotNull(result);
        assertEquals(anonymous.getUserId(), result.uid());
        assertNull(result.cartCookieValue());
    }

    @Test
    @DisplayName(value = "장바구니 데이터 처리를 위한 사용자 정보 DTO 생성 및 반환. 비회원이고 Cookie가 있는 경우")
    void getCartMemberDTOAnonymous() {
        String cookieHeader = "cartCookie";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(cookieHeader, ANONYMOUS_CART_COOKIE));

        CartMemberDTO result = assertDoesNotThrow(() -> cartService.getCartMemberDTO(request, null));

        assertNotNull(result);
        assertEquals(anonymous.getUserId(), result.uid());
        assertEquals(ANONYMOUS_CART_COOKIE, result.cartCookieValue());
    }
}

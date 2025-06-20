package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.Fixture.CartFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.config.customException.exception.CustomOrderSessionExpiredException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.RabbitMQProperties;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.RabbitMQPrefix;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.domain.vo.order.OrderItemVO;
import com.example.mansshop_boot.domain.vo.order.PreOrderDataVO;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.service.OrderService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService.retryFailedOrder()의 경우
 * AdminFailedDataService에서 재시도를 위해 호출하는 메서드이기 때문에
 * 이 테스트 클래스에서는 제외.
 * DLQ 메시지를 재시도하는 처리이기도 하고,
 * DLQ 재처리 테스트 자체를 수동으로 수행하는 것이 더 효율적이라고 판단해 테스트 클래스를 작성하지 않음.
 */
@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Autowired
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RedisTemplate<String, PreOrderDataVO> orderRedisTemplate;

    private Member member;

    private Member anonymous;

    private Product product;

    private List<ProductOption> productOptionList;

    private Cart memberCart;

    private List<CartDetail> memberCartDetailList;

    private Cart anonymousCart;

    private Principal principal;

    private static final String ANONYMOUS_COOKIE_VALUE = "testCookieValue";

    private static final String ORDER_TOKEN = "testOrderTokenValue";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO memberAndAuthFixtureDTO = MemberAndAuthFixture.createDefaultMember(1);
        MemberAndAuthFixtureDTO anonymousFixtureDTO = MemberAndAuthFixture.createAnonymous();
        List<Member> memberList = new ArrayList<>(memberAndAuthFixtureDTO.memberList());
        memberList.addAll(anonymousFixtureDTO.memberList());
        memberRepository.saveAll(memberList);
        member = memberAndAuthFixtureDTO.memberList().get(0);
        anonymous = anonymousFixtureDTO.memberList().get(0);
        principal = () -> member.getUserId();

        List<Classification> classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        product = ProductFixture.createSaveProductList(1, classificationList.get(0)).get(0);
        productOptionList = product.getProductOptions();
        productRepository.save(product);
        productOptionRepository.saveAll(productOptionList);

        memberCart = CartFixture.createDefaultMemberCart(List.of(member), productOptionList).get(0);
        anonymousCart = CartFixture.createSaveAnonymousCartMultipleOptions(productOptionList, anonymous, ANONYMOUS_COOKIE_VALUE);

        List<Cart> saveCartList = List.of(memberCart, anonymousCart);
        cartRepository.saveAll(saveCartList);

        memberCartDetailList = memberCart.getCartDetailList();
    }

    @AfterEach
    void set() {
        cartRepository.deleteAll();
        productOrderRepository.deleteAll();
        memberRepository.deleteAll();
        productSalesSummaryRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        classificationRepository.deleteAll();
        periodSalesSummaryRepository.deleteAll();
    }

    private List<OrderProductDTO> createDirectOrderProductDTOFixtureList() {
        List<OrderProductDTO> resultList = new ArrayList<>();
        for(int i = 0; i < productOptionList.size(); i++) {
            ProductOption option = productOptionList.get(i);

            resultList.add(
                    new OrderProductDTO(
                            option.getId(),
                            product.getProductName(),
                            product.getId(),
                            i + 1,
                            option.getProduct().getProductPrice() * (i + 1)
                    )
            );
        }

        return resultList;
    }

    private List<OrderProductDTO> createCartOrderProductDTOFixtureList() {
        List<OrderProductDTO> resultList = new ArrayList<>();
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            ProductOption option = memberCartDetailList.get(i).getProductOption();

            resultList.add(
                    new OrderProductDTO(
                            option.getId(),
                            option.getProduct().getProductName(),
                            option.getProduct().getId(),
                            i + 1,
                            option.getProduct().getProductPrice() * (i + 1)
                    )
            );
        }

        return resultList;
    }

    @Test
    @DisplayName(value = "결제 이후 주문 데이터 처리. 비회원이 상품 상세 페이지에서 주문한 경우")
    void paymentDirectAnonymous() {
        List<OrderProductDTO> orderProductList = createDirectOrderProductDTOFixtureList();
        int totalPrice = orderProductList.stream().mapToInt(OrderProductDTO::getDetailPrice).sum();
        int deliveryFee = totalPrice < 100000 ? 3500 : 0;
        PaymentDTO paymentDTO = new PaymentDTO(
                "testRecipient",
                "01000010002",
                "testRecipient Memo",
                "testRecipient home",
                orderProductList,
                deliveryFee,
                totalPrice,
                "card",
                "direct",
                productOptionList.size()
        );
        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous.getUserId(), ANONYMOUS_COOKIE_VALUE);

        String result = assertDoesNotThrow(() -> orderService.payment(paymentDTO, cartMemberDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<ProductOrder> saveOrder = productOrderRepository.findAll();
        assertFalse(saveOrder.isEmpty());
        assertEquals(1, saveOrder.size());

        List<ProductOrderDetail> saveOrderDetail = productOrderDetailRepository.findAll();
        assertEquals(productOptionList.size(), saveOrderDetail.size());

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    List<PeriodSalesSummary> savePeriodSummary = periodSalesSummaryRepository.findAll();

                    assertFalse(savePeriodSummary.isEmpty());
                    assertEquals(1, savePeriodSummary.size());
                    assertEquals(totalPrice, savePeriodSummary.get(0).getCardTotal());
                    assertEquals(0, savePeriodSummary.get(0).getCashTotal());

                    List<ProductSalesSummary> saveProductSummary = productSalesSummaryRepository.findAll();

                    assertFalse(saveProductSummary.isEmpty());
                    assertEquals(productOptionList.size(), saveProductSummary.size());
                });
    }

    @Test
    @DisplayName(value = "결제 이후 주문 데이터 처리. 회원이 장바구니에서 주문한 경우")
    void paymentCartMember() {
        List<OrderProductDTO> orderProductList = createCartOrderProductDTOFixtureList();
        int totalPrice = orderProductList.stream().mapToInt(OrderProductDTO::getDetailPrice).sum();
        int deliveryFee = totalPrice < 100000 ? 3500 : 0;
        PaymentDTO paymentDTO = new PaymentDTO(
                "testRecipient",
                "01000010002",
                "testRecipient Memo",
                "testRecipient home",
                orderProductList,
                deliveryFee,
                totalPrice,
                "cash",
                "cart",
                productOptionList.size()
        );
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);

        String result = assertDoesNotThrow(() -> orderService.payment(paymentDTO, cartMemberDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        List<ProductOrder> saveOrder = productOrderRepository.findAll();
        assertFalse(saveOrder.isEmpty());
        assertEquals(1, saveOrder.size());

        List<ProductOrderDetail> saveOrderDetail = productOrderDetailRepository.findAll();
        assertEquals(productOptionList.size(), saveOrderDetail.size());

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    List<Cart> cart = cartRepository.findAll();
                    assertEquals(1, cart.size());

                    List<PeriodSalesSummary> savePeriodSummary = periodSalesSummaryRepository.findAll();

                    assertFalse(savePeriodSummary.isEmpty());
                    assertEquals(1, savePeriodSummary.size());
                    assertEquals(0, savePeriodSummary.get(0).getCardTotal());
                    assertEquals(totalPrice, savePeriodSummary.get(0).getCashTotal());

                    List<ProductSalesSummary> saveProductSummary = productSalesSummaryRepository.findAll();

                    assertFalse(saveProductSummary.isEmpty());
                    assertEquals(productOptionList.size(), saveProductSummary.size());
                });
    }

    @Test
    @DisplayName(value = "결제 요청 시 상품 정보 조회")
    void getProductOrderData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<OrderProductRequestDTO> optionIdAndCountDTO = new ArrayList<>();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < productOptionList.size(); i++){
            ProductOption option = productOptionList.get(i);
            int count = (int) (option.getId() * (i + 1));
            int price = product.getProductPrice() * count;
            totalPrice += price;

            optionIdAndCountDTO.add(
                    new OrderProductRequestDTO(option.getId(), count)
            );

            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            option.getId(),
                            product.getProductName(),
                            option.getSize(),
                            option.getColor(),
                            count,
                            price
                    )
            );

            orderItemVOList.add(
                    new OrderItemVO(
                            product.getId(),
                            option.getId(),
                            count,
                            price
                    )
            );
        }
        OrderDataResponseDTO result = assertDoesNotThrow(() -> orderService.getProductOrderData(optionIdAndCountDTO, request, response, principal));

        assertNotNull(result);
        assertFalse(result.orderData().isEmpty());
        assertEquals(orderDataDTOList.size(), result.orderData().size());
        assertEquals(totalPrice, result.totalPrice());

        orderDataDTOList.forEach(v -> assertTrue(result.orderData().contains(v)));

        String orderToken = response.getHeader("Set-Cookie").split(";", 2)[0].split("=", 2)[1];
        assertNotNull(orderToken);
        assertNotEquals("", orderToken);

        PreOrderDataVO preOrderDataVO = orderRedisTemplate.opsForValue().get(orderToken);
        assertNotNull(preOrderDataVO);
        assertEquals(principal.getName(), preOrderDataVO.userId());
        assertEquals(totalPrice, preOrderDataVO.totalPrice());
        orderItemVOList.forEach(v -> assertTrue(preOrderDataVO.orderData().contains(v)));

        orderRedisTemplate.delete(orderToken);
    }

    @Test
    @DisplayName(value = "결제 요청 시 상품 정보 조회. 상품 옵션 아이디들이 잘못 된 경우")
    void getProductOrderDataWrongIds() {
        List<OrderProductRequestDTO> optionIdAndCountDTO = List.of(new OrderProductRequestDTO(0L, 3));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(IllegalArgumentException.class, () -> orderService.getProductOrderData(optionIdAndCountDTO, request, response, principal));
    }

    @Test
    @DisplayName(value = "결제 요청 시 장바구니 상품 정보 조회")
    void getCartOrderData() {
        List<Long> cartDetailIds = new ArrayList<>();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            CartDetail detail = memberCartDetailList.get(i);
            cartDetailIds.add(detail.getId());
            int price = product.getProductPrice() * detail.getCartCount();
            totalPrice += price;
            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            product.getProductName(),
                            detail.getProductOption().getSize(),
                            detail.getProductOption().getColor(),
                            detail.getCartCount(),
                            price
                    )
            );

            orderItemVOList.add(
                    new OrderItemVO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            detail.getCartCount(),
                            price
                    )
            );
        }

        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OrderDataResponseDTO result = assertDoesNotThrow(() -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response));

        assertNotNull(result);
        assertFalse(result.orderData().isEmpty());
        assertEquals(orderDataDTOList.size(), result.orderData().size());
        assertEquals(totalPrice, result.totalPrice());

        orderDataDTOList.forEach(v -> assertTrue(result.orderData().contains(v)));

        String orderToken = response.getHeader("Set-Cookie").split(";", 2)[0].split("=", 2)[1];
        assertNotNull(orderToken);
        assertNotEquals("", orderToken);

        PreOrderDataVO preOrderDataVO = orderRedisTemplate.opsForValue().get(orderToken);
        assertNotNull(preOrderDataVO);
        assertEquals(principal.getName(), preOrderDataVO.userId());
        assertEquals(totalPrice, preOrderDataVO.totalPrice());
        orderItemVOList.forEach(v -> assertTrue(preOrderDataVO.orderData().contains(v)));

        orderRedisTemplate.delete(orderToken);
    }

    @Test
    @DisplayName(value = "결제 요청 시 장바구니 상품 정보 조회. 장바구니 상세 아이디들이 잘못 된 경우")
    void getCartOrderDataWrongIds() {
        List<Long> cartDetailIds = List.of(0L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO(member.getUserId(), null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(
                CustomNotFoundException.class,
                () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response)
        );
    }

    @Test
    @DisplayName(value = "결제 요청 시 장바구니 상품 정보 조회. 장바구니 데이터가 사용자의 데이터가 아닌 경우")
    void getCartOrderDataNotEqualsMember() {
        List<Long> cartDetailIds = memberCartDetailList.stream().mapToLong(CartDetail::getId).boxed().toList();
        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous.getUserId(), null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(
                CustomAccessDeniedException.class,
                () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response)
        );
    }

    @Test
    @DisplayName(value = "결제 요청 시 장바구니 상품 정보 조회. 장바구니 데이터가 비회원 쿠키 값의 데이터가 아닌 경우")
    void getCartOrderDataNotEqualsAnonymous() {
        List<Long> cartDetailIds = anonymousCart.getCartDetailList().stream().mapToLong(CartDetail::getId).boxed().toList();
        CartMemberDTO cartMemberDTO = new CartMemberDTO(anonymous.getUserId(), "wrongcookievalue");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(
                CustomAccessDeniedException.class,
                () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response)
        );
    }

    @Test
    @DisplayName(value = "결제 API 호출 직전 데이터 검증")
    void validateOrder() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("order", ORDER_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            CartDetail detail = memberCartDetailList.get(i);
            int price = product.getProductPrice() * detail.getCartCount();
            totalPrice += price;

            orderItemVOList.add(
                    new OrderItemVO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            detail.getCartCount(),
                            price
                    )
            );

            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            product.getProductName(),
                            detail.getProductOption().getSize(),
                            detail.getProductOption().getColor(),
                            detail.getCartCount(),
                            price
                    )
            );
        }
        PreOrderDataVO fixtureOrderDataVO = new PreOrderDataVO(principal.getName(), orderItemVOList, totalPrice);
        OrderDataResponseDTO  requestDTO = new OrderDataResponseDTO(orderDataDTOList, totalPrice);

        orderRedisTemplate.opsForValue().set(ORDER_TOKEN, fixtureOrderDataVO);

        ResponseMessageDTO result = assertDoesNotThrow(() -> orderService.validateOrder(requestDTO, principal, request, response));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result.message());

        orderRedisTemplate.delete(ORDER_TOKEN);
    }

    @Test
    @DisplayName(value = "결제 API 호출 직전 데이터 검증. 주문 토큰이 없는 경우")
    void validateOrderTokenIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            CartDetail detail = memberCartDetailList.get(i);
            int price = product.getProductPrice() * detail.getCartCount();
            totalPrice += price;

            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            product.getProductName(),
                            detail.getProductOption().getSize(),
                            detail.getProductOption().getColor(),
                            detail.getCartCount(),
                            price
                    )
            );
        }
        OrderDataResponseDTO  requestDTO = new OrderDataResponseDTO(orderDataDTOList, totalPrice);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, principal, request, response)
        );
    }

    @Test
    @DisplayName(value = "결제 API 호출 직전 데이터 검증. Redis 캐싱된 데이터가 없는 경우")
    void validateOrderCachingDataIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("order", ORDER_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            CartDetail detail = memberCartDetailList.get(i);
            int price = product.getProductPrice() * detail.getCartCount();
            totalPrice += price;

            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            product.getProductName(),
                            detail.getProductOption().getSize(),
                            detail.getProductOption().getColor(),
                            detail.getCartCount(),
                            price
                    )
            );
        }
        OrderDataResponseDTO  requestDTO = new OrderDataResponseDTO(orderDataDTOList, totalPrice);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, principal, request, response)
        );
    }

    @Test
    @DisplayName(value = "결제 API 호출 직전 데이터 검증. 검증 결과 일치하지 않는 경우")
    void validateOrderFailedToValidate() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("order", ORDER_TOKEN));
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        List<OrderDataDTO> orderDataDTOList = new ArrayList<>();
        int totalPrice = 0;
        for(int i = 0; i < memberCartDetailList.size(); i++) {
            CartDetail detail = memberCartDetailList.get(i);
            int price = product.getProductPrice() * detail.getCartCount();
            totalPrice += price;

            orderItemVOList.add(
                    new OrderItemVO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            detail.getCartCount(),
                            price
                    )
            );

            orderDataDTOList.add(
                    new OrderDataDTO(
                            product.getId(),
                            detail.getProductOption().getId(),
                            product.getProductName(),
                            detail.getProductOption().getSize(),
                            detail.getProductOption().getColor(),
                            detail.getCartCount(),
                            price
                    )
            );
        }
        PreOrderDataVO fixtureOrderDataVO = new PreOrderDataVO(principal.getName(), orderItemVOList, totalPrice + 1000);
        OrderDataResponseDTO  requestDTO = new OrderDataResponseDTO(orderDataDTOList, totalPrice);

        orderRedisTemplate.opsForValue().set(ORDER_TOKEN, fixtureOrderDataVO);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, principal, request, response)
        );

        orderRedisTemplate.delete(ORDER_TOKEN);
    }
}

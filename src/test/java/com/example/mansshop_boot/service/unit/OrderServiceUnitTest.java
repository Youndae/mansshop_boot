package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.config.customException.exception.CustomOrderSessionExpiredException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderDataDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderProductInfoDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.domain.vo.order.OrderItemVO;
import com.example.mansshop_boot.domain.vo.order.PreOrderDataVO;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.service.OrderServiceImpl;
import com.example.mansshop_boot.service.unit.fixture.OrderUnitFixture;
import com.example.mansshop_boot.service.util.CookieHeaderParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceUnitTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private RedisTemplate<String, PreOrderDataVO> redisTemplate;

    @Mock
    private ValueOperations<String, PreOrderDataVO> valueOperations;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Test
    @DisplayName(value = "상품 페이지에서 선택한 상품 바로 구매를 위한 상품 데이터 조회")
    void getProductOrderData() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderProductRequestDTO dto1 = new OrderProductRequestDTO(1L, 3);
        OrderProductRequestDTO dto2 = new OrderProductRequestDTO(2L, 4);
        List<OrderProductRequestDTO> optionIdAndCountDTO = List.of(dto1, dto2);
        List<Long> optionIds = List.of(1L, 2L);
        List<OrderProductInfoDTO> orderDataDTO = OrderUnitFixture.createOrderProductInfoDTOList();

        when(productOptionRepository.findOrderData(optionIds))
                .thenReturn(orderDataDTO);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        OrderDataResponseDTO result = assertDoesNotThrow(
                () -> orderService.getProductOrderData(optionIdAndCountDTO, request, response, null)
        );

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());

        Map<String, String> cookieAttributes = CookieHeaderParser.parseSetCookieHeader(cookieCaptor.getValue());

        assertTrue(cookieAttributes.containsKey("order"));
        assertNotNull(cookieAttributes.get("order"));
        assertTrue(cookieAttributes.containsKey("Path"));
        assertEquals("/", cookieAttributes.get("Path"));
        assertTrue(cookieAttributes.containsKey("Max-Age"));
        assertEquals(String.valueOf(Duration.ofMinutes(10).getSeconds()), cookieAttributes.get("Max-Age"));
        assertTrue(cookieAttributes.containsKey("Secure"));
        assertTrue(cookieAttributes.containsKey("HttpOnly"));
        assertTrue(cookieAttributes.containsKey("SameSite"));
        assertEquals("Strict", cookieAttributes.get("SameSite"));

        assertNotNull(result);
        assertFalse(result.orderData().isEmpty());
        assertEquals(110000, result.totalPrice());
        assertEquals(optionIdAndCountDTO.size(), result.orderData().size());
    }

    @Test
    @DisplayName(value = "상품 페이지에서 선택한 상품 바로 구매를 위한 상품 데이터 조회. 데이터가 없는 경우")
    void getProductOrderDataEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderProductRequestDTO dto1 = new OrderProductRequestDTO(1L, 3);
        OrderProductRequestDTO dto2 = new OrderProductRequestDTO(2L, 4);
        List<OrderProductRequestDTO> optionIdAndCountDTO = List.of(dto1, dto2);
        List<Long> optionIds = List.of(1L, 2L);

        when(productOptionRepository.findOrderData(optionIds))
                .thenReturn(Collections.emptyList());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getProductOrderData(optionIdAndCountDTO, request, response, null)
        );
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회.")
    void getCartOrderData() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<Long> cartDetailIds = List.of(1L, 2L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO("testUser", null);
        Cart cartEntity = OrderUnitFixture.createCart();
        List<OrderProductInfoDTO> orderDataDTO = OrderUnitFixture.createOrderProductInfoDTOList();

        when(cartDetailRepository.findAllById(cartDetailIds))
                .thenReturn(cartEntity.getCartDetailList());
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cartEntity));
        when(productOptionRepository.findOrderData(cartDetailIds))
                .thenReturn(orderDataDTO);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        OrderDataResponseDTO result = assertDoesNotThrow(() -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response));

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());

        Map<String, String> cookieAttributes = CookieHeaderParser.parseSetCookieHeader(cookieCaptor.getValue());

        assertTrue(cookieAttributes.containsKey("order"));
        assertNotNull(cookieAttributes.get("order"));
        assertTrue(cookieAttributes.containsKey("Path"));
        assertEquals("/", cookieAttributes.get("Path"));
        assertTrue(cookieAttributes.containsKey("Max-Age"));
        assertEquals(String.valueOf(Duration.ofMinutes(10).getSeconds()), cookieAttributes.get("Max-Age"));
        assertTrue(cookieAttributes.containsKey("Secure"));
        assertTrue(cookieAttributes.containsKey("HttpOnly"));
        assertTrue(cookieAttributes.containsKey("SameSite"));
        assertEquals("Strict", cookieAttributes.get("SameSite"));

        assertNotNull(result);
        assertFalse(result.orderData().isEmpty());
        assertEquals(110000, result.totalPrice());
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회. 장바구니 상세 데이터가 없는 경우")
    void getCartOrderDataDetailEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<Long> cartDetailIds = List.of(1L, 2L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO("testUser", null);

        when(cartDetailRepository.findAllById(cartDetailIds))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomNotFoundException.class, () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response));

        verify(cartRepository, never()).findById(anyLong());
        verify(productOptionRepository, never()).findOrderData(List.of(anyLong()));
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회. 조회한 장바구니가 사용자 데이터가 아닌 경우")
    void getCartOrderDataDetailAccessDenied() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<Long> cartDetailIds = List.of(1L, 2L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO("Anonymous", null);
        Cart cartEntity = Cart.builder()
                .id(1L)
                .member(
                        Member.builder()
                                .userId("testUser")
                                .build()
                )
                .cookieId(null)
                .build();
        CartDetail cartDetail1 = CartDetail.builder()
                .cart(cartEntity)
                .productOption(ProductOption.builder().id(1L).build())
                .cartCount(3)
                .build();

        CartDetail cartDetail2 = CartDetail.builder()
                .cart(cartEntity)
                .productOption(ProductOption.builder().id(2L).build())
                .cartCount(4)
                .build();
        List<CartDetail> cartDetails = List.of(cartDetail1, cartDetail2);

        when(cartDetailRepository.findAllById(cartDetailIds)).thenReturn(cartDetails);
        when(cartRepository.findById(cartEntity.getId())).thenReturn(Optional.of(cartEntity));

        assertThrows(CustomAccessDeniedException.class, () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO, request, response));

        verify(productOptionRepository, never()).findOrderData(List.of(anyLong()));
    }

    @Test
    @DisplayName(value = "결제 API 호출 전 데이터 검증")
    void validateOrder() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderDataResponseDTO requestDTO = OrderUnitFixture.createOrderDataResponseDTO();
        Cookie orderToken = new Cookie("order", "orderTestValue");

        List<OrderItemVO> orderItems = requestDTO.orderData().stream().map(OrderDataDTO::toOrderItemVO).toList();
        PreOrderDataVO validateData = new PreOrderDataVO("Anonymous", orderItems, requestDTO.totalPrice());

        when(request.getCookies()).thenReturn(new Cookie[] { orderToken });
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(orderToken.getValue())).thenReturn(validateData);


        ResponseMessageDTO result = assertDoesNotThrow(
                () -> orderService.validateOrder(requestDTO, null, request, response)
        );

        assertEquals(Result.OK.getResultKey(), result.message());
    }

    @Test
    @DisplayName(value = "결제 API 호출 전 데이터 검증. orderToken이 없는 경우")
    void validateOrderTokenIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderDataResponseDTO requestDTO = OrderUnitFixture.createOrderDataResponseDTO();

        when(request.getCookies()).thenReturn(null);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, null, request, response)
        );

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName(value = "결제 API 호출 전 데이터 검증. 검증 데이터가 존재하지 않는 경우")
    void validateOrderValidateDataIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderDataResponseDTO requestDTO = OrderUnitFixture.createOrderDataResponseDTO();
        Cookie orderToken = new Cookie("order", "orderTestValue");

        when(request.getCookies()).thenReturn(new Cookie[] { orderToken });
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(orderToken.getValue())).thenReturn(null);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, null, request, response)
        );

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());

        Map<String, String> cookieAttributes = CookieHeaderParser.parseSetCookieHeader(cookieCaptor.getValue());

        assertTrue(cookieAttributes.containsKey("order"));
        assertEquals("", cookieAttributes.get("order"));
        assertTrue(cookieAttributes.containsKey("Path"));
        assertEquals("/", cookieAttributes.get("Path"));
        assertTrue(cookieAttributes.containsKey("Max-Age"));
        assertEquals("0", cookieAttributes.get("Max-Age"));
    }

    @Test
    @DisplayName(value = "결제 API 호출 전 데이터 검증. 검증 데이터와 일치하지 않는 경우")
    void validateOrderInValidData() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OrderDataResponseDTO requestDTO = OrderUnitFixture.createOrderDataResponseDTO();
        Cookie orderToken = new Cookie("order", "orderTestValue");
        OrderItemVO orderItem = new OrderItemVO("testProduct1", 2L, 3, 50000);
        PreOrderDataVO validateData = new PreOrderDataVO("Anonymous", List.of(orderItem), requestDTO.totalPrice());

        when(request.getCookies()).thenReturn(new Cookie[] { orderToken });
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(orderToken.getValue())).thenReturn(validateData);

        assertThrows(
                CustomOrderSessionExpiredException.class,
                () -> orderService.validateOrder(requestDTO, null, request, response)
        );

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());

        Map<String, String> cookieAttributes = CookieHeaderParser.parseSetCookieHeader(cookieCaptor.getValue());

        assertTrue(cookieAttributes.containsKey("order"));
        assertEquals("", cookieAttributes.get("order"));
        assertTrue(cookieAttributes.containsKey("Path"));
        assertEquals("/", cookieAttributes.get("Path"));
        assertTrue(cookieAttributes.containsKey("Max-Age"));
        assertEquals("0", cookieAttributes.get("Max-Age"));
    }
}

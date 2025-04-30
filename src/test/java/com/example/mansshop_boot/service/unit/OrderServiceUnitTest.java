package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderProductInfoDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.service.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceUnitTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Test
    @DisplayName(value = "상품 페이지에서 선택한 상품 바로 구매를 위한 상품 데이터 조회")
    void getProductOrderData() {
        OrderProductRequestDTO dto1 = new OrderProductRequestDTO(1L, 3);
        OrderProductRequestDTO dto2 = new OrderProductRequestDTO(2L, 4);
        List<OrderProductRequestDTO> optionIdAndCountDTO = List.of(dto1, dto2);
        List<Long> optionIds = List.of(1L, 2L);

        OrderProductInfoDTO optionDTO1 = new OrderProductInfoDTO(
                "testProductId1",
                1L,
                "testProductName1",
                "testSize1",
                "testColor1",
                10000
        );

        OrderProductInfoDTO optionDTO2 = new OrderProductInfoDTO(
                "testProductId1",
                2L,
                "testProductName1",
                "testSize1",
                "testColor1",
                20000
        );

        List<OrderProductInfoDTO> orderDataDTO = List.of(optionDTO1, optionDTO2);

        when(productOptionRepository.findOrderData(optionIds))
                .thenReturn(orderDataDTO);

        OrderDataResponseDTO result = Assertions.assertDoesNotThrow(() -> orderService.getProductOrderData(optionIdAndCountDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.orderData().isEmpty());
        Assertions.assertEquals(110000, result.totalPrice());
        Assertions.assertEquals(optionIdAndCountDTO.size(), result.orderData().size());
    }

    @Test
    @DisplayName(value = "상품 페이지에서 선택한 상품 바로 구매를 위한 상품 데이터 조회. 데이터가 없는 경우")
    void getProductOrderDataEmpty() {
        OrderProductRequestDTO dto1 = new OrderProductRequestDTO(1L, 3);
        OrderProductRequestDTO dto2 = new OrderProductRequestDTO(2L, 4);
        List<OrderProductRequestDTO> optionIdAndCountDTO = List.of(dto1, dto2);
        List<Long> optionIds = List.of(1L, 2L);

        when(productOptionRepository.findOrderData(optionIds))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.getProductOrderData(optionIdAndCountDTO));
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회.")
    void getCartOrderData() {
        List<Long> cartDetailIds = List.of(1L, 2L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO("testUser", null);
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

        OrderProductInfoDTO optionDTO1 = new OrderProductInfoDTO(
                "testProductId1",
                1L,
                "testProductName1",
                "testSize1",
                "testColor1",
                10000
        );

        OrderProductInfoDTO optionDTO2 = new OrderProductInfoDTO(
                "testProductId1",
                2L,
                "testProductName1",
                "testSize1",
                "testColor1",
                20000
        );
        List<CartDetail> cartDetails = List.of(cartDetail1, cartDetail2);
        List<OrderProductInfoDTO> orderDataDTO = List.of(optionDTO1, optionDTO2);

        when(cartDetailRepository.findAllById(cartDetailIds))
                .thenReturn(cartDetails);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cartEntity));
        when(productOptionRepository.findOrderData(cartDetailIds))
                .thenReturn(orderDataDTO);

        OrderDataResponseDTO result = Assertions.assertDoesNotThrow(() -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.orderData().isEmpty());
        Assertions.assertEquals(110000, result.totalPrice());
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회. 장바구니 상세 데이터가 없는 경우")
    void getCartOrderDataDetailEmpty() {
        List<Long> cartDetailIds = List.of(1L, 2L);
        CartMemberDTO cartMemberDTO = new CartMemberDTO("testUser", null);

        when(cartDetailRepository.findAllById(cartDetailIds))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(CustomNotFoundException.class, () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO));

        verify(cartRepository, never()).findById(anyLong());
        verify(productOptionRepository, never()).findOrderData(List.of(anyLong()));
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 주문을 위한 상품 데이터 조회. 조회한 장바구니가 사용자 데이터가 아닌 경우")
    void getCartOrderDataDetailAccessDenied() {
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

        Assertions.assertThrows(CustomAccessDeniedException.class, () -> orderService.getCartOrderData(cartDetailIds, cartMemberDTO));

        verify(productOptionRepository, never()).findOrderData(List.of(anyLong()));
    }
}

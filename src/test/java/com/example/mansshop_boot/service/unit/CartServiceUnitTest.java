package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.service.CartServiceImpl;
import com.example.mansshop_boot.service.unit.fixture.CartUnitFixture;
import com.example.mansshop_boot.service.unit.fixture.MemberUnitFixture;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceUnitTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    private CartMemberDTO cartMemberDTO;

    @BeforeEach
    void init() {
        cartMemberDTO = new CartMemberDTO("coco", null);
    }

    @Test
    @DisplayName(value = "장바구니 목록 조회")
    void getCartList() {
        List<CartDetailDTO> detailFixture = List.of(
                new CartDetailDTO(
                        1L,
                        "testProductId",
                        1L,
                        "testProductName",
                        "testProductThumbnail",
                        "testSize",
                        "testColor",
                        1,
                        10000,
                        9000,
                        10
                )
        );
        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(1L);
        when(cartDetailRepository.findAllByCartId(1L)).thenReturn(detailFixture);

        List<CartDetailDTO> result = assertDoesNotThrow(() -> cartService.getCartList(cartMemberDTO));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName(value = "장바구니 목록 조회. 장바구니가 없는 경우")
    void getCartListEmpty() {
        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(1L);

        List<CartDetailDTO> result = assertDoesNotThrow(() -> cartService.getCartList(cartMemberDTO));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 존재하는 경우")
    void addCart() {
        List<AddCartDTO> addList = List.of(
                new AddCartDTO(1L, 2)
        );
        HttpServletResponse response = mock(HttpServletResponse.class);
        Principal principal = mock(Principal.class);
        Cart cart = CartUnitFixture.createCartFixture();
        ProductOption productOption = ProductOption.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = assertDoesNotThrow(() -> cartService.addCart(addList, cartMemberDTO, response, principal));

        assertEquals(Result.OK.getResultKey(), result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName(value = "장바구니 추가. 장바구니가 없는 경우")
    void addCartNotFound() {
        List<AddCartDTO> addList = List.of(
                new AddCartDTO(1L, 2)
        );
        HttpServletResponse response = mock(HttpServletResponse.class);
        Principal principal = mock(Principal.class);
        Member member = MemberUnitFixture.createMemberFixture();
        ProductOption productOption = ProductOption.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(null);
        when(memberRepository.findById(cartMemberDTO.uid())).thenReturn(Optional.of(member));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = assertDoesNotThrow(() -> cartService.addCart(addList, cartMemberDTO, response, principal));

        assertEquals(Result.OK.getResultKey(), result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName(value = "장바구니 전체 삭제")
    void deleteAllCart() {
        HttpServletResponse response = new MockHttpServletResponse();
        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(1L);

        String result = assertDoesNotThrow(() -> cartService.deleteAllCart(cartMemberDTO, response));

        assertEquals(Result.OK.getResultKey(), result);
        verify(cartRepository).deleteById(1L);
    }

    @Test
    @DisplayName(value = "장바구니 전체 삭제. 장바구니가 없는 경우")
    void deleteAllCartEmpty() {
        HttpServletResponse response = new MockHttpServletResponse();
        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(null);

        CustomNotFoundException e = assertThrows(CustomNotFoundException.class,
                () -> cartService.deleteAllCart(cartMemberDTO, response)
        );

        assertEquals(ErrorCode.NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가")
    void countUp() {
        CartDetail cartDetail = CartUnitFixture.createCartDetailFixture();
        Cart cart = cartDetail.getCart();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.of(cartDetail));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail);

        String result = assertDoesNotThrow(() -> cartService.countUp(cartMemberDTO, cartDetail.getId()));

        assertEquals(Result.OK.getResultKey(), result);
        assertEquals(2, cartDetail.getCartCount());
        verify(cartDetailRepository).save(cartDetail);
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 장바구니 데이터가 없는 경우")
    void countUpEmpty() {
        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.countUp(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 증가. 수정해야 할 장바구니 상세 데이터가 없는 경우")
    void countUpDetailEmpty() {
        Cart cart = CartUnitFixture.createCartFixture();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(cartDetailRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> cartService.countUp(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소")
    void countDown() {
        CartDetail cartDetail = CartUnitFixture.createCartDetailFixture();
        cartDetail.addCartCount(1);
        Cart cart = cartDetail.getCart();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.of(cartDetail));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail);

        String result = assertDoesNotThrow(() -> cartService.countDown(cartMemberDTO, cartDetail.getId()));

        assertEquals(Result.OK.getResultKey(), result);
        assertEquals(1, cartDetail.getCartCount());
        verify(cartDetailRepository).save(cartDetail);
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 현재 count가 1인 경우")
    void countDownStopAtOne() {
        CartDetail cartDetail = CartUnitFixture.createCartDetailFixture();
        Cart cart = cartDetail.getCart();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(cartDetailRepository.findById(cartDetail.getId())).thenReturn(Optional.of(cartDetail));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail);

        String result = assertDoesNotThrow(() -> cartService.countDown(cartMemberDTO, cartDetail.getId()));

        assertEquals(Result.OK.getResultKey(), result);
        assertEquals(1, cartDetail.getCartCount());
        verify(cartDetailRepository).save(cartDetail);
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 장바구니가 없는 경우")
    void countDownEmpty() {
        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.countDown(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 상품 수량 감소. 장바구니 상세 데이터가 없는 경우")
    void countDownDetailEmpty() {
        Cart cart = CartUnitFixture.createCartFixture();

        when(cartRepository.findByUserIdAndCookieValue(cartMemberDTO)).thenReturn(cart);
        when(cartDetailRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> cartService.countDown(cartMemberDTO, 1L)
        );
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 삭제. 삭제할 상품이 장바구니 전체와 동일한 경우")
    void deleteCartSelect() {
        List<Long> deleteCartDetailIds = List.of(1L);
        Cart cart = CartUnitFixture.createCartFixture();

        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(cart.getId());
        when(cartDetailRepository.findAllIdByCartId(cart.getId())).thenReturn(deleteCartDetailIds);
        doNothing().when(cartRepository).deleteById(cart.getId());

        String result = assertDoesNotThrow(() -> cartService.deleteCartSelect(cartMemberDTO, deleteCartDetailIds));

        assertEquals(Result.OK.getResultKey(), result);
        verify(cartRepository).deleteById(cart.getId());
        verify(cartDetailRepository, never()).deleteAllById(deleteCartDetailIds);
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 삭제. 삭제할 상품이 장바구니 데이터의 일부인 경우")
    void deleteCartSelectSmallerThen() {
        List<Long> deleteCartDetailIds = List.of(1L);
        Cart cart = CartUnitFixture.createCartFixture();

        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(cart.getId());
        when(cartDetailRepository.findAllIdByCartId(cart.getId())).thenReturn(List.of(1L, 2L));
        doNothing().when(cartDetailRepository).deleteAllById(deleteCartDetailIds);

        String result = assertDoesNotThrow(() -> cartService.deleteCartSelect(cartMemberDTO, deleteCartDetailIds));

        assertEquals(Result.OK.getResultKey(), result);
        verify(cartRepository, never()).deleteById(cart.getId());
        verify(cartDetailRepository).deleteAllById(deleteCartDetailIds);
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 삭제. 상세 데이터에 없는 아이디가 리스트에 포함되어있는 경우")
    void deleteCartSelectBiggerThen() {
        List<Long> deleteCartDetailIds = List.of(1L, 2L);
        Cart cart = CartUnitFixture.createCartFixture();

        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(cart.getId());
        when(cartDetailRepository.findAllIdByCartId(cart.getId())).thenReturn(List.of(1L));

        assertThrows(IllegalArgumentException.class,
                () -> cartService.deleteCartSelect(cartMemberDTO, deleteCartDetailIds)
        );

        verify(cartRepository, never()).deleteById(cart.getId());
        verify(cartDetailRepository, never()).deleteAllById(deleteCartDetailIds);
    }

    @Test
    @DisplayName(value = "장바구니 선택 상품 삭제. 장바구니가 없는 경우")
    void deleteCartSelectEntityEmpty() {
        List<Long> deleteCartDetailIds = List.of(1L);
        when(cartRepository.findIdByUserId(cartMemberDTO)).thenReturn(null);

        assertThrows(
                CustomNotFoundException.class,
                () -> cartService.deleteCartSelect(cartMemberDTO, deleteCartDetailIds)
        );
    }
}

package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.dto.order.business.OrderDataDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderProductInfoDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOption;

import java.util.List;

public class OrderUnitFixture {

    // 결제 API 호출 전 검증 시의 RequestDTO
    // 사용자는 비회원처리
    public static OrderDataResponseDTO createOrderDataResponseDTO() {
        OrderDataDTO orderDataDTO1 = new OrderDataDTO(
                "testProduct1",
                1L,
                "testProductName1",
                "testSize",
                "testColor",
                3,
                30000
        );

        OrderDataDTO orderDataDTO2 = new OrderDataDTO(
                "testProduct2",
                2L,
                "testProductName2",
                "testSize2",
                "testColor2",
                2,
                20000
        );
        List<OrderDataDTO> orderDataDTOs = List.of(orderDataDTO1, orderDataDTO2);

        return new OrderDataResponseDTO(orderDataDTOs, 50000);
    }

    // 상품 페이지에서 바로구매 요청 시의 RequestDTO
    public static List<OrderProductInfoDTO> createOrderProductInfoDTOList() {
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

        return List.of(optionDTO1, optionDTO2);
    }

    // Cart 엔티티와 연관 엔티티인 CartDetail List 생성
    public static Cart createCart() {
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

        cartDetails.forEach(cartEntity::addCartDetail);

        return cartEntity;
    }

    // 장바구니에서 선택 상품 주문 요청 처리 중 DB 조회 결과 리스트
    public static List<OrderProductInfoDTO> createOrderCartDTOList() {
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

        return List.of(optionDTO1, optionDTO2);
    }
}

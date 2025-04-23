package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName(value = "상품 상세 페이지에서 주문 요청시 상품 데이터 조회")
    void getProductOrderData() {
        OrderProductRequestDTO data1 = new OrderProductRequestDTO(40L, 2);
        OrderProductRequestDTO data2 = new OrderProductRequestDTO(41L, 3);
        OrderProductRequestDTO data3 = new OrderProductRequestDTO(42L, 5);
        List<OrderProductRequestDTO> requestDTO = List.of(data1, data2, data3);

        OrderDataResponseDTO result = orderService.getProductOrderData(requestDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "장바구니에서 주문 요청 시 상품 데이터 조회")
    void getCartOrderData() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO("tester2", null);
        List<Long> detailIds = List.of(839L, 840L, 841L, 842L);

        OrderDataResponseDTO result = orderService.getCartOrderData(detailIds, cartMemberDTO);

        Assertions.assertNotNull(result);
    }
}

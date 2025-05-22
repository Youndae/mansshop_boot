package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Principal;
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
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Principal principal = Mockito.mock(Principal.class);

        OrderDataResponseDTO result = orderService.getProductOrderData(requestDTO, request, response, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "장바구니에서 주문 요청 시 상품 데이터 조회")
    void getCartOrderData() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO("tester2", null);
        List<Long> detailIds = List.of(839L, 840L, 841L, 842L);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        OrderDataResponseDTO result = orderService.getCartOrderData(detailIds, cartMemberDTO, request, response);

        Assertions.assertNotNull(result);
    }
}

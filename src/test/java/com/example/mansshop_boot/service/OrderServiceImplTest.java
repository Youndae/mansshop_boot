package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Test
    void getProductOrderData() {
        List<OrderProductRequestDTO> requestDTOS = new ArrayList<>();
        requestDTOS.add(new OrderProductRequestDTO(2840L, 5));
        requestDTOS.add(new OrderProductRequestDTO(2841L, 10));
        requestDTOS.add(new OrderProductRequestDTO(2842L, 15));

        OrderDataResponseDTO result = orderService.getProductOrderData(requestDTOS);

        System.out.println(result);
    }

    @Test
    void getCartOrderData() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO("coco", null);
        List<Long> cartDetailIds = new ArrayList<>();
        cartDetailIds.add(71L);
        cartDetailIds.add(72L);
        cartDetailIds.add(73L);

        OrderDataResponseDTO result = orderService.getCartOrderData(cartDetailIds, cartMemberDTO);

        System.out.println(result);
    }
}
package com.example.mansshop_boot.service.integration;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.service.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class CartServiceIT {

    @Autowired
    private CartService cartService;

    @Test
    @DisplayName(value = "장바구니 리스트 조회")
    void getCartList() {
        CartMemberDTO cartMemberDTO = new CartMemberDTO("tester2", null);
        List<CartDetailDTO> result = cartService.getCartList(cartMemberDTO);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }


}

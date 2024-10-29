package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartDetailDSLRepositoryImplTest {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Test
    @DisplayName("cartId is 2")
    void getCartDetail() {
        List<CartDetailDTO> dto = cartDetailRepository.findAllByCartId(2L);

        dto.forEach(System.out::println);
    }
}
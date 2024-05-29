package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.product.ProductOptionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductOptionDSLRepositoryImplTest {

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Test
    void listTest() {
        String productId = "BAGS20210629134401";

        List<ProductOptionDTO>  dto = productOptionRepository.findByDetailOption(productId);

        dto.forEach(System.out::println);
    }
}
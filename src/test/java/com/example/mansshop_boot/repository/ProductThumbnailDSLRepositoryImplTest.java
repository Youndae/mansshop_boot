package com.example.mansshop_boot.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductThumbnailDSLRepositoryImplTest {

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    @Test
    void listTest() {
        String productId = "BAGS20210629134401";

        List<String> productThumbnail = productThumbnailRepository.findByProductId(productId);

        productThumbnail.forEach(System.out::println);
    }
}
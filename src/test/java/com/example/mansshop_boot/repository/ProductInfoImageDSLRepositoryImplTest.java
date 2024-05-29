package com.example.mansshop_boot.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductInfoImageDSLRepositoryImplTest {

    @Autowired
    private ProductInfoImageRepository productInfoImageRepository;

    @Test
    void listTest() {
        String productId = "OUTER20210630114925";

        List<String> imageList = productInfoImageRepository.findByProductId(productId);

        imageList.forEach(System.out::println);

    }
}
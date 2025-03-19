package com.example.mansshop_boot.service;

import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Principal;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    private static final String PRODUCT_ID = "BAGS20250301214630690018433";

    @Test
    @DisplayName(value = "상품 상세 조회")
    void getDetail() {
        Principal principal = createPrincipal();

        ProductDetailDTO result = productService.getDetail(PRODUCT_ID, principal);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName(value = "상품에 대한 리뷰 조회")
    void getDetailReview() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Page<ProductReviewDTO> result = productService.getDetailReview(pageDTO, PRODUCT_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.reviewAmount(), result.getContent().size());
    }

    @Test
    @DisplayName(value = "상품에 대한 문의 조회")
    void getDetailQnA() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Page<ProductQnAResponseDTO> result = productService.getDetailQnA(pageDTO, PRODUCT_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageDTO.reviewAmount(), result.getContent().size());
    }



    Principal createPrincipal() {
        return () -> "tester2";
    }
}

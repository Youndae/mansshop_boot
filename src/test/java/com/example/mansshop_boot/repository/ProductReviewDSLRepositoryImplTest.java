package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductReviewDSLRepositoryImplTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Test
    void listTest() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable reviewPageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.reviewAmount()
                , Sort.by("reviewGroupId").descending()
                        .and(Sort.by("reviewStep").ascending()));

        String productId = "BAGS20210629134401";

        Page<ProductReviewDTO> dto = productReviewRepository.findByProductId(productId, pageDTO, reviewPageable);

        System.out.println(dto.getContent());
    }
}
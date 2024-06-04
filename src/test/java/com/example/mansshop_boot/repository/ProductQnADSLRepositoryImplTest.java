package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnADTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductQnADSLRepositoryImplTest {

    @Autowired
    private ProductQnARepository productQnARepository;

    @Test
    void listTest() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Pageable qnaPageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.qnaAmount()
                , Sort.by("productQnAGroupId").descending()
                        .and(Sort.by("productQnAStep").ascending()));

        String productId = "BAGS20210629134401";

        Page<ProductQnADTO> dto = productQnARepository.findByProductId(productId, qnaPageable);

        System.out.println(dto.getContent());
    }
}
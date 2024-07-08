package com.example.mansshop_boot.domain.dto.product.in;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductQnA;

public record ProductQnAPostDTO(
        String productId
        , String content
) {

    public ProductQnA toProductQnAEntity(Member member, Product product) {
        return ProductQnA.builder()
                .member(member)
                .product(product)
                .qnaContent(this.content)
                .build();
    }
}

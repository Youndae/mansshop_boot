package com.example.mansshop_boot.domain.dto.product.in;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductQnA;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "상품 문의 작성 요청 데이터")
public record ProductQnAPostDTO(
        @Schema(name = "productId", description = "상품 아이디")
        String productId,
        @Schema(name = "content", description = "상품 문의 내용")
        String content
) {

    public ProductQnA toProductQnAEntity(Member member, Product product) {
        return ProductQnA.builder()
                .member(member)
                .product(product)
                .qnaContent(this.content)
                .build();
    }
}

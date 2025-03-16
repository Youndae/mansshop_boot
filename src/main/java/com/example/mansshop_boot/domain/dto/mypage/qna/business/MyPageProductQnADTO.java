package com.example.mansshop_boot.domain.dto.mypage.qna.business;

import com.example.mansshop_boot.domain.entity.ProductQnA;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record MyPageProductQnADTO(
        long productQnAId
        , String productName
        , String writer
        , String qnaContent
        , LocalDateTime createdAt
        , boolean productQnAStat
) {


    public MyPageProductQnADTO(ProductQnA productQnA, String writer) {

        this(
                productQnA.getId()
                , productQnA.getProduct().getProductName()
                , writer
                , productQnA.getQnaContent()
                , productQnA.getCreatedAt()
                , productQnA.isProductQnAStat()
        );
    }
}

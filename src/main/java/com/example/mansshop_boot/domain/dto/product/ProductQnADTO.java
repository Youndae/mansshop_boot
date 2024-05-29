package com.example.mansshop_boot.domain.dto.product;

import java.util.Date;

public record ProductQnADTO(
        String writer
        , String qnaContent
        , Date createdAt
        , int productQnAStep
) {
}

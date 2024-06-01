package com.example.mansshop_boot.domain.dto.product;

import java.util.Date;


public record ProductQnADTO(
        Long qnaId
        , String writer
        , String qnaContent
        , Date createdAt
        , int productQnAStat
) {
}

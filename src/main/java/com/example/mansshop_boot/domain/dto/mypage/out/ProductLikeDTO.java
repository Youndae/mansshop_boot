package com.example.mansshop_boot.domain.dto.mypage.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record ProductLikeDTO(
        Long likeId
        , String productId
        , String productName
        , int productPrice
        , String thumbnail
        , int stock
        , LocalDate createdAt
) {

    public ProductLikeDTO(Long likeId,
                          String productId,
                          String productName,
                          int productPrice,
                          String thumbnail,
                          int stock,
                          LocalDateTime createdAt) {
        this(
                likeId,
                productId,
                productName,
                productPrice,
                thumbnail,
                stock,
                createdAt.toLocalDate()
        );
    }
}

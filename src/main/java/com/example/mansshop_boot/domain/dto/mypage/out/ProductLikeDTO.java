package com.example.mansshop_boot.domain.dto.mypage.out;

import java.util.Date;

public record ProductLikeDTO(
        Long likeId
        , String productId
        , String productName
        , int productPrice
        , String thumbnail
        , int stock
        , Date createdAt
) {
}

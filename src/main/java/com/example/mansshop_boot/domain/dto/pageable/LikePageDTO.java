package com.example.mansshop_boot.domain.dto.pageable;

public record LikePageDTO(
        int pageNum
        , int likeAmount
) {

    public LikePageDTO(int pageNum) {
        this(pageNum, 10);
    }
}

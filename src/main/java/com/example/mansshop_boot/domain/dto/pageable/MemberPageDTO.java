package com.example.mansshop_boot.domain.dto.pageable;

import lombok.Builder;

public record MemberPageDTO(
        int pageNum
        , int mainProductAmount
        , String keyword
        , String classification
) {

    @Builder
    public MemberPageDTO(int pageNum
                        , String keyword
                        , String classification) {
        this(
                pageNum
                , 12
                , keyword == null ? null : "%" + keyword + "%"
                , classification
        );
    }
}

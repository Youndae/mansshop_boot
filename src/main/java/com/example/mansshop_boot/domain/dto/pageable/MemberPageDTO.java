package com.example.mansshop_boot.domain.dto.pageable;

import lombok.Builder;

public record MemberPageDTO(
        int pageNum
        , int mainProductAmount
        , String keyword
        , String classification
) {

    @Builder
    public MemberPageDTO(Long pageNum
                        , String keyword
                        , String classification) {
        this(
                pageNum == null ? 1 : pageNum.intValue()
                , 12
                , keyword == null ? null : "%" + keyword + "%"
                , classification
        );
    }
}

package com.example.mansshop_boot.domain.dto.mypage;

import lombok.Builder;

@Builder
public record MyPagePatchReviewDataDTO(
        long reviewId
        , String content
        , String productName
) {
}

package com.example.mansshop_boot.domain.dto.mypage.out;

import lombok.Builder;

@Builder
public record MyPagePatchReviewDataDTO(
        long reviewId,
        String content,
        String productName
) {
}

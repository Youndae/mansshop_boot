package com.example.mansshop_boot.domain.dto.mypage;

import lombok.Builder;

public record MyPagePatchReviewDTO(
        long reviewId
        , String content
) {
}

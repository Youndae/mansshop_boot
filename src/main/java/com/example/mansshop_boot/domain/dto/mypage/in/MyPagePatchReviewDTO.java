package com.example.mansshop_boot.domain.dto.mypage.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "회원 리뷰 수정 요청 데이터")
public record MyPagePatchReviewDTO(
        @Schema(name = "reviewId", description = "리뷰 아이디")
        long reviewId,
        @Schema(name = "content", description = "리뷰 내용")
        String content
) {
}

package com.example.mansshop_boot.domain.dto.admin.in;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "관리자의 리뷰 답변 작성 요청 데이터")
public record AdminReviewRequestDTO(
        @Schema(name = "reviewId", description = "리뷰 아이디")
        long reviewId,
        @Schema(name = "content", description = "리뷰 답변 내용")
        String content
) {
}

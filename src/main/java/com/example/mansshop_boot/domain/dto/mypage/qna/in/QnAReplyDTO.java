package com.example.mansshop_boot.domain.dto.mypage.qna.in;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "문의 답변 수정 요청 데이터")
public record QnAReplyDTO(
        @Schema(name = "replyId", description = "답변 아이디")
        long replyId,
        @Schema(name = "content", description = "수정 내용")
        String content
) {
}

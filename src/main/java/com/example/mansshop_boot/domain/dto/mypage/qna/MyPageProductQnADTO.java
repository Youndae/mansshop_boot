package com.example.mansshop_boot.domain.dto.mypage.qna;

import java.time.LocalDate;

public record MyPageProductQnADTO(
        long productQnAId
        , String productName
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , boolean productQnAStat
) {
}

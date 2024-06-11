package com.example.mansshop_boot.domain.dto.mypage;

import java.time.LocalDate;

public record MyPageProductQnADTO(
        long productQnAId
        , String productName
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , int productQnAStat
) {
}

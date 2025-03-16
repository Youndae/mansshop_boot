package com.example.mansshop_boot.domain.dto.mypage.qna.business;

import com.example.mansshop_boot.domain.entity.MemberQnA;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberQnADTO(
        long memberQnAId
        , String qnaClassification
        , String qnaTitle
        , String writer
        , String qnaContent
        , LocalDateTime updatedAt
        , boolean memberQnAStat
) {

    public MemberQnADTO(MemberQnA memberQnA, String writer) {
        this(
                memberQnA.getId()
                , memberQnA.getQnAClassification().getQnaClassificationName()
                , memberQnA.getMemberQnATitle()
                , writer
                , memberQnA.getMemberQnAContent()
                , memberQnA.getUpdatedAt()
                , memberQnA.isMemberQnAStat()
        );
    }
}

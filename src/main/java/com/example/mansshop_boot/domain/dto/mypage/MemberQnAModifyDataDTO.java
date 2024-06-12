package com.example.mansshop_boot.domain.dto.mypage;

import com.example.mansshop_boot.domain.entity.MemberQnA;

import java.util.List;

public record MemberQnAModifyDataDTO(
        long qnaId
        , String qnaTitle
        , String qnaContent
        , long qnaClassificationId
        , List<QnAClassificationDTO> classificationList
) {
    public MemberQnAModifyDataDTO(MemberQnA memberQnA,  List<QnAClassificationDTO> classificationList) {
        this(
                memberQnA.getId()
                , memberQnA.getMemberQnATitle()
                , memberQnA.getMemberQnAContent()
                , memberQnA.getQnAClassification().getId()
                , classificationList
        );
    }
}

package com.example.mansshop_boot.domain.dto.mypage.qna.out;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;

import java.time.LocalDate;
import java.util.List;

public record MemberQnADetailDTO(
        long memberQnAId
        , String qnaClassification
        , String qnaTitle
        , String writer
        , String qnaContent
        , LocalDate updatedAt
        , boolean memberQnAStat
        , List<MyPageQnAReplyDTO> replyList
) {

    public MemberQnADetailDTO(MemberQnADTO memberQnADTO, List<MyPageQnAReplyDTO> replyList) {
        this(
                memberQnADTO.memberQnAId()
                , memberQnADTO.qnaClassification()
                , memberQnADTO.qnaTitle()
                , memberQnADTO.writer()
                , memberQnADTO.qnaContent()
                , memberQnADTO.updatedAt()
                , memberQnADTO.memberQnAStat()
                , replyList
        );
    }
}

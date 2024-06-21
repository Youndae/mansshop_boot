package com.example.mansshop_boot.domain.dto.mypage.qna;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;

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
        , UserStatusDTO userStatus
) {

    public MemberQnADetailDTO(MemberQnADTO memberQnADTO, List<MyPageQnAReplyDTO> replyList, String nickname) {
        this(
                memberQnADTO.memberQnAId()
                , memberQnADTO.qnaClassification()
                , memberQnADTO.qnaTitle()
                , memberQnADTO.writer()
                , memberQnADTO.qnaContent()
                , memberQnADTO.updatedAt()
                , memberQnADTO.memberQnAStat()
                , replyList
                , new UserStatusDTO(nickname)
        );
    }
}

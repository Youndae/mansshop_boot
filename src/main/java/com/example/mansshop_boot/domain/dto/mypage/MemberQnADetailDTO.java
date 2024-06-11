package com.example.mansshop_boot.domain.dto.mypage;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;

import java.time.LocalDate;
import java.util.List;

public record MemberQnADetailDTO(
        long memberQnAId
        , String qnaClassification
        , String writer
        , String qnaContent
        , LocalDate updatedAt
        , int memberQnAStat
        , List<MemberQnAReplyDTO> replyList
        , UserStatusDTO userStatus
) {

    public MemberQnADetailDTO(MemberQnADTO memberQnADTO, List<MemberQnAReplyDTO> replyList, String nickname) {
        this(
                memberQnADTO.memberQnAId()
                , memberQnADTO.qnaClassification()
                , memberQnADTO.writer()
                , memberQnADTO.qnaContent()
                , memberQnADTO.updatedAt()
                , memberQnADTO.memberQnAStat()
                , replyList
                , new UserStatusDTO(nickname)
        );
    }
}

package com.example.mansshop_boot.domain.dto.mypage.qna;

import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;

import java.time.LocalDate;
import java.util.List;

public record ProductQnADetailDTO(
        long productQnAId
        , String productName
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , int productQnAStat
        , List<MyPageQnAReplyDTO> replyList
        , UserStatusDTO userStatus
) {

    public ProductQnADetailDTO(MyPageProductQnADTO qnaDTO
                                , List<MyPageQnAReplyDTO> replyList
                                , String nickname) {
        this(
                qnaDTO.productQnAId()
                , qnaDTO.productName()
                , qnaDTO.writer()
                , qnaDTO.qnaContent()
                , qnaDTO.createdAt()
                , qnaDTO.productQnAStat()
                , replyList
                , new UserStatusDTO(nickname)
        );
    }
}

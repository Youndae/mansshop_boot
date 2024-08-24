package com.example.mansshop_boot.domain.dto.mypage.qna.out;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;

import java.time.LocalDate;
import java.util.List;

public record ProductQnADetailDTO(
        long productQnAId
        , String productName
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , boolean productQnAStat
        , List<MyPageQnAReplyDTO> replyList
) {

    public ProductQnADetailDTO(MyPageProductQnADTO qnaDTO
                                , List<MyPageQnAReplyDTO> replyList) {
        this(
                qnaDTO.productQnAId()
                , qnaDTO.productName()
                , qnaDTO.writer()
                , qnaDTO.qnaContent()
                , qnaDTO.createdAt()
                , qnaDTO.productQnAStat()
                , replyList
        );
    }
}

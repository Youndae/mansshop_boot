package com.example.mansshop_boot.domain.dto.product.business;

import com.example.mansshop_boot.domain.entity.ProductQnAReply;


import java.time.LocalDate;
import java.util.Date;


public record ProductQnAReplyDTO(
        String writer
        , String content
        , LocalDate createdAt
) {

    public ProductQnAReplyDTO (ProductQnAReply qnAReply) {
        this(
                qnAReply.getMember().getNickname() == null
                        ? qnAReply.getMember().getUserName() : qnAReply.getMember().getNickname()
                , qnAReply.getReplyContent()
                , qnAReply.getCreatedAt()
        );
    }
}

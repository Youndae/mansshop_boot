package com.example.mansshop_boot.domain.dto.product;

import java.util.Date;
import java.util.List;


public record ProductQnAResponseDTO(
        Long qnaId
        , String writer
        , String qnaContent
        , Date createdAt
        , int productQnAStat
        , List<ProductQnAReplyDTO> replyList
) {
    public ProductQnAResponseDTO(ProductQnADTO dto, List<ProductQnAReplyDTO> replyList){
        this(
                dto.qnaId()
                , dto.writer()
                , dto.qnaContent()
                , dto.createdAt()
                , dto.productQnAStat()
                , replyList
        );
    }
}
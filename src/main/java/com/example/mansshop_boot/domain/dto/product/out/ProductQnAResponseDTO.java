package com.example.mansshop_boot.domain.dto.product.out;

import com.example.mansshop_boot.domain.dto.product.business.ProductQnADTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductQnAReplyDTO;

import java.time.LocalDate;
import java.util.List;


public record ProductQnAResponseDTO(
        Long qnaId
        , String writer
        , String qnaContent
        , LocalDate createdAt
        , boolean productQnAStat
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

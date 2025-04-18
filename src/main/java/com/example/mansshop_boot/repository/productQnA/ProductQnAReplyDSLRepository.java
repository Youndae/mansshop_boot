package com.example.mansshop_boot.repository.productQnA;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductQnAReplyListDTO;
import com.example.mansshop_boot.domain.entity.ProductQnAReply;

import java.util.List;

public interface ProductQnAReplyDSLRepository {

    List<ProductQnAReplyListDTO> getQnAReply(List<Long> qnaIdList);

    List<MyPageQnAReplyDTO> findAllByQnAId(long productQnAId);
}

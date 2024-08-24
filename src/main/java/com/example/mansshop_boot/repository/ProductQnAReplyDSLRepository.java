package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.entity.ProductQnAReply;

import java.util.List;

public interface ProductQnAReplyDSLRepository {

    List<ProductQnAReply> getQnAReply(List<Long> qnaIdList);

    List<MyPageQnAReplyDTO> findAllByQnAId(long productQnAId);
}

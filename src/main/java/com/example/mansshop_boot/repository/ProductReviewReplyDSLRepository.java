package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductReviewReply;

import java.util.List;

public interface ProductReviewReplyDSLRepository {

    List<ProductReviewReply> getReplyList(List<Long> reviewIdList);
}

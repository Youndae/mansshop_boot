package com.example.mansshop_boot.repository.productReview;

import com.example.mansshop_boot.domain.entity.ProductReviewReply;

import java.util.List;

public interface ProductReviewReplyDSLRepository {
    ProductReviewReply findByReviewId(Long reviewId);
}

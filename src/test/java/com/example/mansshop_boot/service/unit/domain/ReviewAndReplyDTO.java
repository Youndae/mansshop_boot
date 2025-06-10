package com.example.mansshop_boot.service.unit.domain;

import com.example.mansshop_boot.domain.entity.ProductReview;
import com.example.mansshop_boot.domain.entity.ProductReviewReply;

public record ReviewAndReplyDTO(
        ProductReview productReview,
        ProductReviewReply productReviewReply
) {
}

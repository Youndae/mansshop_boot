package com.example.mansshop_boot.repository.productReview;

import com.example.mansshop_boot.domain.entity.ProductReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewReplyRepository extends JpaRepository<ProductReviewReply, Long>, ProductReviewReplyDSLRepository {
}

package com.example.mansshop_boot.repository.productReview;

import com.example.mansshop_boot.domain.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long>, ProductReviewDSLRepository {
}

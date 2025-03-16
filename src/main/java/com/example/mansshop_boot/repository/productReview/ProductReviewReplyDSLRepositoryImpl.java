package com.example.mansshop_boot.repository.productReview;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@Slf4j
public class ProductReviewReplyDSLRepositoryImpl implements ProductReviewReplyDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

}

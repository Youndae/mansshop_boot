package com.example.mansshop_boot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.example.mansshop_boot.domain.entity.QProductReviewReply.productReviewReply;

@Repository
@AllArgsConstructor
@Slf4j
public class ProductReviewReplyDSLRepositoryImpl implements ProductReviewReplyDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

}

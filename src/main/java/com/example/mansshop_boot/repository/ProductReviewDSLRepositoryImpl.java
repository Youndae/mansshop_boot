package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MyPageReviewDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductReview.productReview;
import static com.example.mansshop_boot.domain.entity.QProductReviewReply.productReviewReply;
import static com.example.mansshop_boot.domain.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductReviewDSLRepositoryImpl implements ProductReviewDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductReviewDTO> findByProductId(String productId, Pageable pageable) {

        List<ProductReviewDTO> list = jpaQueryFactory
                                            .select(
                                                    Projections.constructor(
                                                            ProductReviewDTO.class
                                                            , new CaseBuilder()
                                                                    .when(productReview.member.nickname.isNull())
                                                                    .then(productReview.member.userName)
                                                                    .otherwise(productReview.member.nickname)
                                                                    .as("reviewWriter")
                                                            , productReview.reviewContent
                                                            , productReview.createdAt.as("reviewCreatedAt")
                                                            , productReviewReply.replyContent.as("answerContent")
                                                            , productReviewReply.createdAt.as("answerCreatedAt")
                                                    )
                                            )
                                            .from(productReview)
                                            .leftJoin(productReviewReply)
                                            .on(productReview.id.eq(productReviewReply.productReview.id))
                                            .where(productReview.product.id.eq(productId))
                                            .orderBy(productReview.createdAt.desc())
                                            .orderBy(productReview.id.desc())
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productReview.count())
                .from(productReview)
                .where(productReview.product.id.eq(productId));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Page<MyPageReviewDTO> findAllByUserId(String userId, Pageable pageable) {

        List<MyPageReviewDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        MyPageReviewDTO.class
                        , productReview.id.as("reviewId")
                        , product.thumbnail
                        , product.productName
                        , productReview.reviewContent.as("content")
                        , productReview.createdAt
                        , productReview.updatedAt
                        , productReviewReply.replyContent
                        , productReviewReply.updatedAt.as("replyUpdatedAt")
                )
        )
                .from(productReview)
                .innerJoin(product)
                .on(productReview.product.id.eq(product.id))
                .leftJoin(productReviewReply)
                .on(productReviewReply.productReview.id.eq(productReview.id))
                .where(productReview.member.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(productReview.id.desc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productReview.countDistinct())
                .from(productReview)
                .where(productReview.member.userId.eq(userId));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }
}

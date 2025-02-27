package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.MyPageReviewDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
import static com.example.mansshop_boot.domain.entity.QMember.member;

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
                                                            , ExpressionUtils.as(
                                                                    JPAExpressions.select(new CaseBuilder()
                                                                            .when(productReview.member.nickname.isNull())
                                                                            .then(productReview.member.userName)
                                                                            .otherwise(productReview.member.nickname))
                                                                            .from(member)
                                                                            .where(member.userId.eq(productReview.member.userId)), "reviewWriter"
                                                            )
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

        JPAQuery<Long> count = jpaQueryFactory.select(productReview.countDistinct())
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

    @Override
    public List<AdminReviewDTO> findAllByAdminReviewList(AdminOrderPageDTO pageDTO, String listType) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminReviewDTO.class
                                , productReview.id.as("reviewId")
                                /*, ExpressionUtils.as(
                                        JPAExpressions.select(product.productName)
                                                .from(product)
                                                .where(product.id.eq(productReview.product.id)), "productName"
                                )*/
                                , product.productName
                                , new CaseBuilder()
                                        .when(productReview.member.nickname.isNull())
                                        .then(productReview.member.userName)
                                        .otherwise(productReview.member.nickname)
                                        .as("writer")
                                , productReview.updatedAt.as("updatedAt")
                                , productReview.status
                        )
                )
                .from(productReview)
                .innerJoin(product)
                .on(productReview.product.id.eq(product.id))
                .where(adminReviewListSearch(pageDTO, listType))
                .orderBy(productReview.updatedAt.desc())
                .offset(pageDTO.offset())
                .limit(pageDTO.amount())
                .fetch();
    }

    @Override
    public Long countByAdminReviewList(AdminOrderPageDTO pageDTO, String listType) {
        return jpaQueryFactory.select(productReview.countDistinct())
                .from(productReview)
                .where(adminReviewListSearch(pageDTO, listType))
                .fetchOne();
    }

    public BooleanExpression adminReviewListSearch(AdminOrderPageDTO pageDTO, String listType) {
        BooleanExpression response = Expressions.asBoolean(true).isTrue();
        if(pageDTO.searchType() != null) {
            String keyword = String.format("%%%s%%", pageDTO.keyword());
            if(pageDTO.searchType().equals("user"))
                response = response.and(
                                    productReview.member.userName.like(keyword)
                                            .or(productReview.member.nickname.like(keyword))
                            );
            else if(pageDTO.searchType().equals("product"))
                response = response.and(productReview.product.productName.like(keyword));
        }

        if(listType.equals(AdminListType.NEW.name())) {
            response = response.and(productReview.status.eq(false));
        }

        return response.equals(Expressions.asBoolean(true).isTrue()) ? null : response;
    }

    @Override
    public AdminReviewDetailDTO findByAdminReviewDetail(long reviewId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminReviewDetailDTO.class
                        , productReview.id.as("reviewId")
                        , productReview.product.productName
                        , productReview.productOption.size
                        , productReview.productOption.color
                        , new CaseBuilder()
                                .when(productReview.member.nickname.isNull())
                                .then(productReview.member.userName)
                                .otherwise(productReview.member.nickname)
                                .as("writer")
                        , productReview.createdAt
                        , productReview.updatedAt
                        , productReview.reviewContent.as("content")
                        , productReviewReply.updatedAt.as("replyUpdatedAt")
                        , productReviewReply.replyContent
                )
        )
                .from(productReview)
                .leftJoin(productReviewReply)
                .on(productReviewReply.productReview.id.eq(productReview.id))
                .where(productReview.id.eq(reviewId))
                .fetchOne();
    }
}

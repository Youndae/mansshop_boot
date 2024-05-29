package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnADTO;
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

import static com.example.mansshop_boot.domain.entity.QProductQnA.productQnA;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductQnADSLRepositoryImpl implements ProductQnADSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductQnADTO> findByProductId(String productId, ProductDetailPageDTO pageDTO, Pageable pageable) {

        List<ProductQnADTO> list = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ProductQnADTO.class
                                , new CaseBuilder()
                                        .when(productQnA.member.nickname.isNull())
                                        .then(productQnA.member.userName)
                                        .otherwise(productQnA.member.nickname)
                                        .as("writer")
                                , productQnA.qnaContent
                                , productQnA.createdAt
                                , productQnA.productQnAStep
                        )
                )
                .from(productQnA)
                .where(productQnA.product.id.eq(productId))
                .orderBy(productQnA.productQnAGroupId.desc())
                .orderBy(productQnA.productQnAStep.asc())
                .offset((pageDTO.pageNum() - 1) * pageDTO.qnaAmount())
                .limit(pageDTO.qnaAmount())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productQnA.count())
                .from(productQnA)
                .where(productQnA.product.id.eq(productId));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }
}

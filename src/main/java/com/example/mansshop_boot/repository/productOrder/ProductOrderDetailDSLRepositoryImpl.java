package com.example.mansshop_boot.repository.productOrder;

import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductOrderDetail.productOrderDetail;
import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;
import static com.example.mansshop_boot.domain.entity.QProductOrder.productOrder;
import static com.example.mansshop_boot.domain.entity.QClassification.classification;

@Repository
@RequiredArgsConstructor
public class ProductOrderDetailDSLRepositoryImpl implements ProductOrderDetailDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MyPageOrderDetailDTO> findByDetailList(List<Long> orderIdList) {

        return jpaQueryFactory.select(
                Projections.constructor(
                        MyPageOrderDetailDTO.class
                        , productOrder.id.as("orderId")
                        , product.id.as("productId")
                        , productOrderDetail.productOption.id.as("optionId")
                        , productOrderDetail.id.as("detailId")
                        , product.productName
                        , productOption.size
                        , productOption.color
                        , productOrderDetail.orderDetailCount.as("detailCount")
                        , productOrderDetail.orderDetailPrice.as("detailPrice")
                        , productOrderDetail.orderReviewStatus.as("reviewStatus")
                        , product.thumbnail
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .innerJoin(productOption)
                .on(productOrderDetail.productOption.id.eq(productOption.id))
                .innerJoin(product)
                .on(productOrderDetail.product.id.eq(product.id))
                .where(productOrderDetail.productOrder.id.in(orderIdList))
                .orderBy(productOrderDetail.product.id.asc())
                .fetch();
    }

    @Override
    public List<AdminOrderDetailListDTO> findByOrderIds(List<Long> orderIdList) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminOrderDetailListDTO.class,
                        productOrder.id.as("orderId"),
                        classification.id.as("classification"),
                        product.productName,
                        productOption.size,
                        productOption.color,
                        productOrderDetail.orderDetailCount.as("count"),
                        productOrderDetail.orderDetailPrice.as("price"),
                        productOrderDetail.orderReviewStatus.as("reviewStatus")
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrderDetail.product, product)
                .innerJoin(productOrderDetail.productOption, productOption)
                .innerJoin(productOrderDetail.productOrder, productOrder)
                .innerJoin(product.classification, classification)
                .where(productOrderDetail.productOrder.id.in(orderIdList))
                .orderBy(productOrder.id.desc())
                .fetch();
    }

    public BooleanExpression searchOption(int year) {
        if(year == 0)
            return null;
        else
            return productOrder.createdAt.year().eq(year);
    }
}

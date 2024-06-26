package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                .on(productOption.product.id.eq(product.id))
                .where(productOrderDetail.productOrder.id.in(orderIdList))
                .fetch();
    }

    @Override
    public List<AdminBestSalesProductDTO> findPeriodBestProduct(LocalDateTime startDate, LocalDateTime endDate) {

        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "productPeriodSalesQuantity");

        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminBestSalesProductDTO.class
                        , product.productName
                        , ExpressionUtils.as(productOrderDetail.orderDetailCount.longValue().sum(), aliasQuantity)
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("productPeriodSales")
                )
        )
                .from(productOrderDetail)
                .innerJoin(product)
                .on(productOrderDetail.product.id.eq(product.id))
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .where(productOrder.createdAt.between(startDate, endDate))
                .groupBy(product.productName)
                .orderBy(aliasQuantity.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<AdminPeriodClassificationDTO> findPeriodClassification(LocalDateTime startDate, LocalDateTime endDate) {

        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminPeriodClassificationDTO.class
                        , classification.id.as("classification")
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("classificationSales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("classificationSalesQuantity")
                )
        )
                .from(productOrderDetail)
                .innerJoin(product)
                .on(productOrderDetail.product.id.eq(product.id))
                .innerJoin(classification)
                .on(product.classification.id.eq(classification.id))
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .where(productOrder.createdAt.between(startDate, endDate))
                .groupBy(classification.id)
                .fetch();
    }

    @Override
    public AdminClassificationSalesDTO findPeriodClassificationSales(LocalDateTime startDate, LocalDateTime endDate, String classification) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminClassificationSalesDTO.class
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("sales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("salesQuantity")
                        , productOrder.id.countDistinct().as("orderQuantity")
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .where(productOrderDetail.product.id.startsWith(classification).and(productOrder.createdAt.between(startDate, endDate)))
                .fetchOne();
    }

    @Override
    public List<AdminClassificationSalesProductListDTO> findPeriodClassificationProductSales(LocalDateTime startDate, LocalDateTime endDate, String classification) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminClassificationSalesProductListDTO.class
                        , product.productName
                        , productOption.size
                        , productOption.color
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("productSales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("productSalesQuantity")
                )
        )
                .from(productOrderDetail)
                .innerJoin(product)
                .on(productOrderDetail.product.id.eq(product.id))
                .innerJoin(productOption)
                .on(productOrderDetail.productOption.id.eq(productOption.id))
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .where(productOrderDetail.product.id.startsWith(classification).and(productOrder.createdAt.between(startDate, endDate)))
                .groupBy(product.productName)
                .fetch();
    }

    @Override
    public List<ProductOrderDetail> findByOrderIds(List<Long> orderIdList) {
        return jpaQueryFactory.select(productOrderDetail)
                .from(productOrderDetail)
                .where(productOrderDetail.productOrder.id.in(orderIdList))
                .orderBy(productOrderDetail.productOrder.id.desc())
                .fetch();
    }

    @Override
    public List<AdminProductSalesOptionDTO> getProductOptionSales(int year, String productId) {
        /*return jpaQueryFactory.select(
                Projections.constructor(
                        AdminProductSalesOptionDTO.class
                        , productOption.id.as("optionId")
                        , productOption.size
                        , productOption.color
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("optionSales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("optionSalesQuantity")
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .innerJoin(productOption)
                .on(productOrderDetail.productOption.id.eq(productOption.id))
                .where(productOrderDetail.product.id.eq(productId).and(searchOption(year)))
                .orderBy(productOption.id.asc())
                .groupBy(productOption.id)
                .fetch();*/

        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminProductSalesOptionDTO.class
                                , productOption.id.as("optionId")
                                , productOption.size
                                , productOption.color
                                , productOrderDetail.orderDetailPrice.longValue().sum().as("optionSales")
                                , productOrderDetail.orderDetailCount.longValue().sum().as("optionSalesQuantity")
                        )
                )
                .from(productOption)
                .leftJoin(productOrderDetail)
                .on(productOption.id.eq(productOrderDetail.productOption.id))
                .innerJoin(productOrder)
                .on(productOrder.id.eq(productOrderDetail.productOrder.id))
                .where(productOrderDetail.product.id.eq(productId).and(searchOption(year)))
                .orderBy(productOption.id.asc())
                .groupBy(productOption.id)
                .fetch();
    }

    public BooleanExpression searchOption(int year) {
        if(year == 0)
            return null;
        else
            return productOrder.createdAt.year().eq(year);
    }
}

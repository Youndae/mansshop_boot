package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumuration.OrderStatus;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductOrder.productOrder;
import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductOrderDetail.productOrderDetail;

@Repository
@RequiredArgsConstructor
public class ProductOrderDSLRepositoryImpl implements ProductOrderDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Value("#{jwt['cookie.cart.uid']}")
    private String nonMemberId;

    @Override
    public Page<ProductOrder> findByUserId(MemberOrderDTO memberOrderDTO, OrderPageDTO pageDTO, Pageable pageable) {

        List<ProductOrder> list = jpaQueryFactory.select(productOrder)
                .from(productOrder)
                .where(search(memberOrderDTO, pageDTO))
                .orderBy(productOrder.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productOrder.count())
                .from(productOrder)
                .where(search(memberOrderDTO, pageDTO));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression search(MemberOrderDTO memberOrderDTO, OrderPageDTO pageDTO) {
        LocalDateTime term = LocalDateTime.now();

        if(pageDTO.term().equals("all"))
            term = LocalDateTime.MIN;
        else
            term = term.minusMonths(Long.parseLong(pageDTO.term()));

        if(memberOrderDTO.userId() == null) {
            return productOrder.recipient
                    .eq(memberOrderDTO.recipient())
                    .and(productOrder.orderPhone.eq(memberOrderDTO.phone()))
                    .and(productOrder.member.userId.eq(nonMemberId))
                    .and(productOrder.createdAt.gt(term));
        }else
            return productOrder.member.userId.eq(memberOrderDTO.userId())
                    .and(productOrder.createdAt.gt(term));
    }

    @Override
    public Page<AdminOrderDTO> findAllOrderList(AdminOrderPageDTO pageDTO, Pageable pageable) {

        List<AdminOrderDTO> list = jpaQueryFactory.select(getFindAllOrderListSelect())
                                                .from(productOrder)
                                                .where(searchAdminOrder(pageDTO))
                                                .orderBy(productOrder.createdAt.desc())
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productOrder.countDistinct())
                                            .from(productOrder)
                                            .where(searchAdminOrder(pageDTO));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Page<AdminOrderDTO> findAllNewOrderList(AdminOrderPageDTO pageDTO, LocalDateTime todayLastOrderTime, Pageable pageable) {

        List<AdminOrderDTO> list = jpaQueryFactory.select(getFindAllOrderListSelect())
                                                .from(productOrder)
                                                .where(
                                                        productOrder.createdAt.loe(todayLastOrderTime)
                                                                .and(productOrder.orderStat.eq(OrderStatus.ORDER.getStatusStr()))
                                                                .and(searchAdminOrder(pageDTO))
                                                )
                                                .orderBy(productOrder.createdAt.desc())
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productOrder.countDistinct())
                                            .from(productOrder)
                                            .where(
                                                    productOrder.createdAt.loe(todayLastOrderTime)
                                                            .and(productOrder.orderStat.eq(OrderStatus.ORDER.getStatusStr()))
                                                            .and(searchAdminOrder(pageDTO))
                                            );

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    public Expression<AdminOrderDTO> getFindAllOrderListSelect() {
        return Projections.constructor(
                        AdminOrderDTO.class
                        , productOrder.id.as("orderId")
                        , productOrder.recipient
                        , new CaseBuilder()
                                .when(productOrder.member.userId.eq("Anonymous"))
                                .then("비회원")
                                .otherwise(productOrder.member.userId)
                                .as("userId")
                        , productOrder.orderPhone.as("phone")
                        , productOrder.createdAt
                        , productOrder.orderAddress.as("address")
                        , productOrder.orderStat.as("orderStatus")
                );
    }

    public BooleanExpression searchAdminOrder(AdminOrderPageDTO pageDTO) {
        if(pageDTO.searchType() == null)
            return null;
        else if(pageDTO.searchType().equals("recipient"))
            return productOrder.recipient.eq(pageDTO.keyword());
        else if(pageDTO.searchType().equals("userId"))
            return productOrder.member.userId.eq(pageDTO.keyword());

        return null;
    }

    @Override
    public List<AdminPeriodSalesListDTO> findPeriodList(int year) {

        List<AdminPeriodSalesListDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminPeriodSalesListDTO.class
                        , productOrder.createdAt.month().as("date")
                        , productOrder.orderTotalPrice.longValue().sum().as("sales")
                        , productOrder.productCount.longValue().sum().as("salesQuantity")
                        , productOrder.id.countDistinct().as("orderQuantity")
                )
        )
                .from(productOrder)
                .where(productOrder.createdAt.year().eq(year))
                .groupBy(productOrder.createdAt.month())
                .fetch();


        return list;
    }

    @Override
    public AdminPeriodSalesStatisticsDTO findPeriodStatistics(LocalDateTime startDate, LocalDateTime endDate) {

        AdminPeriodSalesStatisticsDTO result = jpaQueryFactory.select(
                        Projections.constructor(
                                AdminPeriodSalesStatisticsDTO.class
                                , productOrder.orderTotalPrice.longValue().sum().as("monthSales")
                                , productOrder.productCount.longValue().sum().as("monthSalesQuantity")
                                , productOrder.id.countDistinct().as("monthOrderQuantity")
                                , productOrder.deliveryFee.longValue().sum().as("monthDeliveryFee")
                                , new CaseBuilder()
                                        .when(productOrder.paymentType.eq("cash"))
                                        .then(productOrder.orderTotalPrice)
                                        .otherwise(0)
                                        .longValue()
                                        .sum()
                                        .as("cashTotalPrice")
                                , new CaseBuilder()
                                        .when(productOrder.paymentType.eq("card"))
                                        .then(productOrder.orderTotalPrice)
                                        .otherwise(0)
                                        .longValue()
                                        .sum()
                                        .as("cardTotalPrice")
                        )
                )
                .from(productOrder)
                .where(productOrder.createdAt.between(startDate, endDate))
                .fetchOne();

        return result;
    }

    @Override
    public List<AdminPeriodSalesListDTO> findPeriodDailyList(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminPeriodSalesListDTO.class
                        , productOrder.createdAt.dayOfMonth().as("date")
                        , productOrder.orderTotalPrice.longValue().sum().as("sales")
                        , productOrder.productCount.longValue().sum().as("salesQuantity")
                        , productOrder.id.countDistinct().as("orderQuantity")
                )
        )
                .from(productOrder)
                .where(productOrder.createdAt.between(startDate, endDate))
                .groupBy(productOrder.createdAt.dayOfMonth())
                .fetch();
    }

    @Override
    public AdminClassificationSalesDTO findDailySales(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminClassificationSalesDTO.class
                                , productOrder.orderTotalPrice.longValue().sum().as("sales")
                                , productOrder.productCount.longValue().sum().as("salesQuantity")
                                , productOrder.id.countDistinct().as("orderQuantity")
                        )
                )
                .from(productOrder)
                .where(productOrder.createdAt.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public Page<ProductOrder> findAllByDay(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        List<ProductOrder> list = jpaQueryFactory.select(productOrder)
                                        .from(productOrder)
                                        .where(productOrder.createdAt.between(startDate, endDate))
                                        .orderBy(productOrder.createdAt.desc())
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productOrder.id.countDistinct())
                                        .from(productOrder)
                                        .where(productOrder.createdAt.between(startDate, endDate));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO, Pageable pageable) {

        List<AdminProductSalesListDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminProductSalesListDTO.class
                        , product.classification.id.as("classification")
                        , product.id.as("productId")
                        , product.productName
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("sales")
                        , product.productSales.as("salesQuantity")
                )
        )
                .from(product)
                .innerJoin(productOrderDetail)
                .on(productOrderDetail.product.id.eq(product.id))
                .where(searchSales(pageDTO))
                .orderBy(product.classification.classificationStep.asc())
                .groupBy(product.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(product.countDistinct())
                                .from(product)
                                .where(searchSales(pageDTO));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    public BooleanExpression searchSales(AdminPageDTO pageDTO) {
        if(pageDTO.keyword() != null)
            return product.productName.eq(pageDTO.keyword());
        else
            return null;
    }

    @Override
    public AdminProductSalesDTO getProductSales(String productId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminProductSalesDTO.class
                        , product.productName
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("sales")
                        , product.productSales.as("salesQuantity")
                )
        )
                .from(product)
                .innerJoin(productOrderDetail)
                .on(productOrderDetail.product.id.eq(product.id))
                .where(product.id.eq(productId))
                .fetchOne();
    }

    @Override
    public AdminSalesDTO getProductPeriodSales(int year, String productId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminSalesDTO.class
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("sales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("salesQuantity")
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .where(productOrderDetail.product.id.eq(productId).and(productOrder.createdAt.year().eq(year)))
                .fetchOne();
    }

    @Override
    public List<AdminPeriodSalesListDTO> getProductMonthPeriodSales(int year, String productId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminPeriodSalesListDTO.class
                        , productOrder.createdAt.month().as("date")
                        , productOrderDetail.orderDetailPrice.longValue().sum().as("sales")
                        , productOrderDetail.orderDetailCount.longValue().sum().as("salesQuantity")
                        , productOrder.id.countDistinct().as("orderQuantity")
                )
        )
                .from(productOrder)
                .innerJoin(productOrderDetail)
                .on(productOrder.id.eq(productOrderDetail.productOrder.id))
                .where(productOrder.createdAt.year().eq(year).and(productOrderDetail.product.id.eq(productId)))
                .groupBy(productOrder.createdAt.month())
                .fetch();
    }
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

        List<AdminOrderDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminOrderDTO.class
                        , productOrder.id.as("orderId")
                        , productOrder.recipient
                        , productOrder.member.userId.as("userId")
                        , productOrder.orderPhone.as("phone")
                        , productOrder.createdAt
                        , productOrder.orderAddress.as("address")
                )
        )
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

        List<AdminOrderDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminOrderDTO.class
                        , productOrder.id.as("orderId")
                        , productOrder.recipient
                        , productOrder.member.userId.as("userId")
                        , productOrder.orderPhone.as("phone")
                        , productOrder.createdAt
                        , productOrder.orderAddress.as("address")
                )
        )
                .from(productOrder)
                .where(productOrder.createdAt.lt(todayLastOrderTime))
                .orderBy(productOrder.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productOrder.countDistinct())
                .from(productOrder)
                .where(searchAdminOrder(pageDTO));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    public BooleanExpression searchAdminOrder(AdminOrderPageDTO pageDTO) {
        if(pageDTO.searchType().equals("recipient"))
            return productOrder.recipient.eq(pageDTO.keyword());
        else if(pageDTO.searchType().equals("userId"))
            return productOrder.member.userId.eq(pageDTO.keyword());
        else
            return null;
    }
}

package com.example.mansshop_boot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.example.mansshop_boot.domain.entity.QOrderDetail.orderDetail;

@Repository
@RequiredArgsConstructor
public class OrderDetailDSLRepositoryImpl implements OrderDetailDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;
}

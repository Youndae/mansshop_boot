package com.example.mansshop_boot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductOrderDetailDSLRepositoryImpl implements ProductOrderDetailDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.PeriodSales;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.mansshop_boot.domain.entity.QPeriodSales.periodSales;

@Repository
@RequiredArgsConstructor
public class PeriodSalesDSLRepositoryImpl implements PeriodSalesDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PeriodSales findPeriodLastUpdate() {


        return jpaQueryFactory.select(periodSales)
                .from(periodSales)
                .orderBy(periodSales.period.desc())
                .fetchOne();
    }
}

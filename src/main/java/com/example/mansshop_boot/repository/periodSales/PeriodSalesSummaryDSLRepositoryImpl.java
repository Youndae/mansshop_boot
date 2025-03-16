package com.example.mansshop_boot.repository.periodSales;

import com.example.mansshop_boot.domain.dto.admin.business.AdminBestSalesProductDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminClassificationSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesStatisticsDTO;
import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QPeriodSalesSummary.periodSalesSummary;

@Repository
@RequiredArgsConstructor
public class PeriodSalesSummaryDSLRepositoryImpl implements PeriodSalesSummaryDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AdminPeriodSalesListDTO> findPeriodList(int year) {

        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminPeriodSalesListDTO.class,
                                periodSalesSummary.period.month().as("date"),
                                periodSalesSummary.sales.longValue().sum().as("sales"),
                                periodSalesSummary.salesQuantity.longValue().sum().as("salesQuantity"),
                                periodSalesSummary.orderQuantity.longValue().sum().as("orderQuantity")
                        )
                )
                .from(periodSalesSummary)
                .where(periodSalesSummary.period.year().eq(year))
                .groupBy(periodSalesSummary.period.month())
                .fetch();
    }

    @Override
    public AdminPeriodSalesStatisticsDTO findPeriodStatistics(LocalDate startDate, LocalDate endDate) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminPeriodSalesStatisticsDTO.class,
                                periodSalesSummary.sales.longValue().sum().as("monthSales"),
                                periodSalesSummary.salesQuantity.longValue().sum().as("monthSalesQuantity"),
                                periodSalesSummary.orderQuantity.longValue().sum().as("monthOrderQuantity"),
                                periodSalesSummary.totalDeliveryFee.longValue().sum().as("deliveryFee"),
                                periodSalesSummary.cashTotal.longValue().sum().as("cashTotalPrice"),
                                periodSalesSummary.cardTotal.longValue().sum().as("cardTotalPrice")
                        )
                )
                .from(periodSalesSummary)
                .where(periodSalesSummary.period.goe(startDate).and(periodSalesSummary.period.lt(endDate)))
                .fetchOne();
    }

    @Override
    public List<AdminPeriodSalesListDTO> findPeriodDailyList(LocalDate startDate, LocalDate endDate) {

        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminPeriodSalesListDTO.class,
                                periodSalesSummary.period.dayOfMonth().as("date"),
                                periodSalesSummary.sales.as("sales"),
                                periodSalesSummary.salesQuantity.as("salesQuantity"),
                                periodSalesSummary.orderQuantity.as("orderQuantity")
                        )
                )
                .from(periodSalesSummary)
                .where(periodSalesSummary.period.goe(startDate).and(periodSalesSummary.period.lt(endDate)))
                .orderBy(periodSalesSummary.period.asc())
                .fetch();
    }

    @Override
    public AdminClassificationSalesDTO findDailySales(LocalDate period) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminClassificationSalesDTO.class,
                                periodSalesSummary.sales,
                                periodSalesSummary.salesQuantity,
                                periodSalesSummary.orderQuantity
                        )
                )
                .from(periodSalesSummary)
                .where(periodSalesSummary.period.eq(period))
                .fetchOne();
    }

    @Override
    public PeriodSalesSummary findByPeriod(LocalDate period) {
        return jpaQueryFactory.selectFrom(periodSalesSummary)
                .where(periodSalesSummary.period.eq(period))
                .fetchOne();
    }
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductSales;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductSales.productSales;

@Repository
@RequiredArgsConstructor
public class ProductSalesDSLRepositoryImpl implements ProductSalesDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductSales> findAllByOptionIds(List<Long> orderOptionIdList) {

        return jpaQueryFactory.select(productSales)
                .from(productSales)
                .where(productSales.optionId.in(orderOptionIdList))
                .fetch();
    }
}

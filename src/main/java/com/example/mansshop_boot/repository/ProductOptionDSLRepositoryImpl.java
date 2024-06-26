package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.product.ProductOptionDTO;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;

@Repository
@RequiredArgsConstructor
public class ProductOptionDSLRepositoryImpl implements ProductOptionDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductOptionDTO> findByDetailOption(String productId) {

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ProductOptionDTO.class
                                , productOption.id.as("optionId")
                                , productOption.size
                                , productOption.color
                                , productOption.stock
                        )
                )
                .from(productOption)
                .where(productOption.product.id.eq(productId).and(productOption.isOpen.isTrue()))
                .fetch();
    }

    @Override
    public List<AdminProductOptionDTO> findAllByProductId(String productId) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminProductOptionDTO.class
                                , productOption.id.as("optionId")
                                , productOption.size
                                , productOption.color
                                , productOption.stock.as("optionStock")
                                , productOption.isOpen.as("optionIsOpen")
                        )
                )
                .from(productOption)
                .where(productOption.product.id.eq(productId))
                .fetch();
    }

    @Override
    public List<ProductOption> findAllOptionByProductIdList(List<String> productIdList) {
        return jpaQueryFactory.select(productOption)
                .from(productOption)
                .where(productOption.product.id.in(productIdList))
                .fetch();
    }

    @Override
    public List<ProductOption> findAllOptionByProductId(String productId) {
        return jpaQueryFactory.select(productOption)
                .from(productOption)
                .where(productOption.product.id.eq(productId))
                .orderBy(productOption.id.asc())
                .fetch();
    }
}

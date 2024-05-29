package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.product.ProductOptionDTO;
import com.querydsl.core.types.Projections;
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
                .where(productOption.product.id.eq(productId).and(productOption.optionClosed.eq(0)))
                .fetch();
    }
}

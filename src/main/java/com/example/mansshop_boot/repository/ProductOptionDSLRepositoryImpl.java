package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.business.AdminOptionStockDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.order.business.OrderProductInfoDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductOptionDTO;
import com.example.mansshop_boot.domain.entity.ProductOption;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;
import static com.example.mansshop_boot.domain.entity.QProduct.product;

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
    public List<AdminOptionStockDTO> findAllOptionByProductIdList(List<String> productIdList) {
        /*return jpaQueryFactory.selectFrom(productOption)
//                .from(productOption)
                .join(productOption.product).fetchJoin()
                .where(productOption.product.id.in(productIdList))
                .fetch();*/


        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminOptionStockDTO.class,
                        product.id.as("productId"),
                        productOption.size,
                        productOption.color,
                        productOption.stock.as("optionStock"),
                        productOption.isOpen.as("optionIsOpen")
                )
        )
                .from(productOption)
                .innerJoin(product)
                .on(productOption.product.id.eq(product.id))
                .where(product.id.in(productIdList))
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

    @Override
    public List<OrderProductInfoDTO> findOrderData(List<Long> optionIds) {

        return jpaQueryFactory.select(
                        Projections.constructor(
                                OrderProductInfoDTO.class
                                , product.id.as("productId")
                                , productOption.id.as("optionId")
                                , product.productName
                                , productOption.size
                                , productOption.color
                                , product.productPrice.as("price")
                                , product.productDiscount.as("discount")
                        )
                )
                .from(productOption)
                .innerJoin(product)
                .on(productOption.product.id.eq(product.id))
                .where(productOption.id.in(optionIds))
                .fetch();
    }
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.cart.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QCartDetail.cartDetail;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;
import static com.example.mansshop_boot.domain.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class CartDetailDSLRepositoryImpl implements CartDetailDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CartDetailDTO> findAllByCartId(long cartId) {

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                CartDetailDTO.class
                                , cartDetail.id.as("cartDetailId")
                                , product.id.as("productId")
                                , productOption.id.as("optionId")
                                , product.productName
                                , product.thumbnail.as("productThumbnail")
                                , productOption.size
                                , productOption.color
                                , cartDetail.cartCount.as("count")
                                , cartDetail.cartPrice.as("price")
                        )
                )
                .from(cartDetail)
                .innerJoin(productOption)
                .on(cartDetail.productOption.id.eq(productOption.id))
                .innerJoin(product)
                .on(productOption.product.id.eq(product.id))
                .where(cartDetail.cart.id.eq(cartId))
                .fetch();

    }

    @Override
    public Long countByCartId(long cartId) {
        return jpaQueryFactory.select(cartDetail.count())
                .from(cartDetail)
                .where(cartDetail.cart.id.eq(cartId))
                .fetchOne();
    }
}

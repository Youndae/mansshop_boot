package com.example.mansshop_boot.repository.productLike;

import com.example.mansshop_boot.domain.dto.mypage.out.ProductLikeDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.entity.ProductLike;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductLike.productLike;
import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;

@Repository
@RequiredArgsConstructor
public class ProductLikeDSLRepositoryImpl implements ProductLikeDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int countByUserIdAndProductId(String userId, String productId) {
        List<Long> count =  jpaQueryFactory
                                .select(productLike.count())
                                .from(productLike)
                                .where(
                                        productLike.member.userId.eq(userId)
                                                .and(
                                                        productLike.product.id.eq(productId)
                                                )
                                )
                                .fetch();

        return count.get(0).intValue();
    }

    @Override
    @Transactional
    public Long deleteByUserIdAndProductId(ProductLike productLikeEntity) {
        return jpaQueryFactory.delete(productLike)
                .where(
                        productLike.member.eq(productLikeEntity.getMember())
                        .and(
                                productLike.product.eq(productLikeEntity.getProduct())
                        )
                )
                .execute();
    }

    @Override
    public Page<ProductLikeDTO> findByUserId(LikePageDTO pageDTO, String userId, Pageable pageable) {

        List<ProductLikeDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        ProductLikeDTO.class
                        , productLike.id.as("likeId")
                        , product.id.as("productId")
                        , product.productName
                        , product.productPrice
                        , product.thumbnail
                        , productOption.stock.sum().as("stock")
                        , productLike.createdAt
                )
        )
                .from(productLike)
                .innerJoin(product)
                .on(productLike.product.id.eq(product.id))
                .innerJoin(productOption)
                .on(productOption.product.id.eq(product.id))
                .groupBy(productLike.id)
                .where(productLike.member.userId.eq(userId))
                .orderBy(productLike.id.desc())
                .offset(pageable.getOffset())
                .limit(pageDTO.likeAmount())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(productLike.countDistinct())
                .from(productLike)
                .where(productLike.member.userId.eq(userId));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }
}

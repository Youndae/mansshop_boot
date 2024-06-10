package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductLike.productLike;

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
}

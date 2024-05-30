package com.example.mansshop_boot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}

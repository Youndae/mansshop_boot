package com.example.mansshop_boot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductThumbnail.productThumbnail;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductThumbnailDSLRepositoryImpl implements ProductThumbnailDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> findByProductId(String productId) {
        return jpaQueryFactory
                .select(productThumbnail.imageName)
                .from(productThumbnail)
                .where(productThumbnail.product.id.eq(productId))
                .orderBy(productThumbnail.productThumbnailStep.asc())
                .fetch();
    }
}

package com.example.mansshop_boot.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductInfoImage.productInfoImage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductInfoImageDSLRepositoryImpl implements ProductInfoImageDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> findByProductId(String productId) {

        return jpaQueryFactory
                .select(productInfoImage.imageName)
                .from(productInfoImage)
                .where(productInfoImage.product.id.eq(productId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByImageName(List<String> deleteList) {
        jpaQueryFactory.delete(productInfoImage)
                .where(productInfoImage.imageName.in(deleteList))
                .execute();
    }
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDetailDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductOrderDetail.productOrderDetail;
import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;
import static com.example.mansshop_boot.domain.entity.QProductOrder.productOrder;


@Repository
@RequiredArgsConstructor
public class ProductOrderDetailDSLRepositoryImpl implements ProductOrderDetailDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MyPageOrderDetailDTO> findByDetailList(List<Long> orderIdList) {

        return jpaQueryFactory.select(
                Projections.constructor(
                        MyPageOrderDetailDTO.class
                        , productOrder.id.as("orderId")
                        , product.id.as("productId")
                        , productOrderDetail.id.as("detailId")
                        , product.productName
                        , productOption.size
                        , productOption.color
                        , productOrderDetail.orderDetailCount.as("detailCount")
                        , productOrderDetail.orderDetailPrice.as("detailPrice")
                        , productOrderDetail.orderReviewStatus.as("reviewStatus")
                        , product.thumbnail
                )
        )
                .from(productOrderDetail)
                .innerJoin(productOrder)
                .on(productOrderDetail.productOrder.id.eq(productOrder.id))
                .innerJoin(productOption)
                .on(productOrderDetail.productOption.id.eq(productOption.id))
                .innerJoin(product)
                .on(productOption.product.id.eq(product.id))
                .where(productOrderDetail.productOrder.id.in(orderIdList))
                .fetch();
    }
}

package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductOrderDetail is a Querydsl query type for ProductOrderDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductOrderDetail extends EntityPathBase<ProductOrderDetail> {

    private static final long serialVersionUID = 1740358734L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductOrderDetail productOrderDetail = new QProductOrderDetail("productOrderDetail");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> orderDetailCount = createNumber("orderDetailCount", Integer.class);

    public final NumberPath<Integer> orderDetailPrice = createNumber("orderDetailPrice", Integer.class);

    public final QProduct product;

    public final QProductOption productOption;

    public final QProductOrder productOrder;

    public QProductOrderDetail(String variable) {
        this(ProductOrderDetail.class, forVariable(variable), INITS);
    }

    public QProductOrderDetail(Path<? extends ProductOrderDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductOrderDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductOrderDetail(PathMetadata metadata, PathInits inits) {
        this(ProductOrderDetail.class, metadata, inits);
    }

    public QProductOrderDetail(Class<? extends ProductOrderDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
        this.productOption = inits.isInitialized("productOption") ? new QProductOption(forProperty("productOption"), inits.get("productOption")) : null;
        this.productOrder = inits.isInitialized("productOrder") ? new QProductOrder(forProperty("productOrder"), inits.get("productOrder")) : null;
    }

}


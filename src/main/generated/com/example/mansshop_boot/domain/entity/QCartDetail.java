package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCartDetail is a Querydsl query type for CartDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCartDetail extends EntityPathBase<CartDetail> {

    private static final long serialVersionUID = -1564495601L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCartDetail cartDetail = new QCartDetail("cartDetail");

    public final QCart cart;

    public final NumberPath<Integer> cartCount = createNumber("cartCount", Integer.class);

    public final NumberPath<Integer> cartPrice = createNumber("cartPrice", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProductOption productOption;

    public QCartDetail(String variable) {
        this(CartDetail.class, forVariable(variable), INITS);
    }

    public QCartDetail(Path<? extends CartDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCartDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCartDetail(PathMetadata metadata, PathInits inits) {
        this(CartDetail.class, metadata, inits);
    }

    public QCartDetail(Class<? extends CartDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cart = inits.isInitialized("cart") ? new QCart(forProperty("cart"), inits.get("cart")) : null;
        this.productOption = inits.isInitialized("productOption") ? new QProductOption(forProperty("productOption"), inits.get("productOption")) : null;
    }

}


package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductLike is a Querydsl query type for ProductLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductLike extends EntityPathBase<ProductLike> {

    private static final long serialVersionUID = 1601308520L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductLike productLike = new QProductLike("productLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QProduct product;

    public QProductLike(String variable) {
        this(ProductLike.class, forVariable(variable), INITS);
    }

    public QProductLike(Path<? extends ProductLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductLike(PathMetadata metadata, PathInits inits) {
        this(ProductLike.class, metadata, inits);
    }

    public QProductLike(Class<? extends ProductLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}


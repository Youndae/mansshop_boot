package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductThumbnail is a Querydsl query type for ProductThumbnail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductThumbnail extends EntityPathBase<ProductThumbnail> {

    private static final long serialVersionUID = 2041721787L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductThumbnail productThumbnail = new QProductThumbnail("productThumbnail");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageName = createString("imageName");

    public final QProduct product;

    public final NumberPath<Integer> productThumbnailStep = createNumber("productThumbnailStep", Integer.class);

    public QProductThumbnail(String variable) {
        this(ProductThumbnail.class, forVariable(variable), INITS);
    }

    public QProductThumbnail(Path<? extends ProductThumbnail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductThumbnail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductThumbnail(PathMetadata metadata, PathInits inits) {
        this(ProductThumbnail.class, metadata, inits);
    }

    public QProductThumbnail(Class<? extends ProductThumbnail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}


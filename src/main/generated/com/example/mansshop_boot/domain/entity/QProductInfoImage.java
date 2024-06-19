package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductInfoImage is a Querydsl query type for ProductInfoImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductInfoImage extends EntityPathBase<ProductInfoImage> {

    private static final long serialVersionUID = 1922298908L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductInfoImage productInfoImage = new QProductInfoImage("productInfoImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageName = createString("imageName");

    public final QProduct product;

    public QProductInfoImage(String variable) {
        this(ProductInfoImage.class, forVariable(variable), INITS);
    }

    public QProductInfoImage(Path<? extends ProductInfoImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductInfoImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductInfoImage(PathMetadata metadata, PathInits inits) {
        this(ProductInfoImage.class, metadata, inits);
    }

    public QProductInfoImage(Class<? extends ProductInfoImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}


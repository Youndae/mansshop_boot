package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductQnA is a Querydsl query type for ProductQnA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductQnA extends EntityPathBase<ProductQnA> {

    private static final long serialVersionUID = -1333813293L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductQnA productQnA = new QProductQnA("productQnA");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QProduct product;

    public final NumberPath<Long> productQnAGroupId = createNumber("productQnAGroupId", Long.class);

    public final NumberPath<Integer> productQnAStat = createNumber("productQnAStat", Integer.class);

    public final NumberPath<Integer> productQnAStep = createNumber("productQnAStep", Integer.class);

    public final StringPath qnaContent = createString("qnaContent");

    public QProductQnA(String variable) {
        this(ProductQnA.class, forVariable(variable), INITS);
    }

    public QProductQnA(Path<? extends ProductQnA> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductQnA(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductQnA(PathMetadata metadata, PathInits inits) {
        this(ProductQnA.class, metadata, inits);
    }

    public QProductQnA(Class<? extends ProductQnA> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}


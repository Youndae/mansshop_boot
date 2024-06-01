package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductReviewReply is a Querydsl query type for ProductReviewReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductReviewReply extends EntityPathBase<ProductReviewReply> {

    private static final long serialVersionUID = 1900506497L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductReviewReply productReviewReply = new QProductReviewReply("productReviewReply");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QProductReview productReview;

    public final StringPath replyContent = createString("replyContent");

    public final DateTimePath<java.util.Date> updatedAt = createDateTime("updatedAt", java.util.Date.class);

    public QProductReviewReply(String variable) {
        this(ProductReviewReply.class, forVariable(variable), INITS);
    }

    public QProductReviewReply(Path<? extends ProductReviewReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductReviewReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductReviewReply(PathMetadata metadata, PathInits inits) {
        this(ProductReviewReply.class, metadata, inits);
    }

    public QProductReviewReply(Class<? extends ProductReviewReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.productReview = inits.isInitialized("productReview") ? new QProductReview(forProperty("productReview"), inits.get("productReview")) : null;
    }

}


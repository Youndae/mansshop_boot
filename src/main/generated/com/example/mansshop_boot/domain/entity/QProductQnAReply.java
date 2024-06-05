package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductQnAReply is a Querydsl query type for ProductQnAReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductQnAReply extends EntityPathBase<ProductQnAReply> {

    private static final long serialVersionUID = -2043842857L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductQnAReply productQnAReply = new QProductQnAReply("productQnAReply");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QProductQnA productQnA;

    public final StringPath replyContent = createString("replyContent");

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QProductQnAReply(String variable) {
        this(ProductQnAReply.class, forVariable(variable), INITS);
    }

    public QProductQnAReply(Path<? extends ProductQnAReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductQnAReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductQnAReply(PathMetadata metadata, PathInits inits) {
        this(ProductQnAReply.class, metadata, inits);
    }

    public QProductQnAReply(Class<? extends ProductQnAReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.productQnA = inits.isInitialized("productQnA") ? new QProductQnA(forProperty("productQnA"), inits.get("productQnA")) : null;
    }

}


package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberQnA is a Querydsl query type for MemberQnA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberQnA extends EntityPathBase<MemberQnA> {

    private static final long serialVersionUID = 1658274700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberQnA memberQnA = new QMemberQnA("memberQnA");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath memberQnAContent = createString("memberQnAContent");

    public final NumberPath<Integer> memberQnAStat = createNumber("memberQnAStat", Integer.class);

    public final QQnAClassification qnAClassification;

    public QMemberQnA(String variable) {
        this(MemberQnA.class, forVariable(variable), INITS);
    }

    public QMemberQnA(Path<? extends MemberQnA> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberQnA(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberQnA(PathMetadata metadata, PathInits inits) {
        this(MemberQnA.class, metadata, inits);
    }

    public QMemberQnA(Class<? extends MemberQnA> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.qnAClassification = inits.isInitialized("qnAClassification") ? new QQnAClassification(forProperty("qnAClassification")) : null;
    }

}


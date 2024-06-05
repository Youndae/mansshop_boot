package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberQnAReply is a Querydsl query type for MemberQnAReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberQnAReply extends EntityPathBase<MemberQnAReply> {

    private static final long serialVersionUID = 332674750L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberQnAReply memberQnAReply = new QMemberQnAReply("memberQnAReply");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QMemberQnA memberQnA;

    public final StringPath replyContent = createString("replyContent");

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QMemberQnAReply(String variable) {
        this(MemberQnAReply.class, forVariable(variable), INITS);
    }

    public QMemberQnAReply(Path<? extends MemberQnAReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberQnAReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberQnAReply(PathMetadata metadata, PathInits inits) {
        this(MemberQnAReply.class, metadata, inits);
    }

    public QMemberQnAReply(Class<? extends MemberQnAReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.memberQnA = inits.isInitialized("memberQnA") ? new QMemberQnA(forProperty("memberQnA"), inits.get("memberQnA")) : null;
    }

}


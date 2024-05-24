package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQnAClassification is a Querydsl query type for QnAClassification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnAClassification extends EntityPathBase<QnAClassification> {

    private static final long serialVersionUID = 2104769420L;

    public static final QQnAClassification qnAClassification = new QQnAClassification("qnAClassification");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath qnaClassificationName = createString("qnaClassificationName");

    public QQnAClassification(String variable) {
        super(QnAClassification.class, forVariable(variable));
    }

    public QQnAClassification(Path<? extends QnAClassification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQnAClassification(PathMetadata metadata) {
        super(QnAClassification.class, metadata);
    }

}


package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClassification is a Querydsl query type for Classification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassification extends EntityPathBase<Classification> {

    private static final long serialVersionUID = 722488932L;

    public static final QClassification classification = new QClassification("classification");

    public final NumberPath<Integer> classificationStep = createNumber("classificationStep", Integer.class);

    public final StringPath id = createString("id");

    public QClassification(String variable) {
        super(Classification.class, forVariable(variable));
    }

    public QClassification(Path<? extends Classification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClassification(PathMetadata metadata) {
        super(Classification.class, metadata);
    }

}


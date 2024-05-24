package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QproductSales is a Querydsl query type for productSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QproductSales extends EntityPathBase<productSales> {

    private static final long serialVersionUID = -2057136325L;

    public static final QproductSales productSales = new QproductSales("productSales");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath optionName = createString("optionName");

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> sales = createNumber("sales", Long.class);

    public final NumberPath<Long> salesRate = createNumber("salesRate", Long.class);

    public final DateTimePath<java.util.Date> updatedAt = createDateTime("updatedAt", java.util.Date.class);

    public QproductSales(String variable) {
        super(productSales.class, forVariable(variable));
    }

    public QproductSales(Path<? extends productSales> path) {
        super(path.getType(), path.getMetadata());
    }

    public QproductSales(PathMetadata metadata) {
        super(productSales.class, metadata);
    }

}


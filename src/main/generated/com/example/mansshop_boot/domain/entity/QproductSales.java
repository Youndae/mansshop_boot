package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductSales is a Querydsl query type for ProductSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSales extends EntityPathBase<ProductSales> {

    private static final long serialVersionUID = -1892816037L;

    public static final QProductSales productSales = new QProductSales("productSales");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> optionId = createNumber("optionId", Long.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> sales = createNumber("sales", Long.class);

    public final NumberPath<Long> salesRate = createNumber("salesRate", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QProductSales(String variable) {
        super(ProductSales.class, forVariable(variable));
    }

    public QProductSales(Path<? extends ProductSales> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductSales(PathMetadata metadata) {
        super(ProductSales.class, metadata);
    }

}


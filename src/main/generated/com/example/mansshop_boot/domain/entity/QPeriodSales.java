package com.example.mansshop_boot.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPeriodSales is a Querydsl query type for PeriodSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPeriodSales extends EntityPathBase<PeriodSales> {

    private static final long serialVersionUID = 2121431565L;

    public static final QPeriodSales periodSales = new QPeriodSales("periodSales");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.util.Date> period = createDateTime("period", java.util.Date.class);

    public final NumberPath<Long> sales = createNumber("sales", Long.class);

    public final NumberPath<Long> salesRate = createNumber("salesRate", Long.class);

    public QPeriodSales(String variable) {
        super(PeriodSales.class, forVariable(variable));
    }

    public QPeriodSales(Path<? extends PeriodSales> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPeriodSales(PathMetadata metadata) {
        super(PeriodSales.class, metadata);
    }

}


package com.example.mansshop_boot.repository.productSales;

import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.AdminProductSalesListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.ProductSalesSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductSalesSummary.productSalesSummary;
import static com.example.mansshop_boot.domain.entity.QClassification.classification;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;

@Repository
@RequiredArgsConstructor
public class ProductSalesSummaryDSLRepositoryImpl implements ProductSalesSummaryDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AdminBestSalesProductDTO> findPeriodBestProductOrder(LocalDate startDate, LocalDate endDate) {
        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "productPeriodSalesQuantity");

        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminBestSalesProductDTO.class,
                                product.productName.as("productName"),
                                productSalesSummary.salesQuantity.longValue().sum().as(aliasQuantity),
                                productSalesSummary.sales.longValue().sum().as("productPeriodSales")
                        )
                )
                .from(productSalesSummary)
                .innerJoin(productSalesSummary.product, product)
                .where(productSalesSummary.periodMonth.goe(startDate).and(productSalesSummary.periodMonth.lt(endDate)))
                .groupBy(product.productName)
                .orderBy(aliasQuantity.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<AdminPeriodClassificationDTO> findPeriodClassification(LocalDate startDate, LocalDate endDate) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        AdminPeriodClassificationDTO.class,
                        classification.id.as("classification"),
                        productSalesSummary.sales.longValue().sum().coalesce(0L).as("classificationSales"),
                        productSalesSummary.salesQuantity.longValue().sum().coalesce(0L).as("classificationSalesQuantity")
                )
        )
                .from(productSalesSummary)
                .rightJoin(classification)
                .on(classification.id.eq(productSalesSummary.classification.id))
                .where(productSalesSummary.periodMonth.goe(startDate)
                        .and(productSalesSummary.periodMonth.lt(endDate))
                )
                .groupBy(classification.id)
                .orderBy(classification.classificationStep.asc())
                .fetch();
    }

    @Override
    public AdminClassificationSalesDTO findPeriodClassificationSales(LocalDate startDate, LocalDate endDate, String classification) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminClassificationSalesDTO.class,
                                productSalesSummary.sales.longValue().sum().as("sales"),
                                productSalesSummary.salesQuantity.longValue().sum().as("salesQuantity"),
                                productSalesSummary.orderQuantity.longValue().sum().as("orderQuantity")
                        )
                )
                .from(productSalesSummary)
                .where(productSalesSummary.classification.id.eq(classification)
                        .and(productSalesSummary.periodMonth.goe(startDate))
                        .and(productSalesSummary.periodMonth.lt(endDate))
                )
                .fetchOne();
    }

    @Override
    public List<AdminClassificationSalesProductListDTO> findPeriodClassificationProductSales(LocalDate startDate, LocalDate endDate, String classification) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminClassificationSalesProductListDTO.class,
                                product.productName.as("productName"),
                                productOption.size.as("size"),
                                productOption.color.as("color"),
                                productSalesSummary.sales.coalesce(0L).as("productSales"),
                                productSalesSummary.salesQuantity.coalesce(0L).as("productSalesQuantity")
                        )
                )
                .from(productOption)
                .innerJoin(productOption.product, product)
                .leftJoin(productSalesSummary)
                /*.on(productSalesSummary.product.id.eq(product.id)
                        .and(productSalesSummary.productOption.id.eq(productOption.id))
                        .and(productSalesSummary.classification.id.eq(classification))
                        .and(productSalesSummary.periodMonth.goe(startDate))
                        .and(productSalesSummary.periodMonth.lt(endDate))
                )*/
                .on(productSalesSummary.productOption.id.eq(productOption.id)
                        .and(productSalesSummary.periodMonth.goe(startDate))
                        .and(productSalesSummary.periodMonth.lt(endDate))
                )
                .where(product.classification.id.eq(classification))
                .orderBy(product.createdAt.desc())
                .fetch();
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<AdminProductSalesListDTO> findProductSalesList(AdminPageDTO pageDTO, Pageable pageable) {
        StringBuilder queryBuilder = new StringBuilder();
        String searchCondition = pageDTO.keyword() != null ? "WHERE pr.productName LIKE :keyword " : "";

        queryBuilder.append("SELECT p.classificationId, ")
                .append("p.id, ")
                .append("p.productName, ")
                .append("coalesce(sum(s.sales), 0), ")
                .append("p.productSalesQuantity ")
                .append("FROM productSalesSummary s ")
                .append("RIGHT JOIN")
                .append("(")
                    .append("SELECT pr.id, ")
                    .append("pr.classificationId, ")
                    .append("pr.productName, ")
                    .append("pr.productSalesQuantity ")
                    .append("FROM product pr ")
                    .append("INNER JOIN classification c ")
                    .append("ON pr.classificationId = c.id ")
                    .append(searchCondition)
                    .append("ORDER BY c.classificationStep ASC, ")
                    .append("pr.createdAt DESC ")
                    .append("LIMIT :offset, :amount")
                .append(") as p ")
                .append("ON p.id = s.productId ")
                .append("GROUP BY p.classificationId, p.id, p.productName");

        Query query = em.createNativeQuery(queryBuilder.toString());

        query.setParameter("offset", pageDTO.offset());
        query.setParameter("amount", pageDTO.amount());

        if(pageDTO.keyword() != null)
            query.setParameter("keyword", "%" + pageDTO.keyword() + "%");

        List<Object[]> resultList = query.getResultList();

        List<AdminProductSalesListDTO> list = resultList.stream()
                                                        .map(val -> new AdminProductSalesListDTO(
                                                                (String) val[0],
                                                                (String) val[1],
                                                                (String) val[2],
                                                                ((Number) val[3]).longValue(),
                                                                ((Number) val[4]).longValue()
                                                        ))
                                                        .toList();

        JPAQuery<Long> count = jpaQueryFactory.select(product.createdAt.count())
                                            .from(product)
                                            .where(productSalesDynamicSearch(pageDTO));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression productSalesDynamicSearch(AdminPageDTO pageDTO) {
        if(pageDTO.keyword() != null)
            return product.productName.like(pageDTO.keyword());
        else
            return null;
    }

    @Override
    public AdminProductSalesDTO getProductSales(String productId) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminProductSalesDTO.class,
                                product.productName.as("productName"),
                                productSalesSummary.sales.longValue().sum().as("totalSales"),
                                product.productSalesQuantity.as("totalSalesQuantity")
                        )
                )
                .from(productSalesSummary)
                .innerJoin(productSalesSummary.product, product)
                .where(product.id.eq(productId))
                .fetchOne();
    }

    @Override
    public AdminSalesDTO getProductPeriodSales(int year, String productId) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminSalesDTO.class,
                                productSalesSummary.sales.longValue().sum().as("sales"),
                                product.productSalesQuantity.as("salesQuantity")
                        )
                )
                .from(productSalesSummary)
                .innerJoin(productSalesSummary.product, product)
                .where(
                        productSalesSummary.product.id.eq(productId)
                                .and(productSalesSummary.periodMonth.year().eq(year))
                )
                .fetchOne();
    }

    @Override
    public List<AdminPeriodSalesListDTO> getProductMonthPeriodSales(int year, String productId) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminPeriodSalesListDTO.class,
                                productSalesSummary.periodMonth.month().as("date"),
                                productSalesSummary.sales.longValue().sum().as("sales"),
                                product.productSalesQuantity.as("salesQuantity"),
                                productSalesSummary.orderQuantity.longValue().sum().as("orderQuantity")
                        )
                )
                .from(productSalesSummary)
                .innerJoin(productSalesSummary.product, product)
                .where(productSalesSummary.product.id.eq(productId).and(productSalesSummary.periodMonth.year().eq(year)))
                .groupBy(productSalesSummary.periodMonth.month())
                .orderBy(productSalesSummary.periodMonth.month().asc())
                .fetch();
    }

    @Override
    public List<AdminProductSalesOptionDTO> getProductOptionSales(int year, String productId) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminProductSalesOptionDTO.class,
                                productOption.id.as("optionId"),
                                productOption.size.as("size"),
                                productOption.color.as("color"),
                                productSalesSummary.sales.longValue().sum().coalesce(0L).as("optionSales"),
                                productSalesSummary.salesQuantity.longValue().sum().coalesce(0L).as("optionSalesQuantity")
                        )
                )
                .from(productOption)
                .leftJoin(productSalesSummary)
                .on(
                        productOption.product.id.eq(productSalesSummary.product.id)
                                .and(productOption.id.eq(productSalesSummary.productOption.id))
                                .and(productOptionSalesDynamicSearch(year))
                )
                .where(productOption.product.id.eq(productId))
                .groupBy(productOption.id, productOption.size, productOption.color)
                .orderBy(productOption.id.asc())
                .fetch();
    }

    private BooleanExpression productOptionSalesDynamicSearch(int year) {
        return year == 0 ? null : productSalesSummary.periodMonth.year().eq(year);
    }

    @Override
    public List<ProductSalesSummary> findAllByProductOptionIds(LocalDate periodMonth, List<Long> productOptionIds) {
        // 테스트 필요.
        // 전체 fetchJoin()이 빠를지, 몇개만 하는게 빠를지 아예 안하고 프록시 객체로 처리하는게 빠를지 테스트 필요.
        return jpaQueryFactory.select(productSalesSummary)
                .from(productSalesSummary)
                .innerJoin(productSalesSummary.productOption, productOption).fetchJoin()
                .innerJoin(productSalesSummary.classification, classification).fetchJoin()
                .innerJoin(productSalesSummary.product, product).fetchJoin()
                .where(
                        productSalesSummary.periodMonth.eq(periodMonth)
                                .and(productSalesSummary.productOption.id.in(productOptionIds))
                )
                .fetch();
    }
}

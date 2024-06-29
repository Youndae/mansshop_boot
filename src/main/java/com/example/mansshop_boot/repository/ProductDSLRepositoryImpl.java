package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.AdminProductListDTO;
import com.example.mansshop_boot.domain.dto.admin.AdminProductStockDataDTO;
import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.entity.Product;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProduct.product;
import static com.example.mansshop_boot.domain.entity.QProductOption.productOption;

@Repository
@RequiredArgsConstructor
public class ProductDSLRepositoryImpl implements ProductDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     *
     * @param pageDTO
     * @return
     *
     * 메인 페이지 중 BEST, NEW에 대한 조회
     * 상품 12개 정보만 필요하기 때문에 페이징 사용하지 않을 것이므로 Page 타입이 아닌 List 타입으로.
     */
    @Override
    public List<MainListDTO> findListDefault(MemberPageDTO pageDTO) {

        List<MainListDTO> list = jpaQueryFactory.select(
                        Projections.constructor(
                                MainListDTO.class
                                , product.id.as("productId")
                                , product.productName
                                , product.thumbnail
                                , product.productPrice.as("price")
                                , product.productDiscount.as("discount")
                                , productOption.stock.longValue().sum().as("stock")
                        )
                )
                .from(product)
                .innerJoin(productOption)
                .on(product.id.eq(productOption.product.id))
                .orderBy(defaultListOrderBy(pageDTO.classification()))
                .groupBy(product.id)
                .limit(pageDTO.mainProductAmount())
                .fetch();


        return list;
    }


    @Override
    public Page<MainListDTO> findListPageable(MemberPageDTO pageDTO, Pageable pageable) {

        List<MainListDTO> list = jpaQueryFactory.select(
                        Projections.constructor(
                                MainListDTO.class
                                , product.id.as("productId")
                                , product.productName
                                , product.thumbnail
                                , product.productPrice.as("price")
                                , product.productDiscount.as("discount")
                                , productOption.stock.longValue().sum().as("stock")
                        )
                )
                .from(product)
                .innerJoin(productOption)
                .on(product.id.eq(productOption.product.id))
                .where(
                        searchType(pageDTO.classification(), pageDTO.keyword())
                )
                .groupBy(product.id)
                .orderBy(defaultListOrderBy(pageDTO.classification()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(product.countDistinct())
                .from(product)
                .where(
                        searchType(pageDTO.classification(), pageDTO.keyword())
                );



        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }


    /**
     *
     * @param classification
     * @return
     *
     * 카테고리에 따른 정렬 동적 처리
     * BEST를 제외한 NEW나 다른 카테고리의 경우 모두 등록일자를 기준으로 정렬
     * BEST만 판매량으로 정렬
     */
    private OrderSpecifier<?> defaultListOrderBy(String classification){

        if(classification != null && classification.equals("BEST"))
            return new OrderSpecifier<>(Order.DESC, product.productSales);
        else
            return new OrderSpecifier<>(Order.DESC, product.createdAt);

    }

    /**
     *
     * @param classification
     * @param keyword
     * @return
     *
     * classification이 존재하면 keyword는 존재하지 않고
     * Keyword가 존재한다면 classification은 존재하지 않는다.
     */
    private BooleanExpression searchType(String classification, String keyword) {

        if(classification != null)
            return product.classification.id.eq(classification);
        else if(keyword != null) {
            System.out.println("keyword is not null : " + keyword);
            return product.productName.like(keyword);
        }else
            return null;
    }

    @Override
    public List<Product> findAllByIdList(List<String> productIdList) {
        return jpaQueryFactory.select(product)
                .from(product)
                .where(product.id.in(productIdList))
                .fetch();
    }

    @Override
    public Page<AdminProductListDTO> findAdminProductList(AdminPageDTO pageDTO, Pageable pageable) {

        List<AdminProductListDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminProductListDTO.class
                        , product.id.as("productId")
                        , product.classification.id.as("classification")
                        , product.productName
                        , productOption.stock.sum().as("stock")
                        , productOption.id.count().as("optionCount")
                        , product.productPrice.as("price")
                )
        )
                .from(product)
                .innerJoin(productOption)
                .on(product.id.eq(productOption.product.id))
                .where(adminProductSearch(pageDTO))
                .groupBy(product.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(product.countDistinct())
                                .from(product)
                                .where(adminProductSearch(pageDTO));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    private BooleanExpression adminProductSearch(AdminPageDTO pageDTO) {
        if(pageDTO.keyword() != null){
            return product.productName.like(pageDTO.keyword());
        }else {
            return null;
        }
    }

    @Override
    public Page<AdminProductStockDataDTO> findStockData(AdminPageDTO pageDTO, Pageable pageable) {

        NumberPath<Integer> aliasSum = Expressions.numberPath(Integer.class, "totalStock");

        List<AdminProductStockDataDTO> list = jpaQueryFactory.select(
                        Projections.constructor(
                                AdminProductStockDataDTO.class
                                , product.id.as("productId")
                                , product.classification.id.as("classification")
                                , product.productName
                                , ExpressionUtils.as(
                                        JPAExpressions.select(productOption.stock.sum())
                                                .from(productOption)
                                                .where(productOption.product.id.eq(product.id))
                                                .groupBy(product.id), aliasSum
                                )
                                , product.isOpen.as("isOpen")
                        )
                )
                .from(product)
                .where(adminProductSearch(pageDTO))
                .orderBy(aliasSum.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(product.countDistinct())
                                                .from(product)
                                                .where(adminProductSearch(pageDTO));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public Page<Product> getDiscountProduct(AdminPageDTO pageDTO, Pageable pageable) {

        List<Product> list = jpaQueryFactory.select(product)
                                            .from(product)
                                            .where(product.productDiscount.ne(0).and(adminProductSearch(pageDTO)))
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .orderBy(product.updatedAt.desc())
                                            .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(product.countDistinct())
                .from(product)
                .where(product.productDiscount.ne(0).and(adminProductSearch(pageDTO)));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public List<Product> getProductByClassification(String classification) {
        return jpaQueryFactory.select(product)
                .from(product)
                .where(product.classification.id.eq(classification))
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void patchProductDiscount(AdminDiscountPatchDTO patchDTO) {
        jpaQueryFactory.update(product)
                .set(product.productDiscount, patchDTO.discount())
                .set(product.updatedAt, LocalDate.now())
                .where(product.id.in(patchDTO.productIdList()))
                .execute();

    }
}

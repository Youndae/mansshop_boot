package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProduct.product;

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
                                , product.productPrice
                        )
                )
                .from(product)
                .orderBy(defaultListOrderBy(pageDTO.classification()))
                .limit(12)
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
                                , product.productPrice
                        )
                )
                .from(product)
                .where(
                        searchType(pageDTO.classification(), pageDTO.keyword())
                )
                .orderBy(defaultListOrderBy(pageDTO.classification()))
                .offset((pageDTO.pageNum() - 1) * pageDTO.mainProductAmount())
                .limit(pageDTO.mainProductAmount())
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
}
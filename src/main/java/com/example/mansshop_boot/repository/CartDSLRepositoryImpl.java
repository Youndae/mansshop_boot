package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import static com.example.mansshop_boot.domain.entity.QCart.cart;

@Repository
@RequiredArgsConstructor
public class CartDSLRepositoryImpl implements CartDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Cart findByUserIdAndCookieValue(CartMemberDTO cartMemberDTO) {

        Cart result = jpaQueryFactory.select(cart)
                .from(cart)
                .where(
                        userType(cartMemberDTO)
                )
                .fetchOne();

        return result;
    }

    @Override
    public Long findIdByUserId(CartMemberDTO cartMemberDTO) {
        return jpaQueryFactory.select(cart.id)
                .from(cart)
                .where(
                        userType(cartMemberDTO)
                )
                .fetchOne();
    }

    private BooleanExpression userType(CartMemberDTO cartMemberDTO) {
        if(cartMemberDTO.cartCookieValue() == null)
            return cart.member.userId.eq(cartMemberDTO.uid());
        else
            return cart.cookieId.eq(cartMemberDTO.cartCookieValue()).and(cart.member.userId.eq(cartMemberDTO.uid()));
    }

}

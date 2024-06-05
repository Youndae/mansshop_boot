package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Cart;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QCart.cart;

@Repository
@RequiredArgsConstructor
public class CartDSLRepositoryImpl implements CartDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Value("#{jwt['cookie.cart.uid']}")
    private String nonUserId;

    @Override
    public Cart findByUserIdAndCookieValue(String userId, String cookieValue) {

        Cart result = jpaQueryFactory.select(cart)
                .from(cart)
                .where(
                        userType(userId, cookieValue)
                )
                .fetchOne();

        return result;
    }

    @Override
    public Long findIdByUserId(String userId, String cookieValue) {
        return jpaQueryFactory.select(cart.id)
                .from(cart)
                .where(
                        userType(userId, cookieValue)
                )
                .fetchOne();
    }

    private BooleanExpression userType(String userId, String cookieValue) {
        if(cookieValue == null)
            return cart.member.userId.eq(userId);
        else
            return cart.cookieId.eq(cookieValue).and(cart.member.userId.eq(nonUserId));

    }

}

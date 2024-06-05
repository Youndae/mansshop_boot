package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.entity.Cart;

public interface CartDSLRepository {

    Long findIdByUserId(CartMemberDTO cartMemberDTO);


    Cart findByUserIdAndCookieValue(CartMemberDTO cartMemberDTO);

}

package com.example.mansshop_boot.repository.cart;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.entity.Cart;

public interface CartDSLRepository {

    Long findIdByUserId(CartMemberDTO cartMemberDTO);


    Cart findByUserIdAndCookieValue(CartMemberDTO cartMemberDTO);

}

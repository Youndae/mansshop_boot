package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Cart;

public interface CartDSLRepository {

//    Long findIdByUserId(String userId, String cookieValue);


    Cart findIdByUserId(String userId, String cookieValue);
}

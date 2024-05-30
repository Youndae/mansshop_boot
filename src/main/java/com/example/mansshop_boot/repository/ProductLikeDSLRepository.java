package com.example.mansshop_boot.repository;

public interface ProductLikeDSLRepository {

    int countByUserIdAndProductId(String userId, String productId);
}

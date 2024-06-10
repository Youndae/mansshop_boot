package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductLike;

public interface ProductLikeDSLRepository {

    int countByUserIdAndProductId(String userId, String productId);

    Long deleteByUserIdAndProductId(ProductLike productLike);
}

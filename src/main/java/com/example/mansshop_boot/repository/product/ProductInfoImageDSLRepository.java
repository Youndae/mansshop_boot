package com.example.mansshop_boot.repository.product;

import java.util.List;

public interface ProductInfoImageDSLRepository {
    List<String> findByProductId(String productId);

    void deleteByImageName(List<String> deleteList);
}

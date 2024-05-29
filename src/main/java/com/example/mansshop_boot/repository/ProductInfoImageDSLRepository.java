package com.example.mansshop_boot.repository;

import java.util.List;

public interface ProductInfoImageDSLRepository {
    List<String> findByProductId(String productId);
}

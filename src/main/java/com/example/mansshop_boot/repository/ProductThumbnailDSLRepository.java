package com.example.mansshop_boot.repository;

import java.util.List;

public interface ProductThumbnailDSLRepository {

    List<String> findByProductId(String productId);

    void deleteByImageName(List<String> deleteList);
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductInfoImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInfoImageRepository extends JpaRepository<ProductInfoImage, String>, ProductInfoImageDSLRepository {
}

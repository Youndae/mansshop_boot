package com.example.mansshop_boot.repository.product;

import com.example.mansshop_boot.domain.entity.ProductThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnail, Long>, ProductThumbnailDSLRepository {
}

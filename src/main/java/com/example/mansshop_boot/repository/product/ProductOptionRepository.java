package com.example.mansshop_boot.repository.product;

import com.example.mansshop_boot.domain.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long>, ProductOptionDSLRepository {
}

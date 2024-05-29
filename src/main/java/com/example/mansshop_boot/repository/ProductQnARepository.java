package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductQnA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQnARepository extends JpaRepository<ProductQnA, Long>, ProductQnADSLRepository {
}

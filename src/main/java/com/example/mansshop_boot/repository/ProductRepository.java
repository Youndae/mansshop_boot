package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String>, ProductDSLRepository {
}

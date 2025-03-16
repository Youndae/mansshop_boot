package com.example.mansshop_boot.repository.product;

import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.repository.product.ProductDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String>, ProductDSLRepository {

}

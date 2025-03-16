package com.example.mansshop_boot.repository.productOrder;

import com.example.mansshop_boot.domain.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>, ProductOrderDSLRepository {
}

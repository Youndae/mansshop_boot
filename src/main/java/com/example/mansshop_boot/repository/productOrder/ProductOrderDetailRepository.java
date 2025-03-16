package com.example.mansshop_boot.repository.productOrder;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderDetailRepository extends JpaRepository<ProductOrderDetail, Long>, ProductOrderDetailDSLRepository {
}

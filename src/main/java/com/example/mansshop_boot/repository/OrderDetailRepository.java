package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, OrderDetailDSLRepository {
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

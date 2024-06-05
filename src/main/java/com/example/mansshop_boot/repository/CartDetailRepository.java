package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, CartDetailDSLRepository {
}

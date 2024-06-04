package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartDSLRepository {
}

package com.example.mansshop_boot.repository.auth;

import com.example.mansshop_boot.domain.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}

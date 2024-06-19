package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassificationRepository extends JpaRepository<Classification, String> {
}

package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.MemberQnA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQnARepository extends JpaRepository<MemberQnA, Long>, MemberQnADSLRepository {
}

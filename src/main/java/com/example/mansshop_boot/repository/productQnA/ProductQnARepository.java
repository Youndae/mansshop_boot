package com.example.mansshop_boot.repository.productQnA;

import com.example.mansshop_boot.domain.entity.ProductQnA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductQnARepository extends JpaRepository<ProductQnA, Long>, ProductQnADSLRepository {
    List<ProductQnA> findAllByMember_UserIdOrderByIdDesc(String memberUserId);
}

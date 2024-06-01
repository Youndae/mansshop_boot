package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductQnAReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQnAReplyRepository extends JpaRepository<ProductQnAReply, Long>, ProductQnAReplyDSLRepository {
}

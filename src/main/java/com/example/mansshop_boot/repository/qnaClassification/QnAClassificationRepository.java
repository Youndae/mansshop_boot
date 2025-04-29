package com.example.mansshop_boot.repository.qnaClassification;

import com.example.mansshop_boot.domain.entity.QnAClassification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnAClassificationRepository extends JpaRepository<QnAClassification, Long>, QnAClassificationDSLRepository {
}

package com.example.mansshop_boot.repository.qnaClassification;

import com.example.mansshop_boot.domain.dto.mypage.qna.out.QnAClassificationDTO;

import java.util.List;

public interface QnAClassificationDSLRepository {
    List<QnAClassificationDTO> getAllQnAClassificationDTOs();
}

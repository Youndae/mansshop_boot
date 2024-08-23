package com.example.mansshop_boot.domain.dto.admin;


import com.example.mansshop_boot.domain.entity.QnAClassification;

public record AdminQnAClassificationDTO(
        long id
        , String name
) {

    public AdminQnAClassificationDTO(QnAClassification classification) {
        this(
                classification.getId()
                , classification.getQnaClassificationName()
        );
    }
}

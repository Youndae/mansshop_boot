package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.entity.QnAClassification;

import java.util.List;

public class QnAClassificationFixture {

    public static List<QnAClassification> createQnAClassificationList() {
        QnAClassification qnAClassification1 = new QnAClassification(1L, "상품 관련 문의");
        QnAClassification qnAClassification2 = new QnAClassification(2L, "배송 관련 문의");
        QnAClassification qnAClassification3 = new QnAClassification(3L, "교환, 환불 문의");

        return List.of(qnAClassification1, qnAClassification2, qnAClassification3);
    }
}

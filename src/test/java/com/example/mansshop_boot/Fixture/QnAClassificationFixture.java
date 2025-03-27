package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.entity.QnAClassification;

import java.util.List;

public class QnAClassificationFixture {

    public static List<QnAClassification> createQnAClassificationList() {
        List<String> names = List.of(
                "상품 관련 문의",
                "배송 관련 문의",
                "교환, 환불 문의"
        );

        return names.stream()
                    .map(v -> QnAClassification.builder()
                            .qnaClassificationName(v)
                            .build()
                    )
                    .toList();
    }
}

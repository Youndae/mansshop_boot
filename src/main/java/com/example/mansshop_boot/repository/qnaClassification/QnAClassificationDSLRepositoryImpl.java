package com.example.mansshop_boot.repository.qnaClassification;

import com.example.mansshop_boot.domain.dto.mypage.qna.out.QnAClassificationDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QQnAClassification.qnAClassification;

@Repository
@RequiredArgsConstructor
public class QnAClassificationDSLRepositoryImpl implements QnAClassificationDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<QnAClassificationDTO> getAllQnAClassificationDTOs() {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                QnAClassificationDTO.class,
                                qnAClassification.id,
                                qnAClassification.qnaClassificationName.as("name")
                        )
                )
                .from(qnAClassification)
                .fetch();
    }
}

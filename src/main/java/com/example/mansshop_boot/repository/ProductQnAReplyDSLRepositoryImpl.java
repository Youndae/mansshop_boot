package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductQnAReply;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QProductQnAReply.productQnAReply;

@Repository
@AllArgsConstructor
@Slf4j
public class ProductQnAReplyDSLRepositoryImpl implements ProductQnAReplyDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductQnAReply> getQnAReply(List<Long> qnaIdList) {



        return jpaQueryFactory.select(productQnAReply)
                .from(productQnAReply)
                .where(productQnAReply.productQnA.id.in(qnaIdList))
                .orderBy(productQnAReply.productQnA.id.desc())
                .orderBy(productQnAReply.productQnA.createdAt.asc())
                .fetch();
    }
}

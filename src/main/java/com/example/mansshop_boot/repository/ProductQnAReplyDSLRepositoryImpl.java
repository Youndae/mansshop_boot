package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.entity.ProductQnAReply;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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

    @Override
    public List<MyPageQnAReplyDTO> findAllByQnAId(long productQnAId) {

        return jpaQueryFactory.select(
                Projections.constructor(
                        MyPageQnAReplyDTO.class
                        , productQnAReply.id.as("replyId")
                        , new CaseBuilder()
                                .when(productQnAReply.member.nickname.isNull())
                                .then(productQnAReply.member.userName)
                                .otherwise(productQnAReply.member.nickname)
                                .as("writer")
                        , productQnAReply.replyContent
                        , productQnAReply.updatedAt
                )
        )
                .from(productQnAReply)
                .where(productQnAReply.productQnA.id.eq(productQnAId))
                .orderBy(productQnAReply.id.asc())
                .fetch();
    }
}

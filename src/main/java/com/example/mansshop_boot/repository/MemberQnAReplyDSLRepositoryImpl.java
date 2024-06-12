package com.example.mansshop_boot.repository;


import com.example.mansshop_boot.domain.dto.mypage.MyPageQnAReplyDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QMemberQnAReply.memberQnAReply;

@Repository
@RequiredArgsConstructor
public class MemberQnAReplyDSLRepositoryImpl implements MemberQnAReplyDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MyPageQnAReplyDTO> findAllByQnAId(long memberQnAId) {

        return jpaQueryFactory.select(
                Projections.constructor(
                        MyPageQnAReplyDTO.class
                        , memberQnAReply.id.as("replyId")
                        , new CaseBuilder()
                                .when(memberQnAReply.member.nickname.isNull())
                                .then(memberQnAReply.member.userName)
                                .otherwise(memberQnAReply.member.nickname)
                                .as("writer")
                        , memberQnAReply.replyContent
                        , memberQnAReply.updatedAt
                )
        )
                .from(memberQnAReply)
                .where(memberQnAReply.memberQnA.id.eq(memberQnAId))
                .fetch();
    }
}

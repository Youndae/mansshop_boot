package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QMemberQnA.memberQnA;
import static com.example.mansshop_boot.domain.entity.QQnAClassification.qnAClassification;

@Repository
@RequiredArgsConstructor
public class MemberQnADSLRepositoryImpl implements MemberQnADSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MemberQnAListDTO> findAllByUserId(String userId, Pageable pageable) {

        List<MemberQnAListDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        MemberQnAListDTO.class
                        , memberQnA.id.as("memberQnAId")
                        , memberQnA.memberQnATitle
                        , memberQnA.memberQnAStat
                        , qnAClassification.qnaClassificationName.as("qnaClassification")
                        , memberQnA.updatedAt
                )
        )
                .from(memberQnA)
                .innerJoin(qnAClassification)
                .on(memberQnA.qnAClassification.id.eq(qnAClassification.id))
                .where(memberQnA.member.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(memberQnA.id.desc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(memberQnA.countDistinct())
                .from(memberQnA)
                .where(memberQnA.member.userId.eq(userId));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    @Override
    public MemberQnADTO findByQnAId(long memberQnAId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        MemberQnADTO.class
                        , memberQnA.id.as("memberQnAId")
                        , qnAClassification.qnaClassificationName.as("qnaClassification")
                        , memberQnA.memberQnATitle.as("qnaTitle")
                        , new CaseBuilder()
                                .when(memberQnA.member.nickname.isNull())
                                .then(memberQnA.member.userName)
                                .otherwise(memberQnA.member.nickname)
                                .as("writer")
                        , memberQnA.memberQnAContent.as("qnaContent")
                        , memberQnA.updatedAt
                        , memberQnA.memberQnAStat
                )
        )
                .from(memberQnA)
                .innerJoin(qnAClassification)
                .on(memberQnA.qnAClassification.id.eq(qnAClassification.id))
                .where(memberQnA.id.eq(memberQnAId))
                .fetchOne();
    }

    @Override
    public MemberQnA findModifyDataByIdAndUserId(long qnaId, String userId) {
        return jpaQueryFactory.select(memberQnA)
                .from(memberQnA)
                .where(
                        memberQnA.id.eq(qnaId)
                                .and(memberQnA.member.userId.eq(userId))
                )
                .fetchOne();
    }

    @Override
    public Page<AdminQnAListResponseDTO> findAllByAdminMemberQnA(AdminPageDTO pageDTO, String listType, Pageable pageable) {

        List<AdminQnAListResponseDTO> list = jpaQueryFactory.select(
                                                Projections.constructor(
                                                        AdminQnAListResponseDTO.class
                                                        , memberQnA.id.as("qnaId")
                                                        , qnAClassification.qnaClassificationName.as("classification")
                                                        , memberQnA.memberQnATitle.as("title")
                                                        , new CaseBuilder()
                                                                .when(memberQnA.member.nickname.isNull())
                                                                .then(memberQnA.member.userName)
                                                                .otherwise(memberQnA.member.nickname)
                                                                .as("writer")
                                                        , memberQnA.updatedAt.as("createdAt")
                                                        , memberQnA.memberQnAStat.as("answerStatus")
                                                )
                                        )
                                        .from(memberQnA)
                                        .innerJoin(qnAClassification)
                                        .on(memberQnA.qnAClassification.id.eq(qnAClassification.id))
                                        .where(adminMemberQnASearch(pageDTO, listType))
                                        .orderBy(memberQnA.updatedAt.desc())
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(memberQnA.countDistinct())
                                    .from(memberQnA)
                                    .where(adminMemberQnASearch(pageDTO, listType));

        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    public BooleanExpression adminMemberQnASearch(AdminPageDTO pageDTO, String listType){
        if(listType.equals("new")){
            if(pageDTO.keyword() != null)
                return memberQnA.member.userId.eq(pageDTO.keyword()).and(memberQnA.memberQnAStat.isFalse());
            else
                return memberQnA.memberQnAStat.isFalse();
        }else if(listType.equals("all")) {
            if(pageDTO.keyword() != null)
                return memberQnA.member.userId.eq(pageDTO.keyword());
        }

        return null;
    }
}

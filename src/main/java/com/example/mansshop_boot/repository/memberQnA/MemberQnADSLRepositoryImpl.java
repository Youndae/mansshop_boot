package com.example.mansshop_boot.repository.memberQnA;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.example.mansshop_boot.domain.entity.QMemberQnA.memberQnA;
import static com.example.mansshop_boot.domain.entity.QQnAClassification.qnAClassification;

@Repository
@RequiredArgsConstructor
public class MemberQnADSLRepositoryImpl implements MemberQnADSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager em;

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
    public List<AdminQnAListResponseDTO> findAllByAdminMemberQnA(AdminOrderPageDTO pageDTO) {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT q.id, ")
                .append("qc.qnaClassificationName, ")
                .append("q.memberQnATitle, ");

        if(pageDTO.keyword() == null)
            queryBuilder.append("CASE WHEN (m.nickname IS NULL) THEN m.userName ELSE m.nickname END, ");
        else
            queryBuilder.append("q.userId, ");

        queryBuilder.append("q.updatedAt, ")
                    .append("q.memberQnAStat ")
                    .append("FROM (")
                    .append(adminMemberQnADynamicSubQuery(pageDTO))
                    .append(") as q ")
                    .append("INNER JOIN qnaClassification qc ")
                    .append("ON q.qnaClassificationId = qc.id ");

        if(pageDTO.keyword() == null)
            queryBuilder.append("LEFT JOIN member m ")
                        .append("ON m.userId = q.userId");

        Query query = em.createNativeQuery(queryBuilder.toString());

        query.setParameter("offset", pageDTO.offset());
        query.setParameter("amount", pageDTO.amount());

        if(pageDTO.keyword() != null)
            query.setParameter("keyword", pageDTO.keyword());

        List<Object[]> resultList = query.getResultList();

        return resultList.stream()
                            .map(val -> new AdminQnAListResponseDTO(
                                    ((Number) val[0]).longValue(),
                                    (String) val[1],
                                    (String) val[2],
                                    (String) val[3],
                                    ((Timestamp) val[4]).toLocalDateTime(),
                                    (Boolean) val[5]
                            ))
                            .toList();
    }

    public String adminMemberQnADynamicSubQuery(AdminOrderPageDTO pageDTO) {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT mq.id, ")
                .append("mq.qnaClassificationId, ")
                .append("mq.memberQnATitle, ")
                .append("mq.updatedAt, ")
                .append("mq.memberQnAStat, ");

        if(pageDTO.keyword() == null)
            queryBuilder.append("mq.userId ")
                    .append("FROM memberQnA mq ")
                    .append("WHERE 1=1 ");
        else
            queryBuilder.append("CASE WHEN (m.nickname IS NULL) THEN m.userName ELSE m.nickname END, ")
                    .append("FROM memberQnA mq ")
                    .append("INNER JOIN member m ")
                    .append("ON mq.userId = m.userId ")
                    .append("WHERE (m.nickname = :keyword OR m.userId = :keyword) ");

        queryBuilder.append(adminMemberQnASubQuerySearch(pageDTO))
                .append("ORDER BY mq.updatedAt DESC LIMIT :offset, :amount");

        return queryBuilder.toString();
    }

    public String adminMemberQnASubQuerySearch(AdminOrderPageDTO pageDTO) {
        String query = "";

        if(pageDTO.searchType().equals("new"))
            query = "AND mq.memberQnAStat = 0 ";

        return query;
    }

    @Override
    public Long findAllByAdminMemberQnACount(AdminOrderPageDTO pageDTO) {
        return jpaQueryFactory.select(memberQnA.updatedAt.count())
                .from(memberQnA)
                .where(adminMemberQnASearch(pageDTO))
                .fetchOne();
    }

    public BooleanExpression adminMemberQnASearch(AdminOrderPageDTO pageDTO){
        if(pageDTO.searchType().equals("new")){
            if(pageDTO.keyword() != null)
                return memberQnA.member.nickname.eq(pageDTO.keyword()).and(memberQnA.memberQnAStat.isFalse());
            else
                return memberQnA.memberQnAStat.isFalse();
        }else if(pageDTO.searchType().equals("all")) {
            if(pageDTO.keyword() != null)
                return memberQnA.member.nickname.eq(pageDTO.keyword());
        }

        return null;
    }
}

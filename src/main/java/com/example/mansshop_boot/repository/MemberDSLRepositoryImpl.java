package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberDSLRepositoryImpl implements MemberDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findByLocalUserId(String userId) {

        return jpaQueryFactory.select(member)
                .from(member)
                .where(member.userId.eq(userId).and(member.provider.eq("local")))
                .fetch()
                .get(0);
    }

    @Override
    public Page<AdminMemberDTO> findMember(AdminPageDTO pageDTO, Pageable pageable) {

        List<AdminMemberDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminMemberDTO.class
                        , member.userId
                        , member.nickname
                        , member.phone
                        , member.userEmail.as("email")
                        , member.birth
                        , member.memberPoint.as("point")
                        , member.createdAt
                )
        )
                .from(member)
                .where(searchAdminMember(pageDTO))
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory.select(member.countDistinct())
                                        .from(member)
                                        .where(searchAdminMember(pageDTO));


        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);
    }

    public BooleanExpression searchAdminMember(AdminPageDTO pageDTO) {
        if(pageDTO.keyword() != null)
            return member.userId.eq(pageDTO.keyword());

        return null;
    }
}

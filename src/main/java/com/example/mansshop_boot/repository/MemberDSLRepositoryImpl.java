package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}

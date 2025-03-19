package com.example.mansshop_boot.repository.member;

import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mansshop_boot.domain.entity.QMember.member;
import static com.example.mansshop_boot.domain.entity.QAuth.auth1;

@Repository
@RequiredArgsConstructor
public class MemberDSLRepositoryImpl implements MemberDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findByLocalUserId(String userId) {

        return jpaQueryFactory.selectFrom(member)
                .leftJoin(member.auths, auth1).fetchJoin()
                .where(member.userId.eq(userId).and(member.provider.eq("local")))
                .fetchOne();
    }

    @Override
    public Member findByUserId(String userId) {

        return jpaQueryFactory.selectFrom(member)
                .leftJoin(member.auths, auth1).fetchJoin()
                .where(member.userId.eq(userId).and(member.provider.eq("local")))
                .fetchOne();
    }

    @Override
    public Page<AdminMemberDTO> findMember(AdminOrderPageDTO pageDTO, Pageable pageable) {

        List<AdminMemberDTO> list = jpaQueryFactory.select(
                Projections.constructor(
                        AdminMemberDTO.class
                        , member.userId
                        , member.userName
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

    public BooleanExpression searchAdminMember(AdminOrderPageDTO pageDTO) {

        if(pageDTO.keyword() == null)
            return null;
        else if(pageDTO.searchType().equals("userId"))
            return member.userId.eq(pageDTO.keyword());
        else if(pageDTO.searchType().equals("userName"))
            return member.userName.eq(pageDTO.keyword());
        else if(pageDTO.searchType().equals("nickname"))
            return member.nickname.eq(pageDTO.keyword());

        return null;
    }

    @Override
    public String searchId(UserSearchDTO searchDTO) {
        return jpaQueryFactory.select(member.userId)
                .from(member)
                .where(memberSearchId(searchDTO))
                .fetchOne();
    }

    public BooleanExpression memberSearchId(UserSearchDTO searchDTO) {
        if(searchDTO.userPhone() == null)
            return member.userName.eq(searchDTO.userName()).and(member.userEmail.eq(searchDTO.userEmail()));
        else if(searchDTO.userEmail() == null)
            return member.userName.eq(searchDTO.userName()).and(member.phone.eq(searchDTO.userPhone()));

        return member.userName.eq("");
    }

    @Override
    public Long findByPassword(UserSearchPwDTO searchDTO) {
        return jpaQueryFactory.select(member.countDistinct())
                .from(member)
                .where(
                        member.userId.eq(searchDTO.userId())
                                .and(member.userName.eq(searchDTO.userName()))
                                .and(member.userEmail.eq(searchDTO.userEmail()))
                )
                .fetchOne();
    }

}

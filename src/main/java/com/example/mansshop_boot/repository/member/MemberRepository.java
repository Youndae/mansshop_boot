package com.example.mansshop_boot.repository.member;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.member.MemberDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>, MemberDSLRepository {

    Member findByNickname(String nickname);
}

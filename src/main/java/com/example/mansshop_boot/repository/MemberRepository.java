package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>, MemberDSLRepository {

    Member findByNickname(String nickname);
}

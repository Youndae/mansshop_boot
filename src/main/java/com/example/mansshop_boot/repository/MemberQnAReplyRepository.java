package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.MemberQnAReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQnAReplyRepository extends JpaRepository<MemberQnAReply, Long>, MemberQnAReplyDSLRepository {
}

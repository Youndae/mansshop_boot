package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.Member;

public interface MemberDSLRepository {

    Member findByLocalUserId(String userId);
}

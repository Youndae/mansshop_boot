package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberDSLRepository {

    Member findByLocalUserId(String userId);

    Page<AdminMemberDTO> findMember(AdminPageDTO pageDTO, Pageable pageable);
}

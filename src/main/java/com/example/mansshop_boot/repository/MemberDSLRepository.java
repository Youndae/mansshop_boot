package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.member.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberDSLRepository {

    Member findByLocalUserId(String userId);

    Page<AdminMemberDTO> findMember(AdminOrderPageDTO pageDTO, Pageable pageable);

    String searchId(UserSearchDTO searchDTO);

    Long findByPassword(UserSearchPwDTO searchDTO);
}

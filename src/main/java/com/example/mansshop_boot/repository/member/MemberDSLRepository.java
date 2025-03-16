package com.example.mansshop_boot.repository.member;

import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchDTO;
import com.example.mansshop_boot.domain.dto.member.business.UserSearchPwDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberDSLRepository {

    Member findByLocalUserId(String userId);

    Member findByUserId(String userId);

    Page<AdminMemberDTO> findMember(AdminOrderPageDTO pageDTO, Pageable pageable);

    String searchId(UserSearchDTO searchDTO);

    Long findByPassword(UserSearchPwDTO searchDTO);


    List<Member> dummyMemberList();
}

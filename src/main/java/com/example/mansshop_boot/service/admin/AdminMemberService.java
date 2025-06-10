package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import org.springframework.data.domain.Page;

public interface AdminMemberService {

    Page<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO);

    String postPoint(AdminPostPointDTO pointDTO);
}

package com.example.mansshop_boot.repository.memberQnA;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberQnADSLRepository {
    Page<MemberQnAListDTO> findAllByUserId(String userId, Pageable pageable);

    MemberQnADTO findByQnAId(long memberQnAId);

    MemberQnA findModifyDataByIdAndUserId(long qnaId, String userId);

    List<AdminQnAListResponseDTO> findAllByAdminMemberQnA(AdminOrderPageDTO pageDTO);

    Long findAllByAdminMemberQnACount(AdminOrderPageDTO pageDTO);
}

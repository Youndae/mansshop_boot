package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnAListDTO;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberQnADSLRepository {
    Page<MemberQnAListDTO> findAllByUserId(String userId, Pageable pageable);

    MemberQnADTO findByIdAndUserId(long memberQnAId, String userId);

    MemberQnA findModifyDataByIdAndUserId(long qnaId, String userId);
}
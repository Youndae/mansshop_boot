package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberQnAListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberQnADSLRepository {
    Page<MemberQnAListDTO> findAllByUserId(String userId, Pageable pageable);

    MemberQnADTO findByIdAndUserId(long memberQnAId, String userId);
}

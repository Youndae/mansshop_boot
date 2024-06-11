package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MemberQnAReplyDTO;

import java.util.List;

public interface MemberQnAReplyDSLRepository {

    List<MemberQnAReplyDTO> findAllByQnAId(long memberQnAId);
}

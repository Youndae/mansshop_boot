package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;

import java.util.List;

public interface MemberQnAReplyDSLRepository {

    List<MyPageQnAReplyDTO> findAllByQnAId(long memberQnAId);
}

package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;

import java.security.Principal;
import java.util.List;

public interface AdminQnAService {

    PagingListDTO<AdminQnAListResponseDTO> getProductQnAList(AdminOrderPageDTO pageDTO);

    String patchProductQnAComplete(long qnaId);

    String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    String patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal);

    PagingListDTO<AdminQnAListResponseDTO> getMemberQnAList(AdminOrderPageDTO pageDTO);

    String patchMemberQnAComplete(long qnaId);

    String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    List<AdminQnAClassificationDTO> getQnAClassification();

    String postQnAClassification(String classificationName);

    String deleteQnAClassification(long classificationId);
}

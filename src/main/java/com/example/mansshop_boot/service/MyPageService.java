package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;

import java.security.Principal;


public interface MyPageService {

    PagingResponseDTO<MyPageOrderDTO> getOrderList(OrderPageDTO orderPageDTO, MemberOrderDTO memberOrderDTO);

    PagingResponseDTO<ProductLikeDTO> getLikeList(LikePageDTO pageDTO, Principal principal);

    PagingResponseDTO<ProductQnAListDTO> getProductQnAList(MyPagePageDTO pageDTO, Principal principal);

    ProductQnADetailDTO getProductQnADetail(long productQnAId, Principal principal);

    String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    String patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal);

    String deleteProductQnA(long qnaId, Principal principal);

    PagingResponseDTO<MemberQnAListDTO> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal);

    ResponseIdDTO postMemberQnA(MemberQnAInsertDTO insertDTO, Principal principal);

    MemberQnADetailDTO getMemberQnADetail(long memberQnAId, Principal principal);

    String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    String patchMemberQnAReply(QnAReplyDTO replyDTO, Principal principal);

    ResponseDTO<MemberQnAModifyDataDTO> getModifyData(long qnaId, Principal principal);

    String patchMemberQnA(MemberQnAModifyDTO modifyDTO, Principal principal);

    String deleteMemberQnA(long qnaId, Principal principal);

    QnAClassificationResponseDTO getQnAClassification(Principal principal);
}

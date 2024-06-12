package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import org.springframework.http.ResponseEntity;

import java.security.Principal;


public interface MyPageService {

    ResponseEntity<?> getOrderList(OrderPageDTO orderPageDTO, MemberOrderDTO memberOrderDTO);

    ResponseEntity<?> getLikeList(LikePageDTO pageDTO, Principal principal);

    ResponseEntity<?> getProductQnAList(MyPagePageDTO pageDTO, Principal principal);

    ResponseEntity<?> getProductQnADetail(long productQnAId, Principal principal);

    ResponseEntity<?> patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal);

    ResponseEntity<?> postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    ResponseEntity<?> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal);

    ResponseEntity<?> getMemberQnADetail(long memberQnAId, Principal principal);

    ResponseEntity<?> patchMemberQnAReply(QnAReplyDTO replyDTO, Principal principal);

    ResponseEntity<?> postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    ResponseEntity<?> postMemberQnA(MemberQnAInsertDTO insertDTO, Principal principal);

    ResponseEntity<?> getModifyData(long qnaId, Principal principal);

    ResponseEntity<?> patchMemberQnA(MemberQnAModifyDTO modifyDTO, Principal principal);

    ResponseEntity<?> getQnAClassification(Principal principal);
}

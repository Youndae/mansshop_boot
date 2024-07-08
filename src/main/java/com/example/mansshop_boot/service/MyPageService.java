package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;


public interface MyPageService {

    PagingListDTO<MyPageOrderDTO> getOrderList(OrderPageDTO orderPageDTO, MemberOrderDTO memberOrderDTO);

    Page<ProductLikeDTO> getLikeList(LikePageDTO pageDTO, Principal principal);

    Page<ProductQnAListDTO> getProductQnAList(MyPagePageDTO pageDTO, Principal principal);

    ProductQnADetailDTO getProductQnADetail(long productQnAId, Principal principal);

    ProductQnADetailDTO getProductQnADetailData(long productQnAId);

    String deleteProductQnA(long qnaId, Principal principal);

    Page<MemberQnAListDTO> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal);

    MemberQnADetailDTO getMemberQnADetailData(long qnaId);

    Long postMemberQnA(MemberQnAInsertDTO insertDTO, Principal principal);

    MemberQnADetailDTO getMemberQnADetail(long memberQnAId, Principal principal);

    String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    String patchMemberQnAReply(QnAReplyDTO replyDTO, Principal principal);

    MemberQnAModifyDataDTO getModifyData(long qnaId, Principal principal);

    String patchMemberQnA(MemberQnAModifyDTO modifyDTO, Principal principal);

    String deleteMemberQnA(long qnaId, Principal principal);

    List<QnAClassificationDTO> getQnAClassification(Principal principal);

    Page<MyPageReviewDTO> getReview(MyPagePageDTO pageDTO, Principal principal);

    MyPagePatchReviewDataDTO getPatchReview(long reviewId, Principal principal);

    String patchReview(MyPagePatchReviewDTO reviewDTO, Principal principal);

    String deleteReview(long reviewId, Principal principal);

    String postReview(MyPagePostReviewDTO reviewDTO, Principal principal);

    MyPageInfoDTO getInfo(Principal principal);

    String patchInfo(MyPageInfoPatchDTO infoDTO, Principal principal);
}

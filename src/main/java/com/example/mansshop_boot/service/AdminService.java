package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface AdminService {

    PagingListDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO);

    List<String> getClassification();

    AdminProductDetailDTO getProductDetail(String productId);

    AdminProductPatchDataDTO getPatchProductData(String productId);

    String postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    String patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    PagingListDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO);

    PagingListDTO<AdminDiscountResponseDTO> getDiscountProduct(AdminPageDTO pageDTO);

    List<AdminDiscountProductDTO> getSelectDiscountProduct(String classification);

    String patchDiscountProduct(AdminDiscountPatchDTO patchDTO);

    PagingListDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO);

    PagingListDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO);

    String orderPreparation(long orderId);

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

    PagingListDTO<AdminReviewDTO> getReviewList(AdminOrderPageDTO pageDTO, AdminListType listType);

    Page<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO);

    String postPoint(AdminPostPointDTO pointDTO);

    AdminPeriodSalesResponseDTO getPeriodSales(int term);

    AdminPeriodMonthDetailResponseDTO getPeriodSalesDetail(String term);

    AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification);

    AdminPeriodSalesResponseDTO getSalesByDay(String term);

    PagingListDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page);

    Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO);

    AdminProductSalesDetailDTO getProductSalesDetail(String productId);

    AdminReviewDetailDTO getReviewDetail(long reviewId);

    String postReviewReply(AdminReviewRequestDTO postDTO, Principal principal);

    List<FailedQueueDTO> getFailedMessageList();

    String retryFailedMessages(List<FailedQueueDTO> queueDTOList);
}

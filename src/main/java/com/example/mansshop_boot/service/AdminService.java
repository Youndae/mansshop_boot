package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface AdminService {

    PagingListDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO);

    List<String> getClassification();

    AdminProductDetailDTO getProductDetail(String productId);

    AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal);

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

    Page<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO);

    String postPoint(AdminPostPointDTO pointDTO);

    AdminPeriodSalesResponseDTO getPeriodSales(int term);

    AdminPeriodMonthDetailResponseDTO getPeriodSalesDetail(String term);

    AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification);

    AdminPeriodSalesResponseDTO getSalesByDay(String term);

    PagingListDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page);

    Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO);

    AdminProductSalesDetailDTO getProductSalesDetail(String productId);
}

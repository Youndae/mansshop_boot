package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.MemberQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.ProductQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;

import java.security.Principal;
import java.util.List;

public interface AdminService {

    PagingResponseDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO);

    ResponseDTO<AdminProductDetailDTO> getProductDetail(String productId);

    AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal);

    ResponseIdDTO<String> postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    ResponseIdDTO<String> patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    PagingResponseDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO);

    PagingResponseDTO<AdminDiscountResponseDTO> getDiscountProduct(AdminPageDTO pageDTO);

    ResponseListDTO<String> getClassification();

    ResponseListDTO<AdminDiscountProductDTO> getSelectDiscountProduct(String classification);

    ResponseMessageDTO patchDiscountProduct(AdminDiscountPatchDTO patchDTO);

    PagingResponseDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO);

    PagingElementsResponseDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO);

    String orderPreparation(long orderId);

    PagingResponseDTO<AdminQnAListResponseDTO> getProductQnAList(AdminOrderPageDTO pageDTO);

    ProductQnADetailDTO getProductQnADetail(long qnaId);

    String patchProductQnAComplete(long qnaId);

    String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    PagingResponseDTO<AdminQnAListResponseDTO> getMemberQnAList(AdminOrderPageDTO pageDTO);

    MemberQnADetailDTO getMemberQnADetail(long qnaId);

    String patchMemberQnAComplete(long qnaId);

    String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal);

    ResponseListDTO<AdminQnAClassificationDTO> getQnAClassification();

    String postQnAClassification(String classificationName);

    String deleteQnAClassification(long classificationId);

    PagingResponseDTO<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO);

    String postPoint(AdminPostPointDTO pointDTO);

    ResponseDTO<AdminPeriodSalesResponseDTO> getPeriodSales(int term);

    ResponseDTO<AdminPeriodMonthDetailResponseDTO> getPeriodSalesDetail(String term);

    AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification);

    AdminPeriodSalesResponseDTO getSalesByDay(String term);

    PagingElementsResponseDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page);

    PagingElementsResponseDTO<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO);

    ResponseDTO<AdminProductSalesDetailDTO> getProductSalesDetail(String productId);
}

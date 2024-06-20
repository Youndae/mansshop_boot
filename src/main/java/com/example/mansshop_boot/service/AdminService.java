package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;

import java.security.Principal;
import java.util.List;

public interface AdminService {

    PagingResponseDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO);

    ResponseDTO<AdminProductDetailDTO> getProductDetail(String productId);

    AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal);

    ResponseListDTO<String> getClassification();

    ResponseIdDTO<String> postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    ResponseIdDTO<String> patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    PagingResponseDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO);

    PagingResponseDTO<AdminDiscountResponseDTO> getDiscountProduct(AdminPageDTO pageDTO);

    ResponseListDTO<AdminDiscountProductDTO> getSelectDiscountProduct(String classification);

    ResponseMessageDTO patchDiscountProduct(AdminDiscountPatchDTO patchDTO);

    PagingResponseDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO);

    PagingResponseDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO);
}

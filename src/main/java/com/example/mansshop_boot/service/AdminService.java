package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;

import java.security.Principal;
import java.util.List;

public interface AdminService {

    PagingResponseDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO);

    ResponseDTO<AdminProductDetailDTO> getProductDetail(String productId);

    AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal);

    ResponseListDTO<String> getClassification(Principal principal);

    ResponseIdDTO<String> postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    ResponseIdDTO<String> patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO);

    PagingResponseDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO);
}

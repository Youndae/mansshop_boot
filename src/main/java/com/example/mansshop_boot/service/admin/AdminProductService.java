package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;

import java.util.List;

public interface AdminProductService {
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
}

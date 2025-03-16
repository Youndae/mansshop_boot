package com.example.mansshop_boot.repository.product;

import com.example.mansshop_boot.domain.dto.admin.out.AdminDiscountProductDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminProductListDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductStockDataDTO;
import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import com.example.mansshop_boot.domain.dto.order.business.ProductIdClassificationDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductDSLRepository {

    List<MainListDTO> findListDefault(MainPageDTO pageDTO);

    Page<MainListDTO> findListPageable(MainPageDTO pageDTO, Pageable pageable);

    List<Product> findAllByIdList(List<String> productIdList);

    List<AdminProductListDTO> findAdminProductList(AdminPageDTO pageDTO);

    Long findAdminProductListCount(AdminPageDTO pageDTO);

    List<AdminProductStockDataDTO> findStockData(AdminPageDTO pageDTO);

    Long findStockCount(AdminPageDTO pageDTO);

    Page<Product> getDiscountProduct(AdminPageDTO pageDTO, Pageable pageable);

    List<AdminDiscountProductDTO> getProductByClassification(String classification);

    void patchProductDiscount(AdminDiscountPatchDTO patchDTO);

    List<ProductIdClassificationDTO> findClassificationAllByProductIds(List<String> productIds);

    void patchProductSalesQuantity(Map<String, Integer> productMap);
}

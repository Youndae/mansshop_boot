package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;

public interface AdminSalesService {

    AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> getPeriodSales(int term);

    AdminPeriodMonthDetailResponseDTO getPeriodSalesDetail(String term);

    AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification);

    AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> getSalesByDay(String term);

    PagingListDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page);

    Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO);

    AdminProductSalesDetailDTO getProductSalesDetail(String productId);

}

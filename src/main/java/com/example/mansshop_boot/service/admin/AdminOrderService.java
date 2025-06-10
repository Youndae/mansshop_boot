package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.out.AdminOrderResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;

public interface AdminOrderService {

    PagingListDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO);

    PagingListDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO);

    String orderPreparation(long orderId);
}

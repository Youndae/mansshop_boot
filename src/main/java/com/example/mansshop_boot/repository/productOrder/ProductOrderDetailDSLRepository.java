package com.example.mansshop_boot.repository.productOrder;


import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.entity.ProductOrderDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderDetailDSLRepository {
    List<MyPageOrderDetailDTO> findByDetailList(List<Long> orderIdList);

    List<AdminOrderDetailListDTO> findByOrderIds(List<Long> orderIdList);

}

package com.example.mansshop_boot.repository;


import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDetailDTO;

import java.util.List;

public interface ProductOrderDetailDSLRepository {
    List<MyPageOrderDetailDTO> findByDetailList(List<Long> orderIdList);
}

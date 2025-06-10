package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.enumeration.AdminListType;

import java.security.Principal;

public interface AdminReviewService {

    PagingListDTO<AdminReviewDTO> getReviewList(AdminOrderPageDTO pageDTO, AdminListType listType);

    AdminReviewDetailDTO getReviewDetail(long reviewId);

    String postReviewReply(AdminReviewRequestDTO postDTO, Principal principal);
}

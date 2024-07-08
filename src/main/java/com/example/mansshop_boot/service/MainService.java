package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;

import java.security.Principal;
import java.util.List;

public interface MainService {

    List<MainListResponseDTO> getBestAndNewList(MemberPageDTO pageDTO, Principal principal);

    PagingListDTO<MainListResponseDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal);
}

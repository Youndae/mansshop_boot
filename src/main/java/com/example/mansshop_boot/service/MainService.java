package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.MainResponseDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;

import java.security.Principal;

public interface MainService {

    MainResponseDTO getBestAndNewList(MemberPageDTO pageDTO, Principal principal);

    PagingResponseDTO<MainListDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal);
}

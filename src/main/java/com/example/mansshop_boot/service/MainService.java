package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface MainService {

    ResponseListDTO<MainListDTO> getBestAndNewList(MemberPageDTO pageDTO, Principal principal);

    PagingResponseDTO<MainListDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal);
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface MainService {

    List<MainListResponseDTO> getBestAndNewList(MainPageDTO pageDTO);

    PagingListDTO<MainListResponseDTO> getClassificationAndSearchList(MainPageDTO pageDTO);

}

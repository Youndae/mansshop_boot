package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductDSLRepository {

    List<MainListDTO> findListDefault(MemberPageDTO pageDTO);

    Page<MainListDTO> findListPageable(MemberPageDTO pageDTO, Pageable pageable);
}

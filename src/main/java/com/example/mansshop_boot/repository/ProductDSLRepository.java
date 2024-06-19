package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminProductListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductDSLRepository {

    List<MainListDTO> findListDefault(MemberPageDTO pageDTO);

    Page<MainListDTO> findListPageable(MemberPageDTO pageDTO, Pageable pageable);

    List<Product> findAllByIdList(List<String> productIdList);

    Page<AdminProductListDTO> findAdminProductList(AdminPageDTO pageDTO, Pageable pageable);
}

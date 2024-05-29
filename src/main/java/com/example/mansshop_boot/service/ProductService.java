package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;

import java.security.Principal;

public interface ProductService {

    ProductDetailDTO getDetail(String productId, Principal principal);
}

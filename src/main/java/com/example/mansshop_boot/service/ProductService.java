package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.product.in.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Map;

public interface ProductService {

    ProductDetailDTO getDetail(String productId, Principal principal);

    Page<ProductReviewDTO> getDetailReview(ProductDetailPageDTO pageDTO, String productId);

    Page<ProductQnAResponseDTO> getDetailQnA(ProductDetailPageDTO pageDTO, String productId);

    String likeProduct(Map<String, String> productId, Principal principal);

    String deLikeProduct(String productId, Principal principal);

    String postProductQnA(ProductQnAPostDTO postDTO, Principal principal);
}

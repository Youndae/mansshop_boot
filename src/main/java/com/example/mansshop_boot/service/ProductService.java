package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.ProductPageableDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface ProductService {

    ProductDetailDTO getDetail(String productId, Principal principal);

    ProductPageableDTO<ProductReviewDTO> getDetailReview(ProductDetailPageDTO pageDTO, String productId);

    ProductPageableDTO<ProductQnAResponseDTO> getDetailQnA(ProductDetailPageDTO pageDTO, String productId);

    ResponseEntity likeProduct(String productId, Principal principal);

    ResponseEntity deLikeProduct(String productId, Principal principal);
}

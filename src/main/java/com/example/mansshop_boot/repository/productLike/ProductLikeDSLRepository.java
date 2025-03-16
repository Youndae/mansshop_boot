package com.example.mansshop_boot.repository.productLike;

import com.example.mansshop_boot.domain.dto.mypage.out.ProductLikeDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.entity.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductLikeDSLRepository {

    int countByUserIdAndProductId(String userId, String productId);

    Long deleteByUserIdAndProductId(ProductLike productLike);

    Page<ProductLikeDTO> findByUserId(LikePageDTO pageDTO, String userId, Pageable pageable);
}

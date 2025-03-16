package com.example.mansshop_boot.repository.productLike;

import com.example.mansshop_boot.domain.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long>, ProductLikeDSLRepository {


}

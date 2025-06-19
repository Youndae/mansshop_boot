package com.example.mansshop_boot.repository.productLike;

import com.example.mansshop_boot.domain.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long>, ProductLikeDSLRepository {

    //ProductService Integration test QueryMethod
    List<ProductLike> findByMember_UserId(String memberUserId);
}

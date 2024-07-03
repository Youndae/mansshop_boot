package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.cart.CartDetailDTO;
import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.entity.CartDetail;

import java.util.List;

public interface CartDetailDSLRepository {

    List<CartDetailDTO> findAllByCartId(long cartId);

    Long countByCartId(long cartId);

    List<CartDetail> findAllCartDetailByCartId(long cartId);

    List<CartDetail> findAllCartDetailByCartIdAndOptionIds(long cartId, List<Long> optionIds);
}

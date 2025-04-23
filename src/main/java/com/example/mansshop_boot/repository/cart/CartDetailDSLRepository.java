package com.example.mansshop_boot.repository.cart;

import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.CartDetail;

import java.util.List;

public interface CartDetailDSLRepository {

    List<CartDetailDTO> findAllByCartId(long cartId);

    List<Long> findAllIdByCartId(long cartId);

    List<CartDetail> findAllCartDetailByCartId(long cartId);

    List<CartDetail> findAllCartDetailByCartIdAndOptionIds(long cartId, List<Long> optionIds);
}

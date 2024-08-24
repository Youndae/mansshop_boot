package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.List;

public interface CartService {

    List<CartDetailDTO> getCartList(CartMemberDTO cartMemberDTO);

    String addCart(List<AddCartDTO> addList, CartMemberDTO cartMemberDTO, HttpServletResponse response, Principal principal);

    String deleteAllCart(CartMemberDTO cartMemberDTO, HttpServletResponse response);

    String countUp(CartMemberDTO cartMemberDTO, long cartDetailId);

    String countDown(CartMemberDTO cartMemberDTO, long cartDetailId);

    String deleteCartSelect(CartMemberDTO cartMemberDTO, List<Long> deleteCartDetailId);

    CartMemberDTO getCartMemberDTO(HttpServletRequest request, Principal principal);

}

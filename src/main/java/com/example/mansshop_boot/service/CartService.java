package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface CartService {

    ResponseEntity<?> getCartList(CartMemberDTO cartMemberDTO);

    ResponseEntity<?> addCart(List<AddCartDTO> addList, CartMemberDTO cartMemberDTO, HttpServletResponse response, Principal principal);

    ResponseEntity<?> countUp(CartMemberDTO cartMemberDTO, long cartDetailId);

    ResponseEntity<?> countDown(CartMemberDTO cartMemberDTO, long cartDetailId);

    ResponseEntity<?> deleteAllCart(CartMemberDTO cartMemberDTO, HttpServletResponse response);

    ResponseEntity<?> deleteCartSelect(CartMemberDTO cartMemberDTO, List<Long> deleteCartDetailId);

    CartMemberDTO getCartMemberDTO(HttpServletRequest request, Principal principal);

}

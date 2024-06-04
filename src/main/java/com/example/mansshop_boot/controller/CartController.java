package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.cart.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    //사용자 장바구니 목록
    @GetMapping("/")
    public ResponseEntity<?> getCartList(HttpServletRequest request, Principal principal) {

        return null;
    }

    //장바구니 상품 추가
    @PostMapping("/")
    public ResponseEntity<?> addCart(@RequestBody AddCartDTO addList, HttpServletRequest request, HttpServletResponse response, Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        return cartService.addCart(addList.addList(), cartMemberDTO, response, principal);
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteCart(Principal principal, HttpServletRequest request, HttpServletResponse response) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        return cartService.deleteAllCart(cartMemberDTO, response);
    }

}

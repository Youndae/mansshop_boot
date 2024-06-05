package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.cart.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    //사용자 장바구니 목록
    @GetMapping("/")
    public ResponseEntity<?> getCartList(HttpServletRequest request, Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        if(cartMemberDTO.uid() == null && cartMemberDTO.cartCookieValue() == null)
            return ResponseEntity.status(HttpStatus.OK).body(null);


        return cartService.getCartList(cartMemberDTO);
    }

    //장바구니 상품 추가
    @PostMapping("/")
    public ResponseEntity<?> addCart(@RequestBody AddCartDTO addList, HttpServletRequest request, HttpServletResponse response, Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        return cartService.addCart(addList.addList(), cartMemberDTO, response, principal);
    }

    @PatchMapping("/count-up/{cartDetailId}")
    public ResponseEntity<?> cartCountUp(@PathVariable(name = "cartDetailId") long cartDetailId, HttpServletRequest request, Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);


        return cartService.countUp(cartMemberDTO, cartDetailId);
    }

    @PatchMapping("/count-down/{cartDetailId}")
    public ResponseEntity<?> cartCountDown(@PathVariable(name = "cartDetailId") long cartDetailId, HttpServletRequest request, Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        return cartService.countDown(cartMemberDTO, cartDetailId);
    }

    /*
        Memo

        axios 에서 delete 요청에 대해 쿼리 스트링이 아닌 data를 담아 보내기 위해서는 두번재 중괄호인 config에 작성한다.
        그리고 서버에서 RequestBody로 받기 위해서는 data : { } 와 같은 형태로 처리해야 한다.
        그렇지 않으면 RequestBody로 받을 수 없다.
        또한 받더라도 고려해야 할 사항이 있다.

        1. data: { cartDetailList: reqData }
            이렇게 보내는 경우 Map으로도 받을 수 없으며 String으로 받을 수 있다.
            "{cartDetailList:[5, 6, 7, 8]}"과 같은 형태로 받게 된다.
        2. data: { reqData }
            이렇게 보내는 경우 "reqData:[5, 6, 7, 8]" 형태로 전달된다.
        3. data: { ...reqData }
            이렇게 보내니 {0=5, 1=6, 2=7, 3=8} 형태로 전달되며 Map으로 매핑이 가능해진다.
     */
    @DeleteMapping("/select")
    public ResponseEntity<?> deleteSelectCart(@RequestBody Map<String, Long> deleteSelectId, HttpServletRequest request, Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        List<Long> cartDetailIdList = new ArrayList<>();
        deleteSelectId.forEach((k, v) -> cartDetailIdList.add(v));

        cartDetailIdList.forEach(v -> System.out.println("list : " + v));


        return cartService.deleteCartSelect(cartMemberDTO, cartDetailIdList);
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteCart(Principal principal, HttpServletRequest request, HttpServletResponse response) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        return cartService.deleteAllCart(cartMemberDTO, response);
    }

}

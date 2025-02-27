package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.CartService;
import com.example.mansshop_boot.service.ResponseMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param request
     * @param principal
     *
     * 사용자의 장바구니 데이터 조회
     */
    @Operation(summary = "장바구니 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @GetMapping("/")
    public ResponseEntity<ResponseListDTO<CartDetailDTO>> getCartList(HttpServletRequest request,
                                                                      Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        if(cartMemberDTO.uid() == null && cartMemberDTO.cartCookieValue() == null)
            return ResponseEntity.status(HttpStatus.OK).body(null);

        List<CartDetailDTO> responseDTO = cartService.getCartList(cartMemberDTO);
        return responseMappingService.mappingResponseListDTO(responseDTO, principal);
    }

    /**
     *
     * @param addList
     * @param request
     * @param response
     * @param principal
     *
     * 상품 상세 페이지에서 장바구니 담기
     */
    @Operation(summary = "상품 상세 페이지에서 장바구니에 담기 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요. 생략하면 새로운 Cookie 발급이 되면서 새로운 장바구니에 상품이 담김.",
            in = ParameterIn.COOKIE
    )
    @PostMapping("/")
    public ResponseEntity<ResponseMessageDTO> addCart(@RequestBody List<AddCartDTO> addList,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = cartService.addCart(addList, cartMemberDTO, response, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param cartDetailId
     * @param request
     * @param principal
     *
     * 장바구니내 상품 수량 증가
     */
    @Operation(summary = "장바구니 상품 수량 증가")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @Parameter(name = "cartDetailId",
            description = "장바구니 상세 데이터 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/count-up/{cartDetailId}")
    public ResponseEntity<ResponseMessageDTO> cartCountUp(@PathVariable(name = "cartDetailId") long cartDetailId,
                                                          HttpServletRequest request,
                                                          Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);


        String responseMessage = cartService.countUp(cartMemberDTO, cartDetailId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param cartDetailId
     * @param request
     * @param principal
     *
     * 장바구니내 상품 수량 감소
     */
    @Operation(summary = "장바구니 상품 수량 감소")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @Parameter(name = "cartDetailId",
            description = "장바구니 상세 데이터 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/count-down/{cartDetailId}")
    public ResponseEntity<ResponseMessageDTO> cartCountDown(@PathVariable(name = "cartDetailId") long cartDetailId,
                                                            HttpServletRequest request,
                                                            Principal principal) {
        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = cartService.countDown(cartMemberDTO, cartDetailId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
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

    /**
     *
     * @param deleteSelectId
     * @param request
     * @param principal
     *
     * 장바구니 선택 상품 삭제
     */
    @Operation(summary = "장바구니 선택 상품 삭제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @DeleteMapping("/select")
    public ResponseEntity<ResponseMessageDTO> deleteSelectCart(@RequestBody List<Long> deleteSelectId,
                                                               HttpServletRequest request,
                                                               Principal principal) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = cartService.deleteCartSelect(cartMemberDTO, deleteSelectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param principal
     * @param request
     * @param response
     *
     * 장바구니 모든 상품 삭제
     */
    @Operation(summary = "장바구니 상품 전체 삭제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(
            name = "cartCookie",
            description = "비회원인 경우 갖게 되는 장바구니 cookieId. 비회원의 경우 JWT가 아닌 이 쿠키값이 필요.",
            in = ParameterIn.COOKIE
    )
    @DeleteMapping("/all")
    public ResponseEntity<ResponseMessageDTO> deleteCart(Principal principal,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {

        CartMemberDTO cartMemberDTO = cartService.getCartMemberDTO(request, principal);

        String responseMessage = cartService.deleteAllCart(cartMemberDTO, response);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

}

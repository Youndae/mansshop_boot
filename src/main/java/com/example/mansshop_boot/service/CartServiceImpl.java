package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.cart.*;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.CartDetailRepository;
import com.example.mansshop_boot.repository.CartRepository;
import com.example.mansshop_boot.repository.MemberRepository;
import com.example.mansshop_boot.repository.ProductOptionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService{

    @Value("#{jwt['cookie.cart.header']}")
    private String cartCookieHeader;

    @Value("#{jwt['cookie.cart.expirationDay']}")
    private long cartCookieExpirationDay;

    @Value("#{jwt['cookie.cart.uid']}")
    private String nonUserId;

    private final JWTTokenProvider tokenProvider;

    private final CartRepository cartRepository;

    private final MemberRepository memberRepository;

    private final ProductOptionRepository productOptionRepository;

    private final CartDetailRepository cartDetailRepository;


    /*
        출력에 필요한 정보

        상품 대표 썸네일
        상풍명
        옵션 [
            사이즈
            컬러
            수량
            금액
         ]


     */
    @Override
    public ResponseListDTO<CartDetailDTO> getCartList(CartMemberDTO cartMemberDTO) {
        Long userCartId = cartRepository.findIdByUserId(cartMemberDTO);

        if(userCartId == null)
            return new ResponseListDTO<>(null, new UserStatusDTO(cartMemberDTO.uid()));

        List<CartDetailDTO> cartDetailList = cartDetailRepository.findAllByCartId(userCartId);

        return new ResponseListDTO<>(cartDetailList, new UserStatusDTO(cartMemberDTO.uid()));
    }

    /*
            cart에 데이터가 없다면
            cart build 후 cartDetail 을 set 해준 뒤
            cartrepository.save()를 한다.

            cart 데이터가 존재한다면
            cartDetail 데이터만 리스트화 해서 CartDetailRepository.save()를 한다.

         */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String addCart(List<AddCartDTO> addList
                        , CartMemberDTO cartMemberDTO
                        , HttpServletResponse response
                        , Principal principal) {
        String cookieValue = null;
        List<CartDetail> optionListDetail = new ArrayList<>();

        if(cartMemberDTO.uid().equals(nonUserId))
            cookieValue = cartMemberDTO.cartCookieValue() == null ? createAnonymousCookie(response) : cartMemberDTO.cartCookieValue();

        Cart cart = cartRepository.findByUserIdAndCookieValue(
                new CartMemberDTO(cartMemberDTO.uid(), cookieValue)
        );

        if(cart == null) {
            Member member = memberRepository.findById(cartMemberDTO.uid()).orElseThrow(IllegalArgumentException::new);

            cart = Cart.builder()
                            .member(member)
                            .cookieId(cookieValue)
                            .build();
        }else {
            List<Long> addOptionIdList = new ArrayList<>();
            addList.forEach(v -> addOptionIdList.add(v.optionId()));
            optionListDetail = cartDetailRepository.findAllCartDetailByCartIdAndOptionIds(cart.getId(), addOptionIdList);
        }

        for(AddCartDTO detailValue : addList) {
            CartDetail addDetail = CartDetail.builder()
                                            .productOption(
                                                    productOptionRepository.findById(detailValue.optionId())
                                                            .orElseThrow(IllegalArgumentException::new)
                                            )
                                            .cartCount(detailValue.count())
                                            .cartPrice(detailValue.price())
                                            .build();

            if(!optionListDetail.isEmpty()) {
                for(int i = 0; i < optionListDetail.size(); i++) {
                    CartDetail listObject = optionListDetail.get(i);
                    if(detailValue.optionId().equals(listObject.getProductOption().getId())){
                        listObject.setCartCount(detailValue.count());
                        listObject.setCartPrice(detailValue.price());

                        addDetail = listObject;
                        optionListDetail.remove(i);
                        break;
                    }
                }
            }

            cart.addCartDetail(addDetail);
        }

        cartRepository.save(cart);

        return Result.OK.getResultKey();
    }

    /*
        장바구니 전체 삭제 버튼 기능
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String deleteAllCart(CartMemberDTO cartMemberDTO, HttpServletResponse response) {

        Long cartId = cartRepository.findIdByUserId(cartMemberDTO);

        cartRepository.deleteById(cartId);

        return Result.OK.getResultKey();
    }

    @Override
    public String countUp(CartMemberDTO cartMemberDTO, long cartDetailId) {
        Cart cart = cartRepository.findByUserIdAndCookieValue(cartMemberDTO);

        if(cart == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId).orElseThrow(IllegalArgumentException::new);
        cartDetail.countUpDown("up");

        cartDetailRepository.save(cartDetail);


        return Result.OK.getResultKey();
    }

    @Override
    public String countDown(CartMemberDTO cartMemberDTO, long cartDetailId) {
        Cart cart = cartRepository.findByUserIdAndCookieValue(cartMemberDTO);

        if(cart == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId).orElseThrow(IllegalArgumentException::new);
        cartDetail.countUpDown("down");

        cartDetailRepository.save(cartDetail);

        return Result.OK.getResultKey();
    }

    /*
            장바구니 선택 삭제 기능
            선택 목록이 전체일 경우도 감안해야 함.
         */
    @Override
    public String deleteCartSelect(CartMemberDTO cartMemberDTO, List<Long> deleteCartDetailId) {

        Long cartId = cartRepository.findIdByUserId(cartMemberDTO);
        Long cartDetailSize = cartDetailRepository.countByCartId(cartId);

        if(cartDetailSize == deleteCartDetailId.size())
            cartRepository.deleteById(cartId);
        else
            cartDetailRepository.deleteAllById(deleteCartDetailId);


        return Result.OK.getResultKey();
    }

    private String createAnonymousCookie(HttpServletResponse response) {
        String cookieValue = UUID.randomUUID().toString().replace("-", "");

        setCartCookie(cookieValue, response);

        return cookieValue;
    }

    private void setCartCookie(String cookieValue, HttpServletResponse response) {
        tokenProvider.setTokenCookie(
                cartCookieHeader
                , cookieValue
                , Duration.ofDays(cartCookieExpirationDay)
                , response
        );
    }

    /**
     *
     * @param request
     * @param principal
     * @return
     *
     * 장바구니 데이터 처리를 위한 사용자 정보 DTO 생성 및 반환
     * 로그인한 사용자의 경우 DTO.uid에 사용자 아이디, cookieValue 는 null
     * 로그인하지 않은 사용자의 경우 uid에 'Anonymous', cookieValue는 존재하면 반환, 없다면 null
     */
    @Override
    public CartMemberDTO getCartMemberDTO(HttpServletRequest request, Principal principal) {
        String uid = nonUserId;
        String cartCookieValue = null;
        Cookie anonymousCookie = WebUtils.getCookie(request, cartCookieHeader);

        if(principal == null)
            cartCookieValue = anonymousCookie == null ? null : anonymousCookie.getValue();
        else
            uid = principal.getName();

        return CartMemberDTO.builder()
                            .uid(uid)
                            .cartCookieValue(cartCookieValue)
                            .build();
    }
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.cart.in.AddCartDTO;
import com.example.mansshop_boot.domain.dto.cart.out.CartDetailDTO;
import com.example.mansshop_boot.domain.entity.Cart;
import com.example.mansshop_boot.domain.entity.CartDetail;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
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


    /**
     *
     * @param cartMemberDTO
     *
     * 회원의 장바구니 리스트 조회
     * 데이터가 존재하지 않는 경우 오류를 발생시킬 것이 아니라 Null을 반환해 상품이 없다는 문구를 출력하도록 처리.
     */
    @Override
    public List<CartDetailDTO> getCartList(CartMemberDTO cartMemberDTO) {
        Long userCartId = cartRepository.findIdByUserId(cartMemberDTO);

        if(userCartId == null)
            return null;

        return cartDetailRepository.findAllByCartId(userCartId);
    }

    /**
     *
     * @param addList
     * @param cartMemberDTO
     * @param response
     * @param principal
     *
     * 장바구니 추가 기능.
     *
     * 회원, 비회원 모두 사용이 가능하다.
     * 회원은 아이디, 비회원은 'Anonymous'라는 아이디와 발급받은 장바구니 쿠키값이 들어가 구분할 수 있도록 한다.
     * 회원의 경우 쿠키 데이터는 들어가지 않는다.
     *
     * CartMemberDTO에 담긴 상태로 서비스에 넘어오며 컨트롤러에서 CartMemberDTO를 생성한 뒤 담아서 보낸다.
     *
     * 장바구니에 데이터가 하나도 없는 경우 Cart 테이블에 데이터가 존재하지 않기 때문에 Cart Entity를 새로 build 해주고 상품 데이터를 담아 save 처리.
     *
     * Cart 테이블에 데이터가 존재한다면 같은 상품의 경우 수량만 증가시켜주기 위해 추가하고자 하는 데이터가 장바구니 상세 데이터에 존재하는지 체크하고
     * 조건에 따라 처리한다.
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
            List<Long> addOptionIdList = addList.stream().map(AddCartDTO::optionId).toList();
            optionListDetail = cartDetailRepository.findAllCartDetailByCartIdAndOptionIds(cart.getId(), addOptionIdList);
        }

        for(AddCartDTO detailValue : addList) {
            CartDetail addDetail = CartDetail.builder()
                                            .productOption(
                                                    productOptionRepository.findById(detailValue.optionId())
                                                            .orElseThrow(IllegalArgumentException::new)
                                            )
                                            .cartCount(detailValue.count())
                                            .build();

            if(!optionListDetail.isEmpty()) {
                for(int i = 0; i < optionListDetail.size(); i++) {
                    CartDetail listObject = optionListDetail.get(i);
                    if(detailValue.optionId().equals(listObject.getProductOption().getId())){
                        listObject.addCartCount(detailValue.count());

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

    /**
     *
     * @param cartMemberDTO
     * @param response
     *
     * 장바구니 내 상품 전체 삭제.
     * '상세 데이터가 존재하지 않는 Cart 테이블은 존재하지 않는다' 라고 설계했기 때문에
     * Cart 데이터 자체를 삭제. Cascade 설정으로 인해 상세 데이터까지 같이 삭제처리된다.
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String deleteAllCart(CartMemberDTO cartMemberDTO, HttpServletResponse response) {

        Long cartId = cartRepository.findIdByUserId(cartMemberDTO);

        if (cartId == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        cartRepository.deleteById(cartId);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param cartMemberDTO
     * @param cartDetailId
     *
     * 장바구니 내 상품의 수량 증가.
     */
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

    /**
     *
     * @param cartMemberDTO
     * @param cartDetailId
     *
     * 장바구니 내 상품의 수량 감소
     */
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

    /**
     *
     * @param cartMemberDTO
     * @param deleteCartDetailId
     *
     * 장바구니 내 선택 상품의 삭제 처리.
     *
     * 버튼은 선택 상품 버튼이지만 전체 선택이 되어있는 경우를 감안해 저장된 데이터의 크기를 조회.
     * 동일하다면 Cart 데이터를 제거하고 그렇지 않다면 상세 데이터만 제거한다.
     */
    @Override
    public String deleteCartSelect(CartMemberDTO cartMemberDTO, List<Long> deleteCartDetailId) {

        Long cartId = cartRepository.findIdByUserId(cartMemberDTO);

        if(cartId == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        List<Long> detailIds = cartDetailRepository.findAllIdByCartId(cartId);

        for(Long detailId : deleteCartDetailId)
            if(!detailIds.contains(detailId))
                throw new IllegalArgumentException("Invalid CartDetailId");

        if(detailIds.size() == deleteCartDetailId.size())
            cartRepository.deleteById(cartId);
        else
            cartDetailRepository.deleteAllById(deleteCartDetailId);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param response
     *
     * 비회원의 장바구니 쿠키 생성
     */
    private String createAnonymousCookie(HttpServletResponse response) {
        String cookieValue = UUID.randomUUID().toString().replace("-", "");

        setCartCookie(cookieValue, response);

        return cookieValue;
    }

    /**
     *
     * @param cookieValue
     * @param response
     *
     * 장바구니 쿠키를 응답쿠키에 담는다.
     * 만료기간은 7일.
     */
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

        return new CartMemberDTO(uid, cartCookieValue);
    }
}

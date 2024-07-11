package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final ProductOrderRepository productOrderRepository;

    private final CartDetailRepository cartDetailRepository;

    private final CartRepository cartRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductRepository productRepository;

    /**
     *
     * @param paymentDTO
     * @param cartMemberDTO
     * @return
     *
     * 주문 내역 데이터 처리
     * , 장바구니를 통한 구매의 경우 장바구니 데이터 정리
     * , 기간별, 상품별 매출 정리
     * , 상품의 판매량 수정
     * , 상품 옵션별 재고 수정
     *
     *
     * 뭔가 너무 복잡하게 처리하고 있는 듯한 느낌인데 개선할 수 있는 방법이 있을지 고민해보기.
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
        ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid()); // 주문내역
        List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();// 주문 내역 중 상품 옵션 정보 리스트
        List<Long> orderOptionIdList = new ArrayList<>();// 주문한 상품 옵션 아이디를 담아줄 리스트
        int totalProductCount = 0;// 총 판매량

        //옵션 정보 리스트에서 각 객체를 OrderDetail Entity로 Entity화 해서 ProductOrder Entity에 담아준다.
        //주문한 옵션 번호는 추후 더 사용하기 때문에 리스트화 한다.
        //총 판매량은 기간별 매출에 필요하기 때문에 이때 같이 총 판매량을 계산한다.
        for(OrderProductDTO data : orderProductList) {
            productOrder.addDetail(data.toOrderDetailEntity());
            orderOptionIdList.add(data.optionId());
            totalProductCount += data.detailCount();
        }
        productOrder.setProductCount(totalProductCount);
        //주문 정보와 주문 상세정보를 같이 저장한다.
        productOrderRepository.save(productOrder);

        //주문 타입이 cart인 경우 장바구니에서 선택한 상품 또는 전체 상품 주문이므로 해당 상품을 장바구니에서 삭제해준다.
        if(paymentDTO.orderType().equals("cart")){
            //사용자의 장바구니 아이디를 가져와서 장바구니 상세 리스트를 가져온다.
            //장바구니 상세 리스트의 경우 리스트화 한 옵션 번호를 통해 가져올 수도 있으나 전체 리스트와 주문 리스트의 크기가 일치한다면
            //장바구니의 모든 상품을 구매한 것이기 때문에 장바구니 데이터 자체를 삭제하도록 하기 위함.
            Long cartId = cartRepository.findIdByUserId(cartMemberDTO);
            List<CartDetail> cartDetailList = cartDetailRepository.findAllCartDetailByCartId(cartId);

            if(cartDetailList.size() == orderOptionIdList.size())
                cartRepository.deleteById(cartId);
            else{
                List<Long> deleteCartDetailIdList = cartDetailList.stream()
                        .filter(cartDetail ->
                                orderOptionIdList.contains(
                                        cartDetail.getProductOption()
                                                .getId()
                                )
                        )
                        .map(CartDetail::getId)
                        .toList();

                cartDetailRepository.deleteAllById(deleteCartDetailIdList);
            }
        }

        //상품 옵션 재고 수정을 위해 주문 내역에 해당하는 상품 옵션 데이터를 조회
        //저장 또는 수정할 데이터를 담아줄 리스트를 새로 생성
        List<ProductOption> productOptionList = productOptionRepository.findAllById(orderOptionIdList);
        List<ProductOption> productOptionSetList = new ArrayList<>();

        //상품 테이블에 존재하는 판매량을 처리하기 위해 Map 구조로 '상품 아이디 : 해당 상품 총 주문량(옵션 별 총합)' 으로 처리한다.
        //조회해야 할 상품 아이디를 리스트화 하기 위해 리스트를 하나 생성한다.
        Map<String, Integer> productMap = new HashMap<>();
        List<String> productIdList = new ArrayList<>();


        for(int i = 0; i < orderProductList.size(); i++) {
            //주문 내역을 반복문으로 처리하면서 Map에 상품 아이디와 해당 상품 주문 총량을 처리한다.
            //주문내역에서는 상품 아이디가 겹치는 경우가 발생하기 때문에 리스트에 담겨있지 않은 경우에만 담도록 처리한다.
            OrderProductDTO dto = orderProductList.get(i);
            productMap.put(
                    dto.productId()
                    , productMap.getOrDefault(dto.productId(), 0) + dto.detailCount()
            );

            if(!productIdList.contains(dto.productId()))
                productIdList.add(dto.productId());

            //상품 옵션 테이블에서 재고 수정을 위해 해당 옵션 상품 리스트를 반복문으로 돌리면서
            //조회된 Entity의 재고를 수정한 뒤 리스트에 담아준다.
            //한번 수정이 발생할 때마다 다음 루프의 횟수를 줄이기 위해 리스트 데이터를 지워나간다.
            for(int j = 0; j < productOptionList.size(); j++) {
                if(dto.optionId() == productOptionList.get(j).getId()){
                    ProductOption productOption = productOptionList.get(j);

                    productOption.setStock(productOption.getStock() - dto.detailCount());
                    productOptionSetList.add(productOption);

                    productOptionList.remove(j);
                    break;
                }
            }
        }

        productOptionRepository.saveAll(productOptionSetList);

        //상품 판매량 수정을 위해 해당되는 상품들을 조회.
        List<Product> productList = productRepository.findAllByIdList(productIdList);
        List<Product> productSetList = new ArrayList<>();

        //해당 되는 상품 Entity에 대해 판매량을 수정한 뒤 리스트에 담아준다.
        for(Product data : productList) {
            long productSales = data.getProductSales() + productMap.get(data.getId());
            data.setProductSales(productSales);

            productSetList.add(data);
        }

        productRepository.saveAll(productSetList);


        return Result.OK.getResultKey();
    }
}

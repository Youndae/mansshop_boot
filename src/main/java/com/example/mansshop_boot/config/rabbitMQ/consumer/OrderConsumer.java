package com.example.mansshop_boot.config.rabbitMQ.consumer;

import com.example.mansshop_boot.domain.dto.order.business.*;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.repository.cart.CartDetailRepository;
import com.example.mansshop_boot.repository.cart.CartRepository;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OrderConsumer {

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    private final PeriodSalesSummaryRepository periodSalesSummaryRepository;

    private final ProductSalesSummaryRepository productSalesSummaryRepository;

    private final CartRepository cartRepository;

    private final CartDetailRepository cartDetailRepository;

    private final ProductOrderRepository productOrderRepository;

    public OrderConsumer(ProductRepository productRepository,
                         ProductOptionRepository productOptionRepository,
                         PeriodSalesSummaryRepository periodSalesSummaryRepository,
                         ProductSalesSummaryRepository productSalesSummaryRepository,
                         CartRepository cartRepository,
                         CartDetailRepository cartDetailRepository,
                         ProductOrderRepository productOrderRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.periodSalesSummaryRepository = periodSalesSummaryRepository;
        this.productSalesSummaryRepository = productSalesSummaryRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productOrderRepository = productOrderRepository;
    }

    /**
     *
     * @param messageDTO
     *
     * 상품 주문 처리 시 Product의 productSalesQuantity 데이터 수정.
     * 동일한 상품이더라도 옵션이 다른 경우 각 OrderProductDTO에 담겨 있기 때문에 Map을 통해 전체 집계 처리.
     * 수정 처리는 조회로 인해 발생하는 시간을 줄이기 위해 save() 처리를 지양.
     * Map 구조 그대로 productId, salesQuantity 구조 그대로 Repository로 보내고 update 쿼리를 통해 수정하도록 처리.
     *
     * 또한 동시성 제어를 위해 concurrency 1로 설정.
     */
    @RabbitListener(queues = "${rabbitmq.queue.orderProduct.name}", concurrency = "1")
    public void consumeOrderProduct(OrderProductMessageDTO messageDTO) {
        Map<String, Integer> productMap = new HashMap<>();

        for(OrderProductDTO dto : messageDTO.getOrderProductList()){
            productMap.put(
                    dto.getProductId(),
                    productMap.getOrDefault(dto.getProductId(), 0) + dto.getDetailCount()
            );
        }

        productRepository.patchProductSalesQuantity(productMap);
    }

    /**
     *
     * @param messageDTO
     *
     * 상품 옵션별 재고 수정.
     * OrderProductDTO 내부의 optionId, detailCount 필드를 통해 update 쿼리로 직접 수정.
     * 이 처리는 처리시간을 최대한 줄여야 상품 관련 UI에서 품절 여부를 빠르게 표시할 수 있기 때문에 save()가 아닌 update 쿼리로 처리.
     *
     * 동시성 제어를 위해 concurrency 1 로 설정.
     */
    @RabbitListener(queues = "${rabbitmq.queue.orderProductOption.name}", concurrency = "1")
    public void consumeOrderProductOption(OrderProductMessageDTO messageDTO) {

        productOptionRepository.patchOrderStock(messageDTO.getOrderProductList());
    }

    /**
     *
     * @param dto
     *
     * 기간별 매출 테이블 데이터 수정 또는 삽입.
     * 해당 주문에 대한 집계 처리는 서비스 메소드 내부에서 이미 주문 테이블을 통해 집계된 상태이므로 해당 데이터를 담은 DTO를 매개변수로 받아 처리.
     * 데이터 존재 유무에 따라 수정 또는 삽입 처리로 분리될 수 있어야 하기 때문에 오늘 날짜를 기준으로 조회.
     * 날짜 처리는 자정을 넘어가는 시점에 대해 Queue 대기를 감안해 서비스에서 요청 시점에 따라 날짜 필드를 처리하고 여기에서는 그 날짜 데이터에 따라 처리.
     *
     * 동시성 제어를 위해 concurrency 1 로 처리.
     */
    @RabbitListener(queues = "${rabbitmq.queue.periodSalesSummary.name}", concurrency = "1")
    public void consumePeriodSalesSummary(PeriodSummaryQueueDTO dto) {

        PeriodSalesSummary entity = periodSalesSummaryRepository.findByPeriod(dto.getPeriod());

        if(entity != null)
            entity.setPatchData(dto);
        else
            entity = dto.toEntity();

         periodSalesSummaryRepository.save(entity);
    }

    /**
     *
     * @param productSummaryDTO
     *
     * 상품 매출 테이블 수정 처리.
     * 상품 매출 데이터의 경우 해당 날짜에서는 상품 옵션이 Unique해야 하기 때문에 조회 후 수정 또는 삽입 처리로 수행.
     * 날짜 필드의 경우 Queue 대기 시간을 고려해서 서비스 로직에서 미리 생성해서 전달.
     *
     * 처리 분기
     * 1. 조회 결과와 요청 데이터의 크기가 동일하다면 해당 날짜의 모든 상품 매출 데이터가 존재하므로 조회된 엔티티 리스트를 수정.
     * 2. 조회 결과가 0으로 나온다면 모든 데이터가 존재하지 않는 것으로 요청 데이터를 통해 모든 엔티티를 생성.
     * 3. 조회 결과가 0은 아니지만 요청 데이터의 크기와 다르다면 기존 데이터를 수정하고 남은 데이터를 통해 엔티티 생성 후 리스트에 추가.
     *
     * 동시성 제어를 위해 concurrency 1 로 처리.
     */
    @RabbitListener(queues = "${rabbitmq.queue.productSalesSummary.name}", concurrency = "1")
    public void consumeProductSalesSummary(OrderProductSummaryDTO productSummaryDTO) {
        List<ProductSalesSummary> summaryEntities = productSalesSummaryRepository.findAllByProductOptionIds(productSummaryDTO.getPeriodMonth(), productSummaryDTO.getProductOptionIds());

        if(summaryEntities.size() == productSummaryDTO.getOrderProductDTOList().size()) {
            //둘의 사이즈가 같다는 것은 해당 상품에 대한 데이터가 이미 들어가 있다는 말이 되므로 기존 엔티티 수정만 처리.
            patchProductSalesSummaryList(summaryEntities, productSummaryDTO.getOrderProductDTOList());
        }else if (summaryEntities.size() == 0){
            createProductSummaryList(summaryEntities, productSummaryDTO.getOrderProductDTOList(), productSummaryDTO.getProductIds(), productSummaryDTO.getPeriodMonth());
        }else {
            //기존 엔티티 수정 처리 후 새로운 데이터에 대한 삽입을 처리하기 위해 productIds를 만든다.
            // 이때, 엔티티에 없는 정보만 조회해야 한다.
            patchProductSalesSummaryList(summaryEntities, productSummaryDTO.getOrderProductDTOList());

            List<String> productIds = new ArrayList<>();
            List<OrderProductDTO> remainDTO = new ArrayList<>();

            // 기존 데이터가 존재하지 않더라도 해당 옵션에 대해 ProductId, ClassificationId가 동일한 상품이라면 DTO에서 데이터를 찾아 파싱할 필요가 없어지므로
            // 해당 처리에 대한 체크를 수행.
            for(OrderProductDTO dto : productSummaryDTO.getOrderProductDTOList()) {
                ProductSalesSummary newEntity = null;
                for(ProductSalesSummary entity : summaryEntities) {

                    if(dto.getOptionId() != entity.getProductOption().getId() &&
                                dto.getProductId().equals(entity.getProduct().getId())){
                        // 옵션 아이디는 불일치하지만 상품 아이디는 일치한다면
                        // 이 Product, Classification을 그대로 담아서 사용할 수 있다.

                        newEntity = ProductSalesSummary.builder()
                                                    .periodMonth(productSummaryDTO.getPeriodMonth())
                                                    .classification(entity.getClassification())
                                                    .product(entity.getProduct())
                                                    .productOption(ProductOption.builder().id(dto.getOptionId()).build())
                                                    .sales(dto.getDetailPrice())
                                                    .salesQuantity(dto.getDetailCount())
                                                    .orderQuantity(1)
                                                    .build();

                        summaryEntities.add(newEntity);
                        break;
                    }
                }
                // newEntity가 그대로 null이라면 일치하는 상품이 없었다는 것이므로 상품 아이디와 OrderProductDTO를 리스트에 추가.
                if(newEntity == null) {
                    productIds.add(dto.getProductId());
                    remainDTO.add(dto);
                }
            }
            // 기존 데이터와 비교 후 일치하는 데이터가 없는 요청 데이터에 대해 엔티티를 생성하고 리스트에 추가하기 위해 호출.
            if(!productIds.isEmpty())
                createProductSummaryList(summaryEntities, remainDTO, productIds, productSummaryDTO.getPeriodMonth());
        }

        productSalesSummaryRepository.saveAll(summaryEntities);
    }

    /**
     *
     * @param list
     * @param requestDTO
     *
     * 상품 매출 테이블의 기존 데이터 수정 처리 메소드.
     * 수정이 처리된 요청 데이터는 더이상 필요하지 않기도 하고 처리되지 않은 데이터를 상위 메소드에서 확인하도록 하기 위해 remove() 처리.
     */
    private void patchProductSalesSummaryList(List<ProductSalesSummary> list,
                                               List<OrderProductDTO> requestDTO) {
        for(ProductSalesSummary entity : list) {

            for(OrderProductDTO dto : requestDTO) {
                if(dto.getOptionId() == entity.getProductOption().getId()){
                    entity.setPatchSalesData(dto);
                    requestDTO.remove(dto);
                    break;
                }
            }
        }
    }

    /**
     *
     * @param list
     * @param requestDTO
     * @param productIds
     * @param periodMonth
     *
     * 상품 매출 테이블에 존재하지 않는 데이터들에 대해 엔티티를 생성하고 리스트에 추가.
     */
    private void createProductSummaryList(List<ProductSalesSummary> list,
                                           List<OrderProductDTO> requestDTO,
                                           List<String> productIds,
                                           LocalDate periodMonth) {
        List<ProductIdClassificationDTO> searchDTO = productRepository.findClassificationAllByProductIds(productIds);

        for(OrderProductDTO dto : requestDTO) {
            String productId = dto.getProductId();

            for(ProductIdClassificationDTO searchData : searchDTO) {
                if(productId.equals(searchData.productId())) {
                    list.add(
                            ProductSalesSummary.builder()
                                    .periodMonth(periodMonth)
                                    .classification(Classification.builder().id(searchData.classificationId()).build())
                                    .product(Product.builder().id(searchData.productId()).build())
                                    .productOption(ProductOption.builder().id(dto.getOptionId()).build())
                                    .sales(dto.getDetailPrice())
                                    .salesQuantity(dto.getDetailCount())
                                    .orderQuantity(1L)
                                    .build()
                    );
                }
            }
        }
    }

    /**
     *
     * @param orderCartDTO
     *
     * 주문 데이터에 해당하는 장바구니 데이터 삭제 처리.
     * 장바구니를 우선 조회하고 해당 데이터 사이즈와 주문 데이터 사이즈가 동일하다면 전체 주문으로 판단하고 장바구니 자체를 삭제.
     * 크기가 다르다면 일치하는 데이터만 찾아서 제거한다.
     *
     * cartId 조회 시 null이 반환된다면 NullPointerException 발생으로 인해 재시도 및 DLQ로 이동하지 않는 문제가 발생하기 때문에
     * null인 경우 로그를 남기도록 처리. 그로 인해 NullPointerException으로 인한 재시도나 큐에 메시지가 잔류하는 것을 막음.
     *
     * 장바구니 데이터 삭제가 목적이기 때문에 null이더라도 처리에 대한 문제가 발생하지는 않을 것으로 생각.
     * 대신 "cart" 로 인해 장바구니를 통한 주문 요청이었는데 Null이 된다는 것은 잘못된 요청이기 떄문에 이로 인한 검증이나 대응책을 고려해서
     * 서비스 메소드 상에서 추가적인 처리가 필요할 것 같음.
     */
    @RabbitListener(queues = "${rabbitmq.queue.orderCart.name}", concurrency = "3")
    public void consumeOrderCart(OrderCartDTO orderCartDTO) {

        Long cartId = cartRepository.findIdByUserId(orderCartDTO.getCartMemberDTO());

        if(cartId != null){
            List<CartDetail> cartDetailList = cartDetailRepository.findAllCartDetailByCartId(cartId);

            if(cartDetailList.size() == orderCartDTO.getProductOptionIds().size())
                cartRepository.deleteById(cartId);
            else {
                List<Long> deleteCartDetailIds = cartDetailList.stream()
                        .filter(cartDetail ->
                                orderCartDTO.getProductOptionIds().contains(
                                        cartDetail.getProductOption().getId()
                                )
                        )
                        .map(CartDetail::getId)
                        .toList();

                cartDetailRepository.deleteAllById(deleteCartDetailIds);
            }
        }else
            log.error("OrderConsumer::consumeOrderCart : cartId is null. cartMemberDTO is {}", orderCartDTO.getCartMemberDTO());
    }

    @RabbitListener(queues = "${rabbitmq.queue.failedOrder.name}", concurrency = "3")
    public void consumeFailedOrderData(ProductOrderDataDTO productOrderDataDTO) {
        ProductOrder order = productOrderDataDTO.productOrder();
        productOrderRepository.save(order);
    }
}

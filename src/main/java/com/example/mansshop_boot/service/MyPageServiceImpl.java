package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageServiceImpl implements MyPageService{

    private final ProductOrderDetailRepository productOrderDetailRepository;

    private final ProductOrderRepository productOrderRepository;

    private final PrincipalService principalService;

    private final ProductLikeRepository productLikeRepository;

    private final ProductQnARepository productQnARepository;

    private final ProductQnAReplyRepository productQnAReplyRepository;

    private final MemberQnARepository memberQnARepository;

    private final MemberQnAReplyRepository memberQnAReplyRepository;

    @Override
    public ResponseEntity<?> getOrderList(OrderPageDTO pageDTO, MemberOrderDTO memberOrderDTO) {

        /*
            data

            pageable,
            content : [
                        {
                            orderId
                            orderTotalPrice
                            orderCreatedAt
                            orderStat
                            detail : [
                                        {
                                            orderId
                                            detailId
                                            productName
                                            size
                                            color
                                            detailCount
                                            detailPrice
                                            reviewStatus
                                            thumbnail
                                        }
                            ]
                        }
            ]
         */

        /*
            둘다 orderId 기준 역정렬 해서 가져올 것.
         */

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.orderAmount()
                                            , Sort.by("orderId").descending());

        Page<ProductOrder> order = productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable);
        List<Long> orderIdList = new ArrayList<>();
        order.getContent().forEach(val -> orderIdList.add(val.getId()));

        List<MyPageOrderDetailDTO> detailDTOList = productOrderDetailRepository.findByDetailList(orderIdList);
        List<MyPageOrderDTO> contentList = new ArrayList<>();
        List<MyPageOrderDetailDTO> orderDetailList = new ArrayList<>();
        for(ProductOrder data : order.getContent()){
            long orderId = data.getId();

            for(int i = 0; i < detailDTOList.size(); i++) {
                if(orderId == detailDTOList.get(i).orderId())
                    orderDetailList.add(detailDTOList.get(i));
            }

            contentList.add(
                    MyPageOrderDTO.builder()
                            .orderId(orderId)
                            .orderTotalPrice(data.getOrderTotalPrice())
                            .orderDate(data.getCreatedAt())
                            .orderStat(data.getOrderStat())
                            .detail(orderDetailList)
                            .build()
            );

            orderDetailList = new ArrayList<>();
        }

        String nickname = principalService.getUidByUserId(memberOrderDTO.userId());

        PagingResponseDTO<MyPageOrderDTO> responseDTO = new PagingResponseDTO<>(contentList, order.isEmpty(), order.getNumber(), order.getTotalPages(), nickname);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


    /*
        필요 데이터

        likeId
        productName
        productPrice
        thumbnail
        stock
        productId
        createdAt
     */
    @Override
    public ResponseEntity<?> getLikeList(LikePageDTO pageDTO, Principal principal) {
        String userId = null;

        try{
            userId = principal.getName();
        }catch (Exception e) {
            log.info("MyPageService.getLikeList :: principal Error");
            e.printStackTrace();

            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        }

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.likeAmount()
                                            , Sort.by("createdAt").descending());

        Page<ProductLikeDTO> dto = productLikeRepository.findByUserId(pageDTO, userId, pageable);

        String nickname = principalService.getPrincipalUid(principal);

        PagingResponseDTO<ProductLikeDTO> responseDTO = new PagingResponseDTO<>(dto, nickname);


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        content : [
            {
                productQnAId
                productName
                productQnAStat
                createdAt
            },
            { }...
        ],
        totalPages
     */
    @Override
    public ResponseEntity<?> getProductQnAList(MyPagePageDTO pageDTO, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        Page<ProductQnAListDTO> dto = productQnARepository.findByUserId(userId, pageable);

        String nickname = principalService.getPrincipalUid(principal);

        PagingResponseDTO<ProductQnAListDTO> responseDTO = new PagingResponseDTO<>(dto, nickname);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        productQnAId
        , productName
        , writer
        , qnaContent
        , createdAt
        , productQnAStat
        , reply : [
                    {
                        writer
                        , replyContent
                        , updatedAt
                    }
                ]
     */
    @Override
    public ResponseEntity<?> getProductQnADetail(long productQnAId, Principal principal){
        String userId = principalService.getUserIdByPrincipal(principal);

        MyPageProductQnADTO qnaDTO = productQnARepository.findByIdAndUserId(productQnAId, userId);
        if(qnaDTO == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        List<MyPageProductQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(productQnAId);

        String nickname = principalService.getPrincipalUid(principal);

        ProductQnADetailDTO responseDTO = new ProductQnADetailDTO(qnaDTO, replyDTOList, nickname);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        memberQnAId
        , writer
        , memberQnAStat
        , qnaClassificationName
        , updatedAt
     */
    @Override
    public ResponseEntity<?> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("createdAt").descending());

        Page<MemberQnAListDTO> dto = memberQnARepository.findAllByUserId(userId, pageable);

        String nickname = principalService.getPrincipalUid(principal);

        PagingResponseDTO<MemberQnAListDTO> responseDTO = new PagingResponseDTO<>(dto, nickname);


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /*
        memberQnAId
        , qnaClassificationName
        , writer
        , qnaContent
        , updatedAt
        , memberQnAStat
        , reply : [
                    {
                        writer
                        , replyContent
                        , updatedAt
                    }
                ]
     */
    @Override
    public ResponseEntity<?> getMemberQnADetail(long memberQnAId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        MemberQnADTO qnaDTO = memberQnARepository.findByIdAndUserId(memberQnAId, userId);
        List<MemberQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(memberQnAId);

        String nickname = principalService.getPrincipalUid(principal);
        MemberQnADetailDTO responseDTO = new MemberQnADetailDTO(qnaDTO, replyDTOList, nickname);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }
}

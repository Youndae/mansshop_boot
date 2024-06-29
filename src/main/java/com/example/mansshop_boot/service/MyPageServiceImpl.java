package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.mypage.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.MailSuffix;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final QnAClassificationRepository qnAClassificationRepository;

    private final MemberRepository memberRepository;

    private final ProductReviewRepository productReviewRepository;

    private final ProductRepository productRepository;

    @Override
    public PagingResponseDTO<MyPageOrderDTO> getOrderList(OrderPageDTO pageDTO, MemberOrderDTO memberOrderDTO) {

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

        String nickname = principalService.getNicknameByUserId(memberOrderDTO.userId());

        return new PagingResponseDTO<>(
                contentList
                , order.isEmpty()
                , order.getNumber()
                , order.getTotalPages()
                , nickname
        );
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
    public PagingResponseDTO<ProductLikeDTO> getLikeList(LikePageDTO pageDTO, Principal principal) {
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

        String nickname = principalService.getNicknameByPrincipal(principal);

        return new PagingResponseDTO<>(dto, nickname);
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
    public PagingResponseDTO<ProductQnAListDTO> getProductQnAList(MyPagePageDTO pageDTO, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        Page<ProductQnAListDTO> dto = productQnARepository.findByUserId(userId, pageable);

        String nickname = principalService.getNicknameByPrincipal(principal);

        return new PagingResponseDTO<>(dto, nickname);
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
    public ProductQnADetailDTO getProductQnADetail(long productQnAId, Principal principal){
        String userId = principalService.getUserIdByPrincipal(principal);
        String nickname = principalService.getNicknameByPrincipal(principal);

        ProductQnA productQnA = productQnARepository.findById(productQnAId).orElseThrow(IllegalArgumentException::new);

        if(!productQnA.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        String writer = productQnA.getMember().getNickname() == null ?
                                productQnA.getMember().getUserName() : productQnA.getMember().getNickname();

        MyPageProductQnADTO qnaDTO = new MyPageProductQnADTO(productQnA, writer);

        List<MyPageQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(productQnAId);

        return new ProductQnADetailDTO(qnaDTO, replyDTOList, nickname);
    }

    @Override
    public String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        ProductQnA productQnA = productQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);

        ProductQnAReply productQnAReply = ProductQnAReply.builder()
                .member(member)
                .productQnA(productQnA)
                .replyContent(insertDTO.content())
                .build();

        productQnAReplyRepository.save(productQnAReply);

        return Result.OK.getResultKey();
    }

    @Override
    public String patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal) {

        ProductQnAReply qnaReplyEntity = productQnAReplyRepository.findById(replyDTO.replyId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!qnaReplyEntity.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        qnaReplyEntity.setReplyContent(replyDTO.content());

        productQnAReplyRepository.save(qnaReplyEntity);

        return Result.OK.getResultKey();
    }

    @Override
    public String deleteProductQnA(long qnaId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        ProductQnA productQnA = productQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        if(!productQnA.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        productQnARepository.deleteById(qnaId);

        return Result.OK.getResultKey();
    }

    /*
        memberQnAId
        , writer
        , memberQnAStat
        , qnaClassificationName
        , updatedAt
     */
    @Override
    public PagingResponseDTO<MemberQnAListDTO> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        Page<MemberQnAListDTO> dto = memberQnARepository.findAllByUserId(userId, pageable);

        String nickname = principalService.getNicknameByPrincipal(principal);

        return new PagingResponseDTO<>(dto, nickname);
    }

    @Override
    public ResponseIdDTO postMemberQnA(MemberQnAInsertDTO insertDTO, Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        QnAClassification qnAClassification = qnAClassificationRepository.findById(insertDTO.classificationId()).orElseThrow(IllegalArgumentException::new);

        MemberQnA memberQnA = MemberQnA.builder()
                .member(member)
                .qnAClassification(qnAClassification)
                .memberQnATitle(insertDTO.title())
                .memberQnAContent(insertDTO.content())
                .build();

        long id = memberQnARepository.save(memberQnA).getId();

        return new ResponseIdDTO(id);
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
    public MemberQnADetailDTO getMemberQnADetail(long memberQnAId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);
        String nickname = principalService.getNicknameByPrincipal(principal);

        MemberQnA memberQnA = memberQnARepository.findById(memberQnAId).orElseThrow(IllegalArgumentException::new);

        if(!memberQnA.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        String writer = memberQnA.getMember().getNickname() == null ?
                memberQnA.getMember().getUserName() : memberQnA.getMember().getNickname();

        MemberQnADTO qnaDTO = new MemberQnADTO(memberQnA, writer);

        List<MyPageQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(memberQnAId);



        return new MemberQnADetailDTO(qnaDTO, replyDTOList, nickname);
    }

    @Override
    public String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        MemberQnA memberQnA = memberQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);

        MemberQnAReply memberQnAReply = MemberQnAReply.builder()
                .member(member)
                .memberQnA(memberQnA)
                .replyContent(insertDTO.content())
                .build();

        memberQnAReplyRepository.save(memberQnAReply);

        return Result.OK.getResultKey();
    }

    @Override
    public String patchMemberQnAReply(QnAReplyDTO replyDTO, Principal principal) {

        MemberQnAReply memberQnAReplyEntity = memberQnAReplyRepository.findById(replyDTO.replyId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!memberQnAReplyEntity.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        memberQnAReplyEntity.setReplyContent(replyDTO.content());

        memberQnAReplyRepository.save(memberQnAReplyEntity);

        return Result.OK.getResultKey();
    }

    @Override
    public ResponseDTO<MemberQnAModifyDataDTO> getModifyData(long qnaId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        MemberQnA memberQnA = memberQnARepository.findModifyDataByIdAndUserId(qnaId, userId);
        List<QnAClassification> qnaClassification = qnAClassificationRepository.findAll();
        List<QnAClassificationDTO> classificationDTO = new ArrayList<>();
        qnaClassification.forEach(v ->
                classificationDTO.add(
                        QnAClassificationDTO.builder()
                                .id(v.getId())
                                .name(v.getQnaClassificationName())
                                .build()
                )
        );

        MemberQnAModifyDataDTO modifyDataDTO = new MemberQnAModifyDataDTO(memberQnA, classificationDTO);
        String nickname = principalService.getNicknameByPrincipal(principal);

        return new ResponseDTO<>(modifyDataDTO, new UserStatusDTO(nickname));
    }

    @Override
    public String patchMemberQnA(MemberQnAModifyDTO modifyDTO, Principal principal) {

        MemberQnA memberQnA = memberQnARepository.findById(modifyDTO.qnaId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!memberQnA.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        QnAClassification qnAClassification = qnAClassificationRepository.findById(modifyDTO.classificationId()).orElseThrow(IllegalArgumentException::new);

        memberQnA.setModifyData(modifyDTO, qnAClassification);

        memberQnARepository.save(memberQnA);

        return Result.OK.getResultKey();
    }

    @Override
    public String deleteMemberQnA(long qnaId, Principal principal) {
        String userId = principalService.getUserIdByPrincipal(principal);

        MemberQnA memberQnA = memberQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        if(!memberQnA.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        memberQnARepository.deleteById(qnaId);

        return Result.OK.getResultKey();
    }

    @Override
    public QnAClassificationResponseDTO getQnAClassification(Principal principal) {

        List<QnAClassification> classificationList = qnAClassificationRepository.findAll();
        List<QnAClassificationDTO> dtoList = new ArrayList<>();
        classificationList.forEach(v ->
                dtoList.add(
                        QnAClassificationDTO.builder()
                                .id(v.getId())
                                .name(v.getQnaClassificationName())
                                .build()
                )
        );

        return QnAClassificationResponseDTO.builder()
                .classificationList(dtoList)
                .userStatus(
                        new UserStatusDTO(principalService.getNicknameByPrincipal(principal))
                )
                .build();
    }

    @Override
    public PagingResponseDTO<MyPageReviewDTO> getReview(MyPagePageDTO pageDTO, Principal principal) {

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        String userId = principalService.getUserIdByPrincipal(principal);

        Page<MyPageReviewDTO> dto = productReviewRepository.findAllByUserId(userId, pageable);

        String nickname = principalService.getNicknameByPrincipal(principal);

        return new PagingResponseDTO<>(dto, nickname);
    }

    @Override
    public ResponseDTO<MyPagePatchReviewDataDTO> getPatchReview(long reviewId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        ProductReview productReview = productReviewRepository.findById(reviewId).orElseThrow(IllegalArgumentException::new);

        if(!productReview.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        MyPagePatchReviewDataDTO dto = MyPagePatchReviewDataDTO.builder()
                                            .reviewId(productReview.getId())
                                            .content(productReview.getReviewContent())
                                            .productName(productReview.getProduct().getProductName())
                                            .build();

        String nickname = principalService.getNicknameByPrincipal(principal);

        return new ResponseDTO<>(dto, new UserStatusDTO(nickname));
    }

    @Override
    public String postReview(MyPagePostReviewDTO reviewDTO, Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        Product product = productRepository.findById(reviewDTO.productId()).orElseThrow(IllegalArgumentException::new);

        ProductReview productReview = ProductReview.builder()
                .member(member)
                .product(product)
                .reviewContent(reviewDTO.content())
                .build();

        productReviewRepository.save(productReview);


        return Result.OK.getResultKey();
    }

    @Override
    public String patchReview(MyPagePatchReviewDTO reviewDTO, Principal principal) {

        ProductReview productReview = productReviewRepository.findById(reviewDTO.reviewId()).orElseThrow(IllegalArgumentException::new);

        String userId = principalService.getUserIdByPrincipal(principal);

        if(!productReview.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        productReview.setReviewContent(reviewDTO.content());

        productReviewRepository.save(productReview);


        return Result.OK.getResultKey();
    }

    @Override
    public String deleteReview(long reviewId, Principal principal) {

        ProductReview productReview = productReviewRepository.findById(reviewId).orElseThrow(IllegalArgumentException::new);

        String userId = principalService.getUserIdByPrincipal(principal);

        if(!productReview.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        productReviewRepository.deleteById(reviewId);

        return Result.OK.getResultKey();
    }

    /*
        suffix를 enum 타입과 비교해서 처리하는 방법으로 구분하고 해당하는 값을 type으로 반환.
        직접 입력의 경우 어떻게 처리할 지 고민 필요.
     */
    @Override
    public ResponseDTO<MyPageInfoDTO> getInfo(Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);

        String[] splitMail = member.getUserEmail().split("@");

        String mailSuffix = splitMail[1].substring(0, splitMail[1].indexOf('.'));
        String type = MailSuffix.findSuffixType(mailSuffix);

        MyPageInfoDTO infoDTO = MyPageInfoDTO.builder()
                .nickname(member.getNickname())
                .phone(member.getPhone().replaceAll("-", ""))
                .mailPrefix(splitMail[0])
                .mailSuffix(splitMail[1])
                .mailType(type)
                .build();

        return new ResponseDTO<>(infoDTO, new UserStatusDTO(member.getNickname()));
    }

    @Override
    public String patchInfo(MyPageInfoPatchDTO infoDTO, Principal principal) {

        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);

        System.out.println("MyPageService.patchInfo :: infoDTO : " + infoDTO);

        member.patchUser(infoDTO);

        memberRepository.save(member);


        return Result.OK.getResultKey();
    }
}

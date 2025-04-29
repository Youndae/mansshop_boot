package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MyPagePageDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPageInfoPatchDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePatchReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.in.MyPagePostReviewDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.*;
import com.example.mansshop_boot.domain.dto.pageable.LikePageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.MailSuffix;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ProductOptionRepository productOptionRepository;

    /**
     *
     * @param pageDTO
     * @param memberOrderDTO
     *
     * 주문 목록 조회.
     * OrderPageDTO에서는 term을 같이 받는데 3, 6, 12, all 네가지로 받는다.
     * 각 개월수를 의미.
     * 해당 개월수에 맞는 데이터 리스트를 조회.
     *
     * 주문 테이블과 주문 상세 테이블은 데이터가 빠르게 쌓이기 때문에 페이징을 직접 처리.
     */
    @Override
    public PagingListDTO<MyPageOrderDTO> getOrderList(OrderPageDTO pageDTO, MemberOrderDTO memberOrderDTO) {
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.orderAmount()
                                            , Sort.by("orderId").descending());

        Page<ProductOrder> order = productOrderRepository.findByUserId(memberOrderDTO, pageDTO, pageable);
        List<Long> orderIdList = order.getContent().stream().map(ProductOrder::getId).toList();
        List<MyPageOrderDetailDTO> detailDTOList = productOrderDetailRepository.findByDetailList(orderIdList);
        List<MyPageOrderDTO> contentList = new ArrayList<>();

        for(ProductOrder data : order.getContent()){
            List<MyPageOrderDetailDTO> orderDetailList = detailDTOList.stream()
                                                        .filter(dto -> data.getId() == dto.orderId())
                                                        .toList();

            contentList.add(new MyPageOrderDTO(data, orderDetailList));
        }

        PagingMappingDTO pagingMappingDTO = PagingMappingDTO.builder()
                                                    .empty(order.isEmpty())
                                                    .number(order.getNumber())
                                                    .totalPages(order.getTotalPages())
                                                    .totalElements(order.getTotalElements())
                                                    .build();

        return new PagingListDTO<>(contentList, pagingMappingDTO);
    }


    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 관심목록 조회
     */
    @Override
    public Page<ProductLikeDTO> getLikeList(LikePageDTO pageDTO, Principal principal) {
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

        return productLikeRepository.findByUserId(userId, pageable);
    }

    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 작성한 상품 문의 목록 조회
     *
     */
    @Override
    public Page<ProductQnAListDTO> getProductQnAList(MyPagePageDTO pageDTO, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        return productQnARepository.findByUserId(userId, pageable);
    }

    /**
     *
     * @param productQnAId
     * @param principal
     *
     * 상품 목록 상세 정보 조회.
     * 작성자와 요청자가 다른 경우 AccessDeniedException을 반환
     */
    @Override
    public ProductQnADetailDTO getProductQnADetail(long productQnAId, Principal principal){
        String uid = principalService.getNicknameByPrincipal(principal);
        ProductQnADetailDTO dto = getProductQnADetailData(productQnAId);

        if(!dto.writer().equals(uid))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        return dto;
    }

    /**
     *
     * @param qnaId
     *
     * 상품 문의와 답변 조회.
     * Admin에서도 해당 메소드를 호출.
     */
    @Override
    public ProductQnADetailDTO getProductQnADetailData(long qnaId) {
        MyPageProductQnADTO qnaDTO = productQnARepository.findByQnAId(qnaId);

        if(qnaDTO == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        List<MyPageQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(qnaId);

        return new ProductQnADetailDTO(qnaDTO, replyDTOList);
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 상품 문의 삭제 처리
     */
    @Override
    public String deleteProductQnA(long qnaId, Principal principal) {
        String userId = principalService.getUserIdByPrincipal(principal);
        ProductQnA productQnA = productQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        if(!productQnA.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        productQnARepository.deleteById(qnaId);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 회원 문의 목록 조회
     */
    @Override
    public Page<MemberQnAListDTO> getMemberQnAList(MyPagePageDTO pageDTO, Principal principal) {
        String userId = principalService.getUserIdByPrincipal(principal);
        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        return memberQnARepository.findAllByUserId(userId, pageable);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 회원 문의 작성
     */
    @Override
    public Long postMemberQnA(MemberQnAInsertDTO insertDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        QnAClassification qnAClassification = qnAClassificationRepository.findById(insertDTO.classificationId()).orElseThrow(IllegalArgumentException::new);

        MemberQnA memberQnA = MemberQnA.builder()
                .member(member)
                .qnAClassification(qnAClassification)
                .memberQnATitle(insertDTO.title())
                .memberQnAContent(insertDTO.content())
                .build();

        return memberQnARepository.save(memberQnA).getId();
    }

    /**
     *
     * @param memberQnAId
     * @param principal
     *
     * 회원문의 상세 데이터 조회
     * 작성자와 요청자가 일치하지 않으면 AccessDeniedException 응답
     */
    @Override
    public MemberQnADetailDTO getMemberQnADetail(long memberQnAId, Principal principal) {
        String nickname = principalService.getNicknameByPrincipal(principal);
        MemberQnADetailDTO dto = getMemberQnADetailData(memberQnAId);

        if(!dto.writer().equals(nickname))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        return dto;
    }

    /**
     *
     * @param qnaId
     *
     * 회원문의 상세 데이터와 답변 리스트 반환
     * 상품문의와 마찬가지로 Admin에서도 이 메소드 호출.
     */
    @Override
    public MemberQnADetailDTO getMemberQnADetailData(long qnaId) {
        MemberQnADTO qnaDTO = memberQnARepository.findByQnAId(qnaId);

        if(qnaDTO == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        List<MyPageQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(qnaId);

        return new MemberQnADetailDTO(qnaDTO, replyDTOList);
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 회원 문의 답변 작성.
     * 관리자가 답변을 달아서 답변 완료 처리가 되었더라도 사용자가 재 답변을 작성하는 경우
     * 미답변으로 수정한다.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        MemberQnA memberQnA = memberQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);
        memberQnA.setMemberQnAStat(false);
        memberQnARepository.save(memberQnA);

        MemberQnAReply memberQnAReply = MemberQnAReply.builder()
                .member(member)
                .memberQnA(memberQnA)
                .replyContent(insertDTO.content())
                .build();

        memberQnAReplyRepository.save(memberQnAReply);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 회원 문의 답변 수정
     */
    @Override
    public String patchMemberQnAReply(QnAReplyDTO replyDTO, Principal principal) {
        MemberQnAReply memberQnAReplyEntity = memberQnAReplyRepository.findById(replyDTO.replyId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!memberQnAReplyEntity.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        memberQnAReplyEntity.setReplyContent(replyDTO.content());

        memberQnAReplyRepository.save(memberQnAReplyEntity);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 회원 문의 수정을 위한 상세 데이터 조회
     *
     */
    @Override
    public MemberQnAModifyDataDTO getModifyData(long qnaId, Principal principal) {

        String userId = principalService.getUserIdByPrincipal(principal);
        MemberQnA memberQnA = memberQnARepository.findModifyDataByIdAndUserId(qnaId, userId);

        if(memberQnA == null)
            throw new IllegalArgumentException("MemberQnA Modify Data is not Found");

        List<QnAClassificationDTO> classificationDTO = qnAClassificationRepository.getAllQnAClassificationDTOs();

        return new MemberQnAModifyDataDTO(memberQnA, classificationDTO);
    }

    /**
     *
     * @param modifyDTO
     * @param principal
     *
     * 회원 문의 수정.
     * 수정 처리 이전 해당 문의를 조회. 작성자와 요청자가 다르면 AccessDeniedException 응답
     *
     */
    @Override
    public String patchMemberQnA(MemberQnAModifyDTO modifyDTO, Principal principal) {
        MemberQnA memberQnA = memberQnARepository.findById(modifyDTO.qnaId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!memberQnA.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        QnAClassification qnAClassification = qnAClassificationRepository.findById(modifyDTO.classificationId()).orElseThrow(IllegalArgumentException::new);
        memberQnA.setModifyData(modifyDTO, qnAClassification);
        memberQnARepository.save(memberQnA);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param qnaId
     * @param principal
     *
     * 회원 문의 삭제 처리.
     * 작성자와 요청자가 일치해야 삭제 처리.
     */
    @Override
    public String deleteMemberQnA(long qnaId, Principal principal) {
        String userId = principalService.getUserIdByPrincipal(principal);
        MemberQnA memberQnA = memberQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        if(!memberQnA.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        memberQnARepository.deleteById(qnaId);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param principal
     *
     * 회원 문의 분류 목록 조회
     */
    @Override
    public List<QnAClassificationDTO> getQnAClassification(Principal principal) {
        return qnAClassificationRepository.getAllQnAClassificationDTOs();
    }

    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 작성한 리뷰 목록 조회
     */
    @Override
    public Page<MyPageReviewDTO> getReview(MyPagePageDTO pageDTO, Principal principal) {

        Pageable pageable = PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.amount()
                                            , Sort.by("id").descending());

        String userId = principalService.getUserIdByPrincipal(principal);

        return productReviewRepository.findAllByUserId(userId, pageable);
    }

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 리뷰 수정을 위한 리뷰 상세 데이터 조회.
     * 작성자와 요청자가 일치하지 않으면 AccessDeniedException 응답
     */
    @Override
    public MyPagePatchReviewDataDTO getPatchReview(long reviewId, Principal principal) {
        String userId = principalService.getUserIdByPrincipal(principal);
        ProductReview productReview = productReviewRepository.findById(reviewId).orElseThrow(IllegalArgumentException::new);

        if(!productReview.getMember().getUserId().equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        return MyPagePatchReviewDataDTO.builder()
                                        .reviewId(productReview.getId())
                                        .content(productReview.getReviewContent())
                                        .productName(productReview.getProduct().getProductName())
                                        .build();
    }

    /**
     *
     * @param reviewDTO
     * @param principal
     *
     * 리뷰 작성 처리.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String postReview(MyPagePostReviewDTO reviewDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        Product product = productRepository.findById(reviewDTO.productId()).orElseThrow(IllegalArgumentException::new);
        ProductOption productOption = productOptionRepository.findById(reviewDTO.optionId()).orElseThrow(IllegalArgumentException::new);
        ProductOrderDetail productOrderDetail = productOrderDetailRepository.findById(reviewDTO.detailId()).orElseThrow(IllegalArgumentException::new);
        ProductReview productReview = ProductReview.builder()
                                                    .member(member)
                                                    .product(product)
                                                    .reviewContent(reviewDTO.content())
                                                    .productOption(productOption)
                                                    .build();

        productReviewRepository.save(productReview);
        productOrderDetail.setOrderReviewStatus(true);
        productOrderDetailRepository.save(productOrderDetail);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param reviewDTO
     * @param principal
     *
     * 리뷰 수정 처리.
     * 작성자와 요청자가 일치하지 않으면 AccessDeniedException 응답
     */
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

    /**
     *
     * @param reviewId
     * @param principal
     *
     * 리뷰 삭제 처리.
     * 작성자와 요청자가 일치하지 않으면 AccessDeniedException 응답
     */
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

    /**
     *
     * @param principal
     *
     * 정보 수정을 위한 사용자 정보 조회.
     */
    @Override
    public MyPageInfoDTO getInfo(Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        String[] splitMail = member.getUserEmail().split("@");
        String mailSuffix = splitMail[1].substring(0, splitMail[1].indexOf('.'));
        String type = MailSuffix.findSuffixType(mailSuffix);

        return new MyPageInfoDTO(member, splitMail, type);
    }

    /**
     *
     * @param infoDTO
     * @param principal
     *
     * 사용자 정보 수정 처리
     */
    @Override
    public String patchInfo(MyPageInfoPatchDTO infoDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);

        member.patchUser(infoDTO);
        memberRepository.save(member);

        return Result.OK.getResultKey();
    }
}

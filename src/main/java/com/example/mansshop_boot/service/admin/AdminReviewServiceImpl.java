package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.cache.CacheRequest;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductReview;
import com.example.mansshop_boot.domain.entity.ProductReviewReply;
import com.example.mansshop_boot.domain.enumeration.AdminListType;
import com.example.mansshop_boot.domain.enumeration.RedisCaching;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminReviewServiceImpl implements AdminReviewService  {

    private final AdminCacheService adminCacheService;

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewReplyRepository productReviewReplyRepository;

    private final MemberRepository memberRepository;

    /**
     *
     * @param pageDTO
     * @param listType
     *
     * 리뷰 리스트 조회.
     * 미답변 상태인 new, 전체인 all을 동적으로 처리
     * AdminListType이라는 enum을 통해 관리.
     * 검색 타입은 userName || nickname으로 검색하는 'user' 와
     * productName으로 검색하는 'product' 두가지가 존재.
     */
    @Override
    public PagingListDTO<AdminReviewDTO> getReviewList(AdminOrderPageDTO pageDTO, AdminListType listType) {
        List<AdminReviewDTO> content = productReviewRepository.findAllByAdminReviewList(pageDTO, listType.name());
        Long totalElements = 0L;

        if(!content.isEmpty()){
            if(pageDTO.keyword() == null && listType.equals(AdminListType.ALL))
                totalElements = adminCacheService.getFullScanCountCache(
                        RedisCaching.ADMIN_REVIEW_COUNT,
                        new CacheRequest(pageDTO, listType.name())
                );
            else
                totalElements = productReviewRepository.countByAdminReviewList(pageDTO, listType.name());
        }

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(content, pagingMappingDTO);
    }

    /**
     *
     * @param reviewId
     *
     * 리뷰 상세 페이지 데이터
     */
    @Override
    public AdminReviewDetailDTO getReviewDetail(long reviewId) {
        AdminReviewDetailDTO responseDTO = productReviewRepository.findByAdminReviewDetail(reviewId);

        if(responseDTO == null)
            throw new IllegalArgumentException("review Detail Data is null");

        return responseDTO;
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 리뷰 답변 작성
     */
    @Override
    public String postReviewReply(AdminReviewRequestDTO postDTO
            , Principal principal) {
        Member member = memberRepository.findById(principal.getName())
                .orElseThrow(IllegalArgumentException::new);

        ProductReview reviewEntity = productReviewRepository.findById(postDTO.reviewId())
                .orElseThrow(IllegalArgumentException::new);

        ProductReviewReply entity = ProductReviewReply.builder()
                .member(member)
                .replyContent(postDTO.content())
                .productReview(reviewEntity)
                .build();


        reviewEntity.setStatus(true);
        productReviewRepository.save(reviewEntity);
        productReviewReplyRepository.save(entity);

        return Result.OK.getResultKey();
    }
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.*;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductInfoImageRepository productInfoImageRepository;

    private final ProductThumbnailRepository productThumbnailRepository;

    private final ProductReviewRepository productReviewRepository;

    private final ProductQnARepository productQnARepository;

    private final PrincipalService principalService;

    private final ProductLikeRepository productLikeRepository;


    /**
     *
     * @param productId
     * @param principal
     *
     * 상품 상세 페이지에 출력할 데이터 조회.
     *
     * @return
     * 상품 아이디, 상품명, 가격, 대표 썸네일,
     * 찜하기 상태
     * (List) 상품 옵션 아이디, 옵션 사이즈, 옵션 컬러, 옵션 재고
     * (List) 상품 썸네일 이미지명
     * (List) 상품 정보 이미지명
     * (List) 상품 리뷰, 작성자, 작성일, 순서, isEmpty, number, totalPages
     * (List) 상품 문의, 작성자, 작성일, 순서, isEmpty, number, totalPages
     * 로그인 상태 (로그인 상태값, 로그인한 사용자 아이디 또는 닉네임)
     *
     */
    @Override
    public ProductDetailDTO getDetail(String productId, Principal principal) {
        String uid = principalService.getPrincipalUid(principal);
        Product product = productRepository.findById(productId).orElse(null);
        boolean likeStat = false;
        if(product == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        if(uid != null){
            int likeResult = productLikeRepository.countByUserIdAndProductId(principal.getName(), productId);

            if(likeResult == 1)
                likeStat = true;
        }

        List<ProductOptionDTO> productOption = productOptionRepository.findByDetailOption(productId);
        List<String> productThumbnailList = productThumbnailRepository.findByProductId(productId);
        List<String> productInfoImageList = productInfoImageRepository.findByProductId(productId);

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable reviewPageable = PageRequest.of(pageDTO.pageNum() - 1
                                                    , pageDTO.reviewAmount()
                                                    , Sort.by("reviewGroupId").descending()
                                                                    .and(Sort.by("reviewStep").ascending()));
        Pageable qnaPageable = PageRequest.of(pageDTO.pageNum() - 1
                                                    , pageDTO.qnaAmount()
                                                    , Sort.by("productQnAGroupId").descending()
                                                            .and(Sort.by("productQnAStep").ascending()));

        System.out.println("service :: pageSize : " + reviewPageable.getPageSize());
        Page<ProductReviewDTO> productReview = productReviewRepository.findByProductId(productId, reviewPageable);
        Page<ProductQnADTO> productQnA = productQnARepository.findByProductId(productId, qnaPageable);
        UserStatusDTO userStatus = new UserStatusDTO(uid);

        return ProductDetailDTO.builder()
                                .productId(product.getId())
                                .productName(product.getProductName())
                                .productPrice(product.getProductPrice())
                                .productImageName(product.getThumbnail())
                                .productOptionList(productOption)
                                .productThumbnailList(productThumbnailList)
                                .productInfoImageList(productInfoImageList)
                                .productReviewList(new ProductPageableDTO<>(productReview))
                                .productQnAList(new ProductPageableDTO<>(productQnA))
                                .userStatus(userStatus)
                                .likeStat(likeStat)
                                .build();
    }
}

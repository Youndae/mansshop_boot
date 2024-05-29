package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.ProductOptionDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnADTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
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

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * productDetailDTO {
     *     productId
     *     productName
     *     productPrice
     *     productImageName(thumbnail)
     *     productOptionList [
     *          {
     *              optionId
     *              size
     *              color
     *              stock
     *          },
     *          ...
     *     ]
     *     productThumbnailList [
     *          {
     *              productThumbnail
     *          },
     *          ...
     *     ]
     *     productInfoImageList [
     *          {
     *              productInfoImage
     *          },
     *          ...
     *     ]
     *     productReviewList [
     *          {
     *              writer
     *              reviewContent
     *              createdAt
     *              reviewStep
     *          },
     *          ...
     *     ]
     *     productQnAList [
     *          {
     *              writer
     *              qnaContent
     *              createdAt
     *              productQnAStep
     *          },
     *          ...
     *     ]
     *     userStatus {
     *         loggedIn
     *         uid
     *     }
     * }
     */
    @Override
    public ProductDetailDTO getDetail(String productId, Principal principal) {
        String uid = principalService.getPrincipalUid(principal);
        Product product = productRepository.findById(productId).orElse(null);

        if(product == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

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
        Page<ProductReviewDTO> productReview = productReviewRepository.findByProductId(productId, pageDTO, reviewPageable);
        Page<ProductQnADTO> productQnA = productQnARepository.findByProductId(productId, pageDTO, qnaPageable);
        UserStatusDTO userStatus = new UserStatusDTO(uid);

        return ProductDetailDTO.builder()
                                .productId(product.getId())
                                .productName(product.getProductName())
                                .productPrice(product.getProductPrice())
                                .productImageName(product.getThumbnail())
                                .productOptionList(productOption)
                                .productThumbnailList(productThumbnailList)
                                .productInfoImageList(productInfoImageList)
                                .productReviewList(productReview)
                                .productQnAList(productQnA)
                                .userStatus(userStatus)
                                .build();
    }
}

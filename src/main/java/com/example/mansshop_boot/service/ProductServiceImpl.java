package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.product.business.*;
import com.example.mansshop_boot.domain.dto.product.in.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.repository.productLike.ProductLikeRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductInfoImageRepository productInfoImageRepository;

    private final ProductThumbnailRepository productThumbnailRepository;

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewReplyRepository productReviewReplyRepository;

    private final ProductQnARepository productQnARepository;

    private final ProductQnAReplyRepository productQnAReplyRepository;

    private final PrincipalService principalService;

    private final ProductLikeRepository productLikeRepository;

    private final MemberRepository memberRepository;


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
     * 리뷰와 문의 paging 처리에 대한 pageDTO
     * (List) 상품 리뷰, 작성자, 작성일, 답글 내용, 답글 작성일, isEmpty, number, totalPages
     * (List) 상품 문의, 작성자, 작성일, 순서, (List)답글, isEmpty, number, totalPages
     * 로그인 상태 (로그인 상태값, 로그인한 사용자 아이디 또는 닉네임)
     *
     */
    @Override
    public ProductDetailDTO getDetail(String productId, Principal principal) {
        String uid = principalService.getNicknameByPrincipal(principal);
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
        ProductPageableDTO<ProductReviewDTO> productReview = new ProductPageableDTO<>(getDetailReview(pageDTO, productId));
        ProductPageableDTO<ProductQnAResponseDTO> productQnA = new ProductPageableDTO<>(getDetailQnA(pageDTO, productId));

        return new ProductDetailDTO(
                        product
                        , likeStat
                        , productOption
                        , productThumbnailList
                        , productInfoImageList
                        , productReview
                        , productQnA
                );
    }

    /**
     *
     * @param pageDTO
     * @param productId
     * @return
     *
     * 페이징 처리를 해야하기 때문에 기능 분리.
     */
    @Override
    public Page<ProductReviewDTO> getDetailReview(ProductDetailPageDTO pageDTO, String productId) {
        Pageable reviewPageable = PageRequest.of(pageDTO.pageNum() - 1
                                                    , pageDTO.reviewAmount()
                                                    , Sort.by("createdAt").descending()
                                                );

        return productReviewRepository.findByProductId(productId, reviewPageable);
    }

    /**
     *
     * @param pageDTO
     * @param productId
     * @return
     *
     * 페이징 처리를 해야 하기 때문에 기능 분리.
     * 사용자가 작성한 문의 사항을 먼저 10개 단위로 조회.
     * 조회된 결과에서 id를 idList에 담아준 뒤 해당 리스트를 통해 답변 내역 조회
     *
     * 조회된 답변 리스트와 기존 리스트를 반복문으로 비교하면서 체크하고
     * 사용자 작성 문의 데이터와 답변 리스트를 Mapping
     *
     * 처리 이후 ProductPageableDTO 객체 생성 및 반환
     */
    @Override
    public Page<ProductQnAResponseDTO> getDetailQnA(ProductDetailPageDTO pageDTO, String productId) {
        Pageable qnaPageable = PageRequest.of(pageDTO.pageNum() - 1
                                                , pageDTO.qnaAmount()
                                                , Sort.by("createdAt").descending()
                                            );

        Page<ProductQnADTO> productQnA = productQnARepository.findByProductId(productId, qnaPageable);
        List<Long> qnaIdList = productQnA.getContent().stream().map(ProductQnADTO::qnaId).toList();
        List<ProductQnAResponseDTO> productQnADTOList = new ArrayList<>();
        List<ProductQnAReplyListDTO> qnaReplyList = productQnAReplyRepository.getQnAReply(qnaIdList);

        for(int i = 0; i < productQnA.getContent().size(); i++) {
            List<ProductQnAReplyDTO> replyList = new ArrayList<>();
            ProductQnADTO dto = productQnA.getContent().get(i);

            for(int j = 0; j < qnaReplyList.size(); j++) {
                if(dto.qnaId().equals(qnaReplyList.get(j).qnaId())){
                    replyList.add(
                            new ProductQnAReplyDTO(qnaReplyList.get(j))
                    );
                }
            }
            productQnADTOList.add(new ProductQnAResponseDTO(dto, replyList));
        }

        return new PageImpl<>(
                            productQnADTOList
                            , qnaPageable
                            , productQnA.getTotalElements()
                    );
    }


    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * 관심상품 등록
     * 사용자 정보가 존재하지 않는 경우에 대응하기 위해 principal null 체크
     *
     * Member, Product 조회 수행 후 둘중 하나라도 null이라면 NotFoundException 반환
     *
     * 둘다 Null이 아닌 경우 정상으로 판단해 해당 상품에 대해 관심상품 등록
     */
    @Override
    public String likeProduct(Map<String, String> productIdMap, Principal principal) {
        String productId = productIdMap.get("productId");

        if(!productIdMap.containsKey("productId") || productId == null || productId.isBlank()){
            log.warn("ProductService.likeProduct :: IllegalArgumentException by productIdMap");
            throw new IllegalArgumentException();
        }

        ProductLike productLike = setLikeProduct(productId, principal);

        productLikeRepository.save(productLike);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * likeProduct Method 와 동일한 처리.
     *
     * Repository에서 Entity를 받아 그 안의 Member.userId와 Product.id를 통해 일치하는 데이터를 찾아 삭제.
     */
    @Override
    public String deLikeProduct(String productId, Principal principal) {
        ProductLike productLike = setLikeProduct(productId, principal);

        productLikeRepository.deleteByUserIdAndProductId(productLike);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param productId
     * @param principal
     *
     * 상품 아이디와 Principal을 통해 Member Entity, Product Entity를 조회하고
     * ProductLike Entity를 생성해 반환
     */
    public ProductLike setLikeProduct(String productId, Principal principal) {
        if(principal == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        Member member = memberRepository.findById(principal.getName()).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if(member == null || product == null)
            throw new CustomNotFoundException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());

        return ProductLike.builder()
                            .member(member)
                            .product(product)
                            .build();
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 상품 문의 작성
     * 컨트롤러에서 PreAuthorize로 비 로그인한 사람을 걸러내기 때문에 Principal 체크는 따로 하지 않는다.
     */
    @Override
    public String postProductQnA(ProductQnAPostDTO postDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        Product product = productRepository.findById(postDTO.productId()).orElseThrow(IllegalArgumentException::new);
        ProductQnA productQnA = postDTO.toProductQnAEntity(member, product);
        productQnARepository.save(productQnA);

        return Result.OK.getResultKey();
    }
}

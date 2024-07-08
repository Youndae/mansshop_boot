package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.product.in.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import com.example.mansshop_boot.domain.dto.response.PagingElementsResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.ResponseWrappingDTO;
import com.example.mansshop_boot.service.ProductService;
import com.example.mansshop_boot.service.ResponseMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * 상품 상세 페이지 데이터 요청.
     * 상품에 대한 찜하기 상태가 필요하므로 Principal을 같이 받아줌.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO<?>> getDetail(@PathVariable(name = "productId") String productId, Principal principal) {
        ResponseWrappingDTO<ProductDetailDTO> wrappingDTO = new ResponseWrappingDTO<>(productService.getDetail(productId, principal));

        return responseMappingService.mappingResponseDTO(wrappingDTO, principal);
    }

    /**
     *
     * @param productId
     * @param page
     * @return
     *
     * 상품 상세 페이지의 리뷰 데이터 요청.
     * 위 상세 페이지 데이터와 별개로 요청이 한번 더 발생하는 것이 아닌
     * 리뷰 페이징 처리를 위함.
     *
     */
    @GetMapping("/{productId}/review/{page}")
    public ResponseEntity<PagingElementsResponseDTO<?>> getReview(@PathVariable(name = "productId") String productId
                                                        , @PathVariable(name = "page") int page
                                                        , Principal principal) {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);
        Page<ProductReviewDTO> responseDTO = productService.getDetailReview(pageDTO, productId);

        return responseMappingService.mappingPageableElementsResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param productId
     * @param page
     * @return
     *
     * 상품 상세 페이지 QnA 데이터 요청
     * 위 리뷰와 마찬가지로 첫페이지의 데이터가 아닌 페이징 기능을 위함
     */
    @GetMapping("/{productId}/qna/{page}")
    public ResponseEntity<PagingElementsResponseDTO<?>> getQnA(@PathVariable(name = "productId") String productId
                                                    , @PathVariable(name = "page") int page
                                                    , Principal principal) {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);
        Page<ProductQnAResponseDTO> responseDTO = productService.getDetailQnA(pageDTO, productId);

        return responseMappingService.mappingPageableElementsResponseDTO(responseDTO, principal);
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 상품 상세 페이지에서 상품 문의 작성
     * 로그인한 사용자만 요청 가능
     */
    @PostMapping("/qna")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> postProductQnA(@RequestBody ProductQnAPostDTO postDTO, Principal principal) {

        String responseMessage = productService.postProductQnA(postDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * 관심상품 등록 기능
     */
    @PostMapping("/like")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> likeProduct(@RequestBody Map<String, String> productId
                                        , Principal principal) {

        String productIdValue = productId.get("productId");

        String responseMessage = productService.likeProduct(productIdValue, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * 관심상품 해제 기능
     */
    @DeleteMapping("/like/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deLikeProduct(@PathVariable(name = "productId") String productId
            , Principal principal) {

        String responseMessage = productService.deLikeProduct(productId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.product.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.ProductPageableDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

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
    public ResponseEntity<ProductDetailDTO> getDetail(@PathVariable(name = "productId") String productId, Principal principal) {

        ProductDetailDTO responseDTO = productService.getDetail(productId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
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
    public ResponseEntity<ProductPageableDTO<ProductReviewDTO>> getReview(@PathVariable(name = "productId") String productId
                                                        , @PathVariable(name = "page") int page) {

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);

        ProductPageableDTO<ProductReviewDTO> responseDTO = productService.getDetailReview(pageDTO, productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
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
    public ResponseEntity<ProductPageableDTO<ProductQnAResponseDTO>> getQnA(@PathVariable(name = "productId") String productId
                                                    , @PathVariable(name = "page") int page) {

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);

        ProductPageableDTO<ProductQnAResponseDTO> responseDTO = productService.getDetailQnA(pageDTO, productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PostMapping("/qna")
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
    public ResponseEntity<?> deLikeProduct(@PathVariable(name = "productId") String productId
            , Principal principal) {

        String responseMessage = productService.deLikeProduct(productId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

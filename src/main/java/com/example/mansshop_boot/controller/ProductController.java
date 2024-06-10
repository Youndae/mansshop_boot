package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.ProductPageableDTO;
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

        return new ResponseEntity<>(productService.getDetail(productId, principal), HttpStatus.OK);
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
    public ResponseEntity<ProductPageableDTO> getReview(@PathVariable(name = "productId") String productId
                                                        , @PathVariable(name = "page") int page) {

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);

        return new ResponseEntity<>(productService.getDetailReview(pageDTO, productId), HttpStatus.OK);
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
    public ResponseEntity<ProductPageableDTO> getQnA(@PathVariable(name = "productId") String productId
                                                    , @PathVariable(name = "page") int page) {

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);

        return new ResponseEntity<>(productService.getDetailQnA(pageDTO, productId), HttpStatus.OK);
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

        return productService.likeProduct(productIdValue, principal);
    }

    /**
     *
     * @param productId
     * @param principal
     * @return
     *
     * 관심상품 해제 기능
     */
    @DeleteMapping("/de-like/{productId}")
    public ResponseEntity<?> deLikeProduct(@PathVariable(name = "productId") String productId
            , Principal principal) {

        return productService.deLikeProduct(productId, principal);
    }
}

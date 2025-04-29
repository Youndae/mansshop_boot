package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.product.in.ProductQnAPostDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductDetailDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductQnAResponseDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import com.example.mansshop_boot.domain.dto.response.PagingElementsResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.ProductService;
import com.example.mansshop_boot.service.ResponseMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    @Operation(summary = "상품 상세 데이터 조회",
            description = "JWT를 같이 보내면 관심상품 여부를 확인 가능"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(
                    name = "productId",
                    description = "상품 아이디",
                    example = "BAGS20210629134401",
                    required = true,
                    in = ParameterIn.PATH
            ),
            @Parameter(
                    name = "Authorization",
                    description = "AccessToken Header",
                    in = ParameterIn.HEADER
            ),
            @Parameter(
                    name = "Authorization_Refresh",
                    description = "RefreshToken Cookie",
                    in = ParameterIn.COOKIE
            ),
            @Parameter(
                    name = "Authorization_ino",
                    description = "ino Cookie",
                    in = ParameterIn.COOKIE
            )
    })
    @SecurityRequirements({
            @SecurityRequirement(name = "Authorization"),
            @SecurityRequirement(name = "Authorization_Refresh"),
            @SecurityRequirement(name = "Authorization_ino")
    })
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
    @Operation(summary = "상품 상세 페이지에서 리뷰 데이터 조회")
    @DefaultApiResponse
    @Parameters({
            @Parameter(
                    name = "productId",
                    description = "상품 아이디",
                    example = "BAGS20210629134401",
                    required = true,
                    in = ParameterIn.PATH
            ),
            @Parameter(
                    name = "page",
                    description = "페이지 번호",
                    example = "1",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(
                    name = "Authorization",
                    description = "AccessToken Header",
                    in = ParameterIn.HEADER
            ),
            @Parameter(
                    name = "Authorization_Refresh",
                    description = "RefreshToken Cookie",
                    in = ParameterIn.COOKIE
            ),
            @Parameter(
                    name = "Authorization_ino",
                    description = "ino Cookie",
                    in = ParameterIn.COOKIE
            )
    })
    @SecurityRequirements({
            @SecurityRequirement(name = "Authorization"),
            @SecurityRequirement(name = "Authorization_Refresh"),
            @SecurityRequirement(name = "Authorization_ino")
    })
    @GetMapping("/{productId}/review")
    public ResponseEntity<PagingElementsResponseDTO<ProductReviewDTO>> getReview(@PathVariable(name = "productId") String productId,
                                                        @RequestParam(name = "page") int page) {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);
        Page<ProductReviewDTO> responseDTO = productService.getDetailReview(pageDTO, productId);

        return responseMappingService.mappingPageableElementsResponseDTO(responseDTO);
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
    @Operation(summary = "상품 상세 페이지에서 상품 문의 데이터 조회")
    @DefaultApiResponse
    @Parameters({
            @Parameter(
                    name = "productId",
                    description = "상품 아이디",
                    example = "BAGS20210629134401",
                    required = true,
                    in = ParameterIn.PATH
            ),
            @Parameter(
                    name = "page",
                    description = "페이지 번호.",
                    example = "2",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(
                    name = "Authorization",
                    description = "AccessToken Header",
                    in = ParameterIn.HEADER
            ),
            @Parameter(
                    name = "Authorization_Refresh",
                    description = "RefreshToken Cookie",
                    in = ParameterIn.COOKIE
            ),
            @Parameter(
                    name = "Authorization_ino",
                    description = "ino Cookie",
                    in = ParameterIn.COOKIE
            )
    })
    @SecurityRequirements({
            @SecurityRequirement(name = "Authorization"),
            @SecurityRequirement(name = "Authorization_Refresh"),
            @SecurityRequirement(name = "Authorization_ino")
    })
    @GetMapping("/{productId}/qna")
    public ResponseEntity<PagingElementsResponseDTO<ProductQnAResponseDTO>> getQnA(@PathVariable(name = "productId") String productId,
                                                    @RequestParam(name = "page") int page) {

        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO(page);
        Page<ProductQnAResponseDTO> responseDTO = productService.getDetailQnA(pageDTO, productId);

        return responseMappingService.mappingPageableElementsResponseDTO(responseDTO);
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 상품 상세 페이지에서 상품 문의 작성
     * 로그인한 사용자만 요청 가능
     */
    @Operation(summary = "상품 문의 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/qna")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessageDTO> postProductQnA(@RequestBody ProductQnAPostDTO postDTO, Principal principal) {

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
    @Operation(summary = "관심상품 등록")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/like")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessageDTO> likeProduct(@Schema(name = "productId", description = "상품 아이디")
                                             @RequestBody String productId,
                                        Principal principal) {
        String responseMessage = productService.likeProduct(productId, principal);

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
    @Operation(summary = "관심상품 해제")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
            description = "상품 아이디",
            example = "BAGS20210629134401",
            required = true,
            in = ParameterIn.PATH
    )
    @DeleteMapping("/like/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessageDTO> deLikeProduct(@PathVariable(name = "productId") String productId,
                                                            Principal principal) {

        String responseMessage = productService.deLikeProduct(productId, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

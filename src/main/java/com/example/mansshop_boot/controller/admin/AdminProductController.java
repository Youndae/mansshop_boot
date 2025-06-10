package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.service.ResponseMappingService;
import com.example.mansshop_boot.service.admin.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param keyword
     * @param page
     *
     * 관리자 상품 목록 리스트 조회
     */
    @Operation(summary = "관리자 상품 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호.",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어. 검색 요청시에만 필요.",
                    example = "DummyOUTER",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product")
    public ResponseEntity<PagingResponseDTO<AdminProductListDTO>> getProductList(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page){

        PagingListDTO<AdminProductListDTO> responseDTO = adminProductService.getProductList(new AdminPageDTO(keyword, page));

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * 상품 분류 리스트 조회.
     * 상품 추가 혹은 상품 할인 추가시 조회.
     * 모든 상품 분류명을 반환
     */
    @Operation(summary = "관리자 상품 분류 조회", description = "분류별 리스트 조회가 아닌 상품 분류명 리스트를 반환. 상품 추가 및 할인 추가 시 필요.")
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/product/classification")
    public ResponseEntity<List<String>> getProductClassification() {
        List<String> responseDTO = adminProductService.getClassification();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param productId
     *
     * 관리자 상품 상세 정보 조회
     */
    @Operation(summary = "관리자 상품 상세 정보 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
            description = "상품 아이디",
            example = "BAGS20210629134401",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<AdminProductDetailDTO> getProductDetail(@PathVariable(name = "productId") String productId){
        AdminProductDetailDTO responseDTO = adminProductService.getProductDetail(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param postDTO
     * @param imageDTO
     *
     * 관리자의 상품 추가 요청
     * 상품 데이터는 AdminProductPatchDTO
     * 상품 대표 썸네일, 썸네일, 정보 이미지 Multipart는 AdminProductImageDTO
     */
    @Operation(summary = "관리자 상품 추가",
            description = "이 API는 swagger에서 테스트 불가. MultipartFile 또는 List<MultipartFile> 객체들을 DTO 필드로 받고 있기 때문에 Swagger에서 테스트는 불가능."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping(value = "/product", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseIdDTO<String>> postProduct(@ModelAttribute AdminProductPatchDTO postDTO,
                                                             @ModelAttribute AdminProductImageDTO imageDTO) {
        ResponseIdDTO<String> responseDTO = new ResponseIdDTO<>(adminProductService.postProduct(postDTO, imageDTO));

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param productId
     *
     * 관리자의 상품 수정 페이지에서 상품 정보 요청
     */
    @Operation(summary = "관리자 상품 수정 페이지 데이터 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
            description = "상품 아이디",
            example = "BAGS20210629134401",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/product/patch/{productId}")
    public ResponseEntity<AdminProductPatchDataDTO> getPatchProductData(@PathVariable(name = "productId") String productId) {

        AdminProductPatchDataDTO responseDTO = adminProductService.getPatchProductData(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param productId
     * @param deleteOptionList
     * @param patchDTO
     * @param imageDTO
     *
     * 관리자의 상품 수정
     * 상품 id를 반환
     */
    @Operation(summary = "관리자 상품 수정",
            description = "이 API는 swagger에서 테스트 불가. MultipartFile 또는 List<MultipartFile> 객체들을 DTO 필드로 받고 있기 때문에 Swagger에서 테스트는 불가능."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "productId",
                    description = "수정할 상품 아이디",
                    example = "BAGS20210629134401",
                    required = true,
                    in = ParameterIn.PATH
            )
    })
    @PatchMapping(value = "/product/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseIdDTO<String>> patchProduct(@PathVariable(name = "productId") String productId,
                                                              @RequestPart(value = "deleteOptionList", required = false) List<Long> deleteOptionList,
                                                              @ModelAttribute AdminProductPatchDTO patchDTO,
                                                              @ModelAttribute AdminProductImageDTO imageDTO) {

        ResponseIdDTO<String> responseDTO = new ResponseIdDTO<>(
                adminProductService.patchProduct(productId, deleteOptionList, patchDTO, imageDTO)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param keyword
     * @param page
     *
     * 관리자 상품 재고 리스트.
     * 재고가 적은순으로 정렬
     */
    @Operation(summary = "상품 재고 리스트 조회",
            description = "재고가 적은순으로 정렬"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호.",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어(상품명)",
                    example = "DummyOUTER",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product/stock")
    public ResponseEntity<PagingResponseDTO<AdminProductStockDTO>> getProductStock(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                   @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);
        PagingListDTO<AdminProductStockDTO> responseDTO = adminProductService.getProductStock(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param keyword
     * @param page
     *
     * 할인중인 상품 리스트 조회
     */
    @Operation(summary = "할인중인 상품 리스트 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호.",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어(상품명)",
                    example = "DummyOUTER",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/product/discount")
    public ResponseEntity<PagingResponseDTO<AdminDiscountResponseDTO>> getDiscountProductList(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                              @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        PagingListDTO<AdminDiscountResponseDTO> responseDTO = adminProductService.getDiscountProduct(pageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param classification
     *
     * 상품 할인 설정에서 상품 select box에 사용될 상품 리스트.
     * 선택한 상품 분류에 따라 그 분류에 해당하는 상품들의 리스트 반환.
     */
    @Operation(summary = "상품 할인 설정에서 선택한 상품 분류에 해당하는 상품 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "classification",
            description = "상품분류 아이디",
            example = "OUTER",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/product/discount/select/{classification}")
    public ResponseEntity<List<AdminDiscountProductDTO>> getDiscountProductSelectList(@PathVariable(name = "classification") String classification) {

        List<AdminDiscountProductDTO> responseDTO = adminProductService.getSelectDiscountProduct(classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param patchDTO
     *
     * 상품 할인 설정.
     */
    @Operation(summary = "상품 할인 설정 요청",
            description = "여러 상품 아이디를 보내 복수의 상품을 동일한 할인율로 설정 가능"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping(value = "/product/discount", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseMessageDTO> patchDiscountProduct(@RequestBody AdminDiscountPatchDTO patchDTO) {

        String responseMessage = adminProductService.patchDiscountProduct(patchDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ResponseMessageDTO(responseMessage)
                );
    }
}

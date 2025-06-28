package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodClassificationDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingElementsResponseDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.service.ResponseMappingService;
import com.example.mansshop_boot.service.admin.AdminSalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminSalesController {

    private final AdminSalesService adminSalesService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param term
     *
     * 기간별 매출 조회.
     * term은 연도를 받는다.
     */
    @Operation(summary = "기간별 매출 조회",
            description = "연도별 매출 조회기능. 해당 연도의 월별 매출 데이터를 조회"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회할 연도. 3년 전 데이터까지만 저장",
            example = "2023",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/sales/period/{term}")
    public ResponseEntity<AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO>> getPeriodSales(@PathVariable(name = "term") int term) {

        AdminPeriodSalesResponseDTO<AdminPeriodSalesListDTO> responseDTO = adminSalesService.getPeriodSales(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }


    /**
     *
     * @param term
     *
     * 관리자의 월매출 조회.
     * term은 YYYY-MM으로 연월을 받는다.
     */
    @Operation(summary = "월 매출 조회",
            description = "해당 월의 매출 데이터 조회"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회하고자 하는 연/월",
            example = "2023-04",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("sales/period/detail/{term}")
    public ResponseEntity<AdminPeriodMonthDetailResponseDTO> getPeriodSalesDetail(@PathVariable(name = "term") String term) {

        AdminPeriodMonthDetailResponseDTO responseDTO = adminSalesService.getPeriodSalesDetail(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param term
     * @param classification
     *
     * 특정 상품 분류의 월 매출 조회
     * term으로 YYYY-MM 구조의 연월을 받는다.
     */
    @Operation(summary = "선택한 상품 분류의 월 매출 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "term",
                    description = "조회하고자 하는 연/월",
                    example = "2023-04",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "classification",
                    description = "조회하고자 하는 상품 분류",
                    example = "OUTER",
                    required = true,
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/sales/period/detail/classification")
    public ResponseEntity<AdminClassificationSalesResponseDTO> getSalesByClassification(@RequestParam(value = "term")String term,
                                                                                        @RequestParam(value = "classification") String classification) {

        AdminClassificationSalesResponseDTO responseDTO = adminSalesService.getSalesByClassification(term, classification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param term
     *
     * 일매출 조회.
     * term으로 YYYY-MM-DD 로 연월일을 받는다.
     */
    @Operation(summary = "일 매출 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "term",
            description = "조회하고자 하는 연/월/일",
            example = "2023-04-02",
            required = true,
            in = ParameterIn.QUERY
    )
    @GetMapping("/sales/period/detail/day")
    public ResponseEntity<AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO>> getSalesByDay(@RequestParam(value = "term") String term) {

        AdminPeriodSalesResponseDTO<AdminPeriodClassificationDTO> responseDTO = adminSalesService.getSalesByDay(term);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param term
     * @param page
     *
     * 선택 일자의 모든 주문 목록을 조회
     * term으로 YYYY-MM-DD구조의 연월일을 받는다.
     */
    @Operation(summary = "선택일자의 모든 주문 목록 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "term",
                    description = "조회하고자 하는 연/월/일",
                    example = "2023-04-02",
                    required = true,
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    required = true,
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/sales/period/order-list")
    public ResponseEntity<PagingElementsResponseDTO<AdminDailySalesResponseDTO>> getOrderListByDay(@RequestParam(value = "term") String term,
                                                                                                   @RequestParam(value = "page") int page) {


        PagingListDTO<AdminDailySalesResponseDTO> responseDTO = adminSalesService.getOrderListByDay(term, page);
        return responseMappingService.mappingPagingElementsResponseDTO(responseDTO);
    }


    /**
     *
     * @param keyword
     * @param page
     *
     * 상품별 매출 조회.
     * 상품 분류를 기준으로 정렬한다.
     */
    @Operation(summary = "상품별 매출 조회",
            description = "상품 분류를 기준으로 정렬"
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.PATH
            ),
            @Parameter(name = "keyword",
                    description = "검색어. 상품명",
                    example = "DummyOUTER",
                    in = ParameterIn.PATH
            )
    })
    @GetMapping("/sales/product")
    public ResponseEntity<PagingResponseDTO<AdminProductSalesListDTO>> getProductSales(@RequestParam(value = "keyword", required = false) String keyword,
                                                                                       @RequestParam(value = "page", required = false, defaultValue = "1") int page) {

        AdminPageDTO pageDTO = new AdminPageDTO(keyword, page);

        Page<AdminProductSalesListDTO> responseDTO = adminSalesService.getProductSalesList(pageDTO);
        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param productId
     *
     * 상품 매출 상세 내역 조회
     *
     */
    @Operation(summary = "상품 매출의 상세 내역 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "productId",
            description = "상품 아이디",
            example = "BAGS20210629134401",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/sales/product/detail/{productId}")
    public ResponseEntity<AdminProductSalesDetailDTO> getProductSales(@PathVariable(name = "productId") String productId) {

        AdminProductSalesDetailDTO responseDTO = adminSalesService.getProductSalesDetail(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);

    }
}

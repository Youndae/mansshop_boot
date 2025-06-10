package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminReviewRequestDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.enumeration.AdminListType;
import com.example.mansshop_boot.service.ResponseMappingService;
import com.example.mansshop_boot.service.admin.AdminReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param keyword
     * @param page
     * @param searchType
     *
     * 관리자의 새로운 리뷰 리스트 조회.
     */
    @Operation(summary = "새로운 리뷰 리스트 조회",
            description = "검색은 사용자 아이디 및 닉네임 또는 상품명 기반으로 선택 후 검색. 새로운 리뷰의 기준은 답변 처리가 안된 리뷰."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. user 또는 product",
                    example = "user",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/review")
    public ResponseEntity<PagingResponseDTO<AdminReviewDTO>> getNewReviewList(@RequestParam(name = "keyword", required = false) String keyword,
                                                                              @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                              @RequestParam(name = "searchType", required = false) String searchType) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminReviewDTO> responseDTO = adminReviewService.getReviewList(pageDTO, AdminListType.NEW);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param keyword
     * @param page
     * @param searchType
     *
     * 전체 리뷰 조회.
     */
    @Operation(summary = "전체 리뷰 조회",
            description = "검색은 사용자 아이디 및 닉네임 또는 상품명 기반으로 선택 후 검색. 새로운 리뷰의 기준은 답변 처리가 안된 리뷰."
    )
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameters({
            @Parameter(name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "searchType",
                    description = "검색 타입. user 또는 product",
                    example = "user",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/review/all")
    public ResponseEntity<PagingResponseDTO<AdminReviewDTO>> getAllReviewList(@RequestParam(name = "keyword", required = false) String keyword,
                                                                              @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                              @RequestParam(name = "searchType", required = false) String searchType) {
        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        PagingListDTO<AdminReviewDTO> responseDTO = adminReviewService.getReviewList(pageDTO, AdminListType.ALL);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param reviewId
     *
     * 리뷰 상세 데이터 조회
     */
    @Operation(summary = "리뷰 상세 데이터 조회")
    @DefaultApiResponse
    @SwaggerAuthentication
    @Parameter(name = "reviewId",
            description = "리뷰 아이디",
            example = "1",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/review/detail/{reviewId}")
    public ResponseEntity<AdminReviewDetailDTO> getReviewDetail(@PathVariable("reviewId") long reviewId) {
        AdminReviewDetailDTO responseDTO = adminReviewService.getReviewDetail(reviewId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param postDTO
     * @param principal
     *
     * 관리자의 리뷰 답변 작성
     */
    @Operation(summary = "리뷰 답변 작성")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/review/reply")
    public ResponseEntity<ResponseMessageDTO> postReviewReply(@RequestBody AdminReviewRequestDTO postDTO,
                                                              Principal principal) {

        String responseMessage = adminReviewService.postReviewReply(postDTO, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

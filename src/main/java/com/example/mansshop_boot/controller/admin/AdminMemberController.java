package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.ResponseMappingService;
import com.example.mansshop_boot.service.admin.AdminMemberService;
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
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    private final ResponseMappingService responseMappingService;

    /**
     *
     * @param keyword
     * @param searchType
     * @param page
     *
     * 관리자의 회원 목록 조회
     */
    @Operation(summary = "회원 목록 조회",
            description = "검색 타입으로는 아이디, 사용자 이름, 닉네임이 존재"
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
                    description = "검색 타입. userId, userName, nickname",
                    example = "userId",
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                    description = "검색어",
                    example = "tester1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/member")
    public ResponseEntity<PagingResponseDTO<AdminMemberDTO>> getMember(@RequestParam(name = "keyword", required = false) String keyword,
                                                                       @RequestParam(name = "searchType", required = false) String searchType,
                                                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(keyword, searchType, page);
        Page<AdminMemberDTO> responseDTO = adminMemberService.getMemberList(pageDTO);

        return responseMappingService.mappingPageableResponseDTO(responseDTO);
    }

    /**
     *
     * @param pointDTO
     *
     * 회원에게 포인트 직접 지급
     */
    @Operation(summary = "회원 포인트 지급")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PatchMapping("/member/point")
    public ResponseEntity<ResponseMessageDTO> postPoint(@RequestBody AdminPostPointDTO pointDTO){

        String responseMessage = adminMemberService.postPoint(pointDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}

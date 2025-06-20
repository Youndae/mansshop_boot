package com.example.mansshop_boot.controller;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.business.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.MyPageOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.service.FileService;
import com.example.mansshop_boot.service.MainService;
import com.example.mansshop_boot.service.MyPageService;
import com.example.mansshop_boot.service.ResponseMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainService mainService;

    private final MyPageService myPageService;

    private final ResponseMappingService responseMappingService;

    private final FileService fileService;

    /**
     *
     * 메인의 BEST 리스트 조회.
     */
    @Operation(summary = "메인 BEST 상품 조회")
    @DefaultApiResponse
    @GetMapping("/")
    public ResponseEntity<List<MainListResponseDTO>> mainList() {
        MainPageDTO mainPageDTO = new MainPageDTO("BEST");
        List<MainListResponseDTO> responseDTO = mainService.getBestAndNewList(mainPageDTO);


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    @Operation(summary = "메인 NEW 상품 카테고리 조회")
    @DefaultApiResponse
    @GetMapping("/new")
    public ResponseEntity<List<MainListResponseDTO>> mainNewList() {
        MainPageDTO mainPageDTO = new MainPageDTO("NEW");
        List<MainListResponseDTO> responseDTO = mainService.getBestAndNewList(mainPageDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param classification
     * @param page
     *
     * 메인의 상품 리스트 중 상품 카테고리 선택으로 인한 리스트 조회.
     */
    @Operation(summary = "상품 분류별 조회 요청")
    @DefaultApiResponse
    @Parameters({
            @Parameter(
                    name = "classification",
                    description = "상품 분류. OUTER, TOP, PANTS, SHOES, BAGS",
                    example = "OUTER",
                    required = true,
                    in = ParameterIn.PATH
            ),
            @Parameter(
                    name = "page",
                    description = "페이지 번호",
                    example = "1",
                    in = ParameterIn.QUERY
            )
    })
    @GetMapping("/{classification}")
    public ResponseEntity<PagingResponseDTO<MainListResponseDTO>> mainClassificationList(@PathVariable(name = "classification") String classification,
                                                            @RequestParam(name = "page", required = false, defaultValue = "1") int page){

        MainPageDTO mainPageDTO = MainPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(null)
                                                .classification(classification)
                                                .build();

        PagingListDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(mainPageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param page
     * @param keyword
     *
     * Navbar의 상품 검색 기능
     */
    @Operation(summary = "상품 검색")
    @DefaultApiResponse
    @Parameters({
            @Parameter(name = "page",
                        description = "페이지 번호",
                        example = "1",
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "keyword",
                        description = "검색어",
                        example = "DummyOUTER",
                        required = true
            )
    })
    @GetMapping("/search")
    public ResponseEntity<PagingResponseDTO<MainListResponseDTO>> searchList(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                    @RequestParam(name = "keyword") String keyword){

        MainPageDTO mainPageDTO = MainPageDTO.builder()
                                                .pageNum(page)
                                                .keyword(keyword)
                                                .classification(null)
                                                .build();
        PagingListDTO<MainListResponseDTO> responseDTO = mainService.getClassificationAndSearchList(mainPageDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }

    /**
     *
     * @param imageName
     *
     * 파일 저장을 local로 처리하는 경우 사용.
     */
    @Operation(summary = "이미지 파일 Binary Data 반환")
    @DefaultApiResponse
    @Parameter(name = "imageName",
                description = "이미지 파일명",
                example = "2149347511.jpg",
                required = true,
                in = ParameterIn.PATH
    )
    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> display(@PathVariable(name = "imageName") String imageName) {

        return fileService.getDisplayImage(imageName);
    }

    /**
     *
     * @param imageName
     *
     * S3에서 파일을 받는 경우.
     * Spring Boot 서버를 proxy 서버로 사용해 파일 다운로드 후 blob으로 반환
     */
    /*@GetMapping("/display/{imageName}")
    public ResponseEntity<InputStreamResource> display(@PathVariable(name = "imageName") String imageName) {

        return fileService.getImageFile(imageName);
    }*/

    /**
     *
     * @param recipient
     * @param phone
     * @param term
     * @param page
     *
     * 비회원의 주문 내역 조회
     * 가능성은 별로 없다고 생각하지만 비회원도 같은 recipient와 연락처로 장기간 동안 주문한 경우
     * 해당 기간 동안의 내역을 출력할 수 있어야 한다고 생각해 사용자 주문내역과 마찬가지로 term을 받아 기간별 조회 처리.
     */
    @Operation(summary = "비회원의 주문내역 조회")
    @DefaultApiResponse
    @Parameters({
            @Parameter(name = "recipient",
                        description = "수령인",
                        example = "테스터1000",
                        required = true,
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "phone",
                        description = "수령인 연락처",
                        example = "01034568890",
                        required = true,
                        in = ParameterIn.QUERY
            ),
            @Parameter(name = "term",
                        description = "조회 기간. 페이지 최초 접근시에는 3으로 처리. 3, 6, 12, all로 구성",
                        example = "3",
                        required = true,
                        in = ParameterIn.PATH
            ),
            @Parameter(name = "page",
                        description = "페이지 번호. 최소값 1",
                        example = "1",
                        required = true,
                        in = ParameterIn.PATH
            )
    })
    @GetMapping("/order/{term}")
    public ResponseEntity<PagingResponseDTO<MyPageOrderDTO>> nonMemberOrderList(@RequestParam(name = "recipient") String recipient,
                                                @RequestParam(name = "phone") String phone,
                                                @PathVariable(name = "term") String term,
                                                @RequestParam(name = "page", required = false, defaultValue = "1") int page){

        MemberOrderDTO memberOrderDTO = MemberOrderDTO.builder()
                                            .userId("Anonymous")
                                            .recipient(recipient)
                                            .phone(phone)
                                            .build();

        OrderPageDTO orderPageDTO = OrderPageDTO.builder()
                                        .pageNum(page)
                                        .term(term)
                                        .build();


        PagingListDTO<MyPageOrderDTO> responseDTO = myPageService.getOrderList(orderPageDTO, memberOrderDTO);

        return responseMappingService.mappingPagingResponseDTO(responseDTO);
    }
}

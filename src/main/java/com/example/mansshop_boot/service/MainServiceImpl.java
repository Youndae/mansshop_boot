package com.example.mansshop_boot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.example.mansshop_boot.domain.dto.main.business.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.out.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MainPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService{

    private final ProductRepository productRepository;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 메인 상품 목록에서 BEST 상품과 NEW 상품의 목록 조회.
     * 해당 카테고리들은 페이징처리 없이 12개의 데이터만 필요하므로 따로 분리해서 처리.
     *
     */
    @Override
    public List<MainListResponseDTO> getBestAndNewList(MainPageDTO pageDTO, Principal principal) {
        List<MainListDTO> listDto = productRepository.findListDefault(pageDTO);

        return mainListDataMapping(listDto);
    }

    /**
     *
     * @param pageDTO
     * @param principal
     *
     * 분류에 해당하는 상품 목록 조회.
     * 개인 쇼핑몰 컨셉이기 때문에 상품 목록이 엄청 많지 않을 것이라고 생각해 페이징의 직접 구현이 아닌 Pageable을 통한 처리.
     */
    @Override
    public PagingListDTO<MainListResponseDTO> getClassificationAndSearchList(MainPageDTO pageDTO, Principal principal) {
        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.mainProductAmount()
                                            , Sort.by("createdAt").descending()
        );

        Page<MainListDTO> dto = productRepository.findListPageable(pageDTO, pageable);
        List<MainListResponseDTO> responseDTO = mainListDataMapping(dto.getContent());
        PagingMappingDTO pagingMappingDTO = PagingMappingDTO.builder()
                                                .totalElements(dto.getTotalElements())
                                                .totalPages(dto.getTotalPages())
                                                .empty(dto.isEmpty())
                                                .number(dto.getNumber())
                                                .build();

        return new PagingListDTO<>(responseDTO, pagingMappingDTO);
    }

    /**
     *
     * @param dto
     *
     * 상품 리스트 매핑.
     * 할인가 처리를 위해 다른 DTO에 매핑처리한다.
     * 할인가를 쿼리에서 연산하는 것보다 이렇게 연산해 처리하는 것이 더 낫겠다는 생각해 처리.
     * MainListResponseDTo 생성자 내부에서 연산 처리후 생성.
     */
    public List<MainListResponseDTO> mainListDataMapping(List<MainListDTO> dto) {

        return dto.stream().map(MainListResponseDTO::new).toList();
    }


    /**
     *
     * @param imageName
     *
     * preSignedUrl로 반환.
     * 테스트를 위해 만료 시간은 1분으로 설정
     */
    /*@Override
    public URL getSignedUrl(String imageName) {

        Date expiration = new Date();
        long expirationTime = expiration.getTime();
        expirationTime += 1000 * 60 * 1; //1 minute 최종 설정은 테스트 해보고 결정
        expiration.setTime(expirationTime);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, imageName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);


        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }*/

    /**
     *
     * @param imageName
     *
     * S3로부터 파일을 다운받아 InputStreamResource 타입으로 반환.
     * 프론트엔드에서는 blob으로 받아 처리.
     */
    @Override
    public ResponseEntity<InputStreamResource> getImageFile(String imageName) {
        S3Object s3Object = amazonS3.getObject(bucket, imageName);
        InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .body(resource);
    }
}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.response.PagingElementsResponseDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * UserStatus와 같이 전달해야 하는 응답 데이터에 대한 매핑을 처리하는 서비스
 * 응답 데이터 매핑이기 때문에 모두 ResponseEntity를 반환한다.
 */
@Service
@RequiredArgsConstructor
public class ResponseMappingServiceImpl implements ResponseMappingService{

    /**
     *
     * @param content
     *
     * 페이징 처리를 하는 객체에 대한 매핑
     * Pageable을 사용하지 않은 기능이나 Pageable을 사용했지만 DTO에 재 매핑하면서
     * 페이징 정보가 따로 매핑이 되는 경우
     */
    @Override
    public <T> ResponseEntity<PagingResponseDTO<T>> mappingPagingResponseDTO(PagingListDTO<T> content) {

        PagingResponseDTO<T> responseDTO = new PagingResponseDTO<>(content);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param content
     *
     * Pageable을 통한 페이징 데이터 반환에 대한 매핑
     */
    @Override
    public <T> ResponseEntity<PagingResponseDTO<T>> mappingPageableResponseDTO(Page<T> content) {

        PagingResponseDTO<T> responseDTO = new PagingResponseDTO<>(content);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param content
     *
     * Pageable을 통한 페이징 데이터 반환에 대한 매핑.
     * 추가적으로 TotalElement가 필요한 경우의 매핑
     */
    @Override
    public <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPageableElementsResponseDTO(Page<T> content) {

        PagingElementsResponseDTO<T> responseDTO = new PagingElementsResponseDTO<>(content);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param content
     * 페이징 처리를 하는 객체에 대한 매핑
     * Pageable을 사용하지 않은 기능이나 Pageable을 사용했지만 DTO에 재 매핑하면서
     * 페이징 정보가 따로 매핑이 되는 경우
     * 추가적으로 TotalElement가 필요한 경우의 매핑
     */
    @Override
    public <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPagingElementsResponseDTO(PagingListDTO<T> content) {

        PagingElementsResponseDTO<T> responseDTO = new PagingElementsResponseDTO<>(content);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }
}

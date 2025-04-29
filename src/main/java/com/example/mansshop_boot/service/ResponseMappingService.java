package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;


public interface ResponseMappingService {

    <T> ResponseEntity<PagingResponseDTO<T>> mappingPagingResponseDTO(PagingListDTO<T> content);

    <T> ResponseEntity<PagingResponseDTO<T>> mappingPageableResponseDTO(Page<T> content);

    <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPageableElementsResponseDTO(Page<T> content);

    <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPagingElementsResponseDTO(PagingListDTO<T> content);

}

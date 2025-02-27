package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.ResponseWrappingDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface ResponseMappingService {

    <T> ResponseEntity<ResponseDTO<T>> mappingResponseDTO(ResponseWrappingDTO<T> content, Principal principal);

    <T> ResponseEntity<PagingResponseDTO<T>> mappingPagingResponseDTO(PagingListDTO<T> content, Principal principal);

    <T> ResponseEntity<PagingResponseDTO<T>> mappingPageableResponseDTO(Page<T> content, Principal principal);

    <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPageableElementsResponseDTO(Page<T> content, Principal principal);

    <T> ResponseEntity<PagingElementsResponseDTO<T>> mappingPagingElementsResponseDTO(PagingListDTO<T> content, Principal principal);

    <T> ResponseEntity<ResponseListDTO<T>>mappingResponseListDTO(List<T> content, Principal principal);

}

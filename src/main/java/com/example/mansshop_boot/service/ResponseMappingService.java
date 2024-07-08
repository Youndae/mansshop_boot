package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.ResponseWrappingDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface ResponseMappingService {

    ResponseEntity<ResponseDTO<?>> mappingResponseDTO(ResponseWrappingDTO<?> content, Principal principal);

    ResponseEntity<PagingResponseDTO<?>> mappingPagingResponseDTO(PagingListDTO<?> content, Principal principal);

    ResponseEntity<PagingResponseDTO<?>> mappingPageableResponseDTO(Page<?> content, Principal principal);

    ResponseEntity<PagingElementsResponseDTO<?>> mappingPageableElementsResponseDTO(Page<?> content, Principal principal);

    ResponseEntity<PagingElementsResponseDTO<?>> mappingPagingElementsResponseDTO(PagingListDTO<?> content, Principal principal);

    ResponseEntity<ResponseListDTO<?>> mappingResponseListDTO(List<?> content, Principal principal);

}

package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final ProductRepository productRepository;

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
    public List<MainListResponseDTO> getBestAndNewList(MemberPageDTO pageDTO, Principal principal) {
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
    public PagingListDTO<MainListResponseDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal) {
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
}

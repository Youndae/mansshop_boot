package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final ProductRepository productRepository;

    private final PrincipalService principalService;


    @Override
    public ResponseListDTO<MainListDTO> getBestAndNewList(MemberPageDTO pageDTO, Principal principal) {
        String uid = principalService.getPrincipalUid(principal);

        List<MainListDTO> listDto = productRepository.findListDefault(pageDTO);

        return new ResponseListDTO<MainListDTO>(listDto, new UserStatusDTO(uid));
    }

    @Override
    public PagingResponseDTO<MainListDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal) {
        String uid = principalService.getPrincipalUid(principal);

        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.mainProductAmount()
                                            , Sort.by("createdAt").descending()
        );

        Page<MainListDTO> dto = productRepository.findListPageable(pageDTO, pageable);

        return new PagingResponseDTO<>(dto, uid);
    }
}

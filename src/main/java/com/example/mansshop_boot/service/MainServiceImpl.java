package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final ProductRepository productRepository;

    private final PrincipalService principalService;


    @Override
    public ResponseListDTO<MainListResponseDTO> getBestAndNewList(MemberPageDTO pageDTO, Principal principal) {
        String uid = principalService.getNicknameByPrincipal(principal);

        List<MainListDTO> listDto = productRepository.findListDefault(pageDTO);
        List<MainListResponseDTO> responseDTO = mainListDataMapping(listDto);

        return new ResponseListDTO<MainListResponseDTO>(responseDTO, new UserStatusDTO(uid));
    }

    @Override
    public PagingResponseDTO<MainListResponseDTO> getClassificationAndSearchList(MemberPageDTO pageDTO, Principal principal) {
        String uid = principalService.getNicknameByPrincipal(principal);

        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                                            , pageDTO.mainProductAmount()
                                            , Sort.by("createdAt").descending()
        );

        Page<MainListDTO> dto = productRepository.findListPageable(pageDTO, pageable);
        dto.getContent().forEach(System.out::println);
        List<MainListResponseDTO> responseDTO = mainListDataMapping(dto.getContent());

        return new PagingResponseDTO<>(
                responseDTO
                , dto.isEmpty()
                , dto.getNumber()
                , dto.getTotalPages()
                , uid
        );
    }

    public List<MainListResponseDTO> mainListDataMapping(List<MainListDTO> dto) {
        List<MainListResponseDTO> response = new ArrayList<>();
        dto.forEach(entity -> response.add(new MainListResponseDTO(entity)));

        return response;
    }
}

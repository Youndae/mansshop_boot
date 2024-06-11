package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MemberQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberQnAListDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberQnARepositoryTest {

    @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Test
    @DisplayName("MemberQnAList 조회")
    void getList() {
        Pageable pageable = PageRequest.of(0
                                        , 20
                                        , Sort.by("createdAt").descending());
        String userId = "coco";

        Page<MemberQnAListDTO> dto = memberQnARepository.findAllByUserId(userId, pageable);

        String nickname = "코코에용";

        PagingResponseDTO<MemberQnAListDTO> response = new PagingResponseDTO<>(dto, nickname);

        System.out.println(response);
    }

    @Test
    @DisplayName("MemberQnADetail 조회. qnaId = 1")
    void getDetail() {
        String userId = "coco";
        long memberQnAId = 1L;

        MemberQnADTO qnaDTO = memberQnARepository.findByIdAndUserId(memberQnAId, userId);
        List<MemberQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(memberQnAId);

        String nickname = "코코에용";

        MemberQnADetailDTO response = new MemberQnADetailDTO(qnaDTO, replyDTOList, nickname);

        System.out.println(response);
    }
}
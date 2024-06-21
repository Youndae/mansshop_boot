package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.qna.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.MyPageQnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.ProductQnADetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.ProductQnAListDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
class ProductQnARepositoryTest {

    @Autowired
    private ProductQnARepository productQnARepository;

    @Autowired
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Test
    @DisplayName("ProductQnAList 조회")
    void getList() {
        Pageable pageable = PageRequest.of(0
                                        , 20
                                        , Sort.by("id").descending());

        Page<ProductQnAListDTO> dto = productQnARepository.findByUserId("coco", pageable);

        dto.getContent().forEach(System.out::println);
    }

    @Test
    @DisplayName("ProductDetail 조회. qnaId = 1")
    void getDetail() {
        String userId = "coco";
        long productQnAId = 1L;

        MyPageProductQnADTO dto = productQnARepository.findByQnAId(productQnAId);

        List<MyPageQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(productQnAId);

        String nickname = "코코에용";

        ProductQnADetailDTO response = new ProductQnADetailDTO(dto, replyDTOList, nickname);

        System.out.println(response);
    }
}
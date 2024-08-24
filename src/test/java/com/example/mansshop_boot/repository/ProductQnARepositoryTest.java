package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductQnARepositoryTest {

    /*@Autowired
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
    @DisplayName("ProductDetail 조회. qnaId = 1522")
    void getDetail() {
        String userId = "coco";
        long productQnAId = 1522L;

        MyPageProductQnADTO dto = productQnARepository.findByQnAId(productQnAId);

        List<MyPageQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(productQnAId);

        ProductQnADetailDTO response = new ProductQnADetailDTO(dto, replyDTOList);

        System.out.println(response);
    }*/
}
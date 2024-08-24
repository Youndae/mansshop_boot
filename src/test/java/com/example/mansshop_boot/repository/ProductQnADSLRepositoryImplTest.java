package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductQnADSLRepositoryImplTest {

    /*@Autowired
    private ProductQnARepository productQnARepository;

    @Test
    void listTest() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();

        Pageable qnaPageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.qnaAmount()
                , Sort.by("productQnAGroupId").descending()
                        .and(Sort.by("productQnAStep").ascending()));

        String productId = "BAGS20210629134401";

        Page<ProductQnADTO> dto = productQnARepository.findByProductId(productId, qnaPageable);

        System.out.println(dto.getContent());
    }*/
}
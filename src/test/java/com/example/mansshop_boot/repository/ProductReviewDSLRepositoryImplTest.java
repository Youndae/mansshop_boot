package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductReviewDSLRepositoryImplTest {

    /*@Autowired
    private ProductReviewRepository productReviewRepository;

    @Test
    void listTest() {
        ProductDetailPageDTO pageDTO = new ProductDetailPageDTO();
        Pageable reviewPageable = PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.reviewAmount()
                , Sort.by("reviewGroupId").descending()
                        .and(Sort.by("reviewStep").ascending()));

        String productId = "BAGS20210629134401";

        Page<ProductReviewDTO> dto = productReviewRepository.findByProductId(productId, reviewPageable);

        System.out.println(dto.getContent());
    }*/
}
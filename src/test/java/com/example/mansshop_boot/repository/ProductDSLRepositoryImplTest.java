package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductDSLRepositoryImplTest {

    /*@Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("best list 조회")
    void bestList() {
        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                .pageNum(1)
                .keyword(null)
                .classification("BEST")
                .build();

        System.out.println(productRepository.findListDefault(memberPageDTO));
    }

    @Test
    @DisplayName("New list 조회")
    void newList() {
        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                .pageNum(1)
                .keyword(null)
                .classification("NEW")
                .build();

        System.out.println(productRepository.findListDefault(memberPageDTO));
    }

    @Test
    @DisplayName("Search keyword=토드백")
    void searchList() {
        MemberPageDTO memberPageDTO = MemberPageDTO.builder()
                .pageNum(1)
                .keyword("토드백")
                .classification(null)
                .build();

        Pageable pageable =  PageRequest.of(memberPageDTO.pageNum() - 1
                , memberPageDTO.mainProductAmount()
                , Sort.by("createdAt").descending()
        );

        Page<MainListDTO> result = productRepository.findListPageable(memberPageDTO, pageable);

        System.out.println(result.getContent());
    }

    @Test
    @DisplayName("detail 조회")
    void detailTest() {
        String productId = "BAGS20210629134401";

        Product product = productRepository.findById(productId).orElse(null);

        Assertions.assertNotNull(product);
    }

    @Test
    @DisplayName("adminProductList Test")
    void adminProductListTest() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("createdAt").descending());

        *//*Page<AdminProductListDTO> dto = productRepository.findAdminProductList(pageDTO, pageable);

        System.out.println(dto.getContent());*//*
    }

    @Test
    @DisplayName("베스트 상품 조회")
    void bestProduct() {
        MemberPageDTO pageDTO = MemberPageDTO.builder()
                .pageNum(1)
                .keyword(null)
                .classification("BEST")
                .build();

        List<MainListDTO> listDto = productRepository.findListDefault(pageDTO);

        listDto.forEach(System.out::println);
    }

    @Test
    @DisplayName("OUTER 상품 검색")
    void outerProduct() {
        MemberPageDTO pageDTO = MemberPageDTO.builder()
                .pageNum(1)
                .keyword(null)
                .classification("OUTER")
                .build();

        Pageable pageable =  PageRequest.of(pageDTO.pageNum() - 1
                , pageDTO.mainProductAmount()
                , Sort.by("createdAt").descending()
        );

        Page<MainListDTO> dto = productRepository.findListPageable(pageDTO, pageable);

        System.out.println("size : " + dto.getContent().size());
    }*/
}
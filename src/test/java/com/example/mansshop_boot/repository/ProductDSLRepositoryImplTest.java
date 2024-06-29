package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminProductListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListDTO;
import com.example.mansshop_boot.domain.dto.main.MainListResponseDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.MemberPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ProductDSLRepositoryImplTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

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

        /*Page<AdminProductListDTO> dto = productRepository.findAdminProductList(pageDTO, pageable);

        System.out.println(dto.getContent());*/
    }


    @Test
    @DisplayName("adminUpdateProduct test")
    void adminUpdateProductTest() {
        String productId = "SHOES20240223151514";

        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);

        ProductOption option1 = ProductOption.builder()
                                    .id(128L)
                                    .product(product)
                                    .size("L")
                                    .color("black2")
                                    .stock(80)
                                    .build();

        ProductOption option2 = ProductOption.builder()
                .id(127L)
                .product(product)
                .size("M")
                .color("white2")
                .stock(40)
                .build();

        /*ProductOption option3 = ProductOption.builder()
                .size("S")
                .color("red")
                .stock(30)
                .build();*/

        ProductOption option4 = ProductOption.builder()
                .id(125L)
                .product(product)
                .size("XS")
                .color("yellow2")
                .stock(20)
                .build();

        List<ProductOption> optionList = new ArrayList<>();
        optionList.add(option1);
        optionList.add(option2);
        optionList.add(option4);


        ProductOption option = ProductOption.builder()
                .id(null)
                .product(product)
                .size("L")
                .color("testColor")
                .stock(80)
                .build();

//        productOptionRepository.saveAll(optionList);

        productOptionRepository.save(option);

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
    }
}
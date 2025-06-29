package com.example.mansshop_boot.service.integration.admin;

import com.example.mansshop_boot.Fixture.AdminPageDTOFixture;
import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.util.PaginationUtils;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.PatchOptionDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.service.FileServiceImpl;
import com.example.mansshop_boot.service.admin.AdminProductServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@Transactional
public class AdminProductServiceIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    @Autowired
    private ProductInfoImageRepository productInfoImageRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private AdminProductServiceImpl adminProductService;

    @MockBean
    private FileServiceImpl fileService;

    @Autowired
    private EntityManager entityManager;

    private List<Product> productList;

    private List<Classification> classificationList;

    @BeforeEach
    void init() {
        classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        productList = ProductFixture.createSaveProductList(30, classificationList.get(0));
        List<ProductOption> optionFixture = productList.stream().flatMap(v -> v.getProductOptions().stream()).toList();
        List<ProductThumbnail> thumbnailFixture = productList.stream().flatMap(v -> v.getProductThumbnails().stream()).toList();
        List<ProductInfoImage> infoImageFixture = productList.stream().flatMap(v -> v.getProductInfoImages().stream()).toList();
        productRepository.saveAll(productList);
        productOptionRepository.saveAll(optionFixture);
        productThumbnailRepository.saveAll(thumbnailFixture);
        productInfoImageRepository.saveAll(infoImageFixture);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName(value = "상품 목록 조회")
    void getProductList() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(pageDTO.amount(), result.content().size());
        assertEquals(productList.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 목록 조회. 데이터가 없는 경우")
    void getProductListEmpty() {
        productOptionRepository.deleteAll();
        productThumbnailRepository.deleteAll();
        productInfoImageRepository.deleteAll();
        productRepository.deleteAll();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 목록 조회. 상품명 검색")
    void getProductListSearchProductName() {
        Product product = productList.get(0);
        AdminPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminPageDTO(product.getProductName(), 1);
        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.content().size());
        assertEquals(1, result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());

        AdminProductListDTO resultContent = result.content().get(0);

        assertEquals(product.getId(), resultContent.productId());
        assertEquals(product.getClassification().getId(), resultContent.classification());
        assertEquals(product.getProductName(), resultContent.productName());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회")
    void getClassification() {
        List<String> result = assertDoesNotThrow(() -> adminProductService.getClassification());

        assertFalse(result.isEmpty());

        classificationList.forEach(v -> assertTrue(result.contains(v.getId())));
    }

    @Test
    @DisplayName(value = "상품 상세 데이터 조회")
    void getProductDetail() {
        Product product = productList.get(0);
        List<String> thumbnailNameList = product.getProductThumbnails().stream().map(ProductThumbnail::getImageName).toList();
        List<String> infoImageNameList = product.getProductInfoImages().stream().map(ProductInfoImage::getImageName).toList();
        AdminProductDetailDTO result = assertDoesNotThrow(() -> adminProductService.getProductDetail(product.getId()));

        assertNotNull(result);
        assertEquals(product.getId(), result.productId());
        assertEquals(product.getClassification().getId(), result.classification());
        assertEquals(product.getThumbnail(), result.firstThumbnail());
        assertEquals(product.getProductPrice(), result.price());
        assertEquals(product.isOpen(), result.isOpen());
        assertEquals(product.getProductSalesQuantity(), result.sales());
        assertEquals(product.getProductDiscount(), result.discount());
        assertEquals(product.getProductOptions().size(), result.optionList().size());
        thumbnailNameList.forEach(v -> assertTrue(result.thumbnailList().contains(v)));
        infoImageNameList.forEach(v -> assertTrue(result.infoImageList().contains(v)));
    }

    @Test
    @DisplayName(value = "상품 상세 데이터 조회. 데이터가 없는 경우")
    void getProductDetailNotFound() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.getProductDetail("emptyId")
        );
    }

    @Test
    @DisplayName(value = "상품 수정 데이터 조회")
    void getPatchProductData() {
        Product product = productList.get(0);
        List<String> thumbnailNameList = product.getProductThumbnails().stream().map(ProductThumbnail::getImageName).toList();
        List<String> infoImageNameList = product.getProductInfoImages().stream().map(ProductInfoImage::getImageName).toList();
        AdminProductPatchDataDTO result = assertDoesNotThrow(() -> adminProductService.getPatchProductData(product.getId()));

        assertNotNull(result);
        assertEquals(product.getId(), result.productId());
        assertEquals(product.getClassification().getId(), result.classificationId());
        assertEquals(product.getThumbnail(), result.firstThumbnail());
        assertEquals(product.getProductPrice(), result.price());
        assertEquals(product.isOpen(), result.isOpen());
        assertEquals(product.getProductDiscount(), result.discount());
        assertEquals(product.getProductOptions().size(), result.optionList().size());
        thumbnailNameList.forEach(v -> assertTrue(result.thumbnailList().contains(v)));
        infoImageNameList.forEach(v -> assertTrue(result.infoImageList().contains(v)));
        classificationList.forEach(v -> assertTrue(result.classificationList().contains(v.getId())));
    }

    @Test
    @DisplayName(value = "상품 수정 데이터 조회. 데이터가 없는 경우")
    void getPatchProductDataNotFound() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.getProductDetail("emptyId")
        );
    }

    private MockMultipartFile createMockMultipartFile(String fieldName, String fileName) {
        return new MockMultipartFile(
                fieldName,
                fileName + ".jpg",
                "image/jpeg",
                fileName.getBytes()
        );
    }

    private List<MockMultipartFile> createMockMultipartFileList(String type, int count) {
        List<MockMultipartFile> result = new ArrayList<>();
        String fieldName = "thumbnail";
        String fileName = "thumb";

        if(type.equals("info")) {
            fieldName = "infoImage";
            fileName = "info";
        }

        for(int i = 0; i < count; i++) {
            fileName = fileName + i;
            result.add(createMockMultipartFile(fieldName, fileName));
        }

        return result;
    }

    @Test
    @DisplayName(value = "상품 등록")
    void postProduct() {
        List<PatchOptionDTO> optionList = IntStream.range(0, 2)
                                                    .mapToObj(v -> new PatchOptionDTO(
                                                            0L,
                                                            "postSize" + v,
                                                            "postColor" + v,
                                                            100,
                                                            true
                                                    ))
                                                    .toList();
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                optionList
        );
        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumbnail,
                null,
                thumbnail,
                null,
                infoImage,
                null
        );

        try {
            given(fileService.imageInsert(any(MultipartFile.class)))
                    .willReturn("saved-first-thumb.jpg");
        }catch (Exception e) {
            e.printStackTrace();
        }

        String result = assertDoesNotThrow(() -> adminProductService.postProduct(patchDTO, imageDTO));

        assertNotNull(result);

        Product saveProduct = productRepository.findById(result).orElse(null);

        assertNotNull(saveProduct);
        assertEquals(patchDTO.getProductName(), saveProduct.getProductName());
        assertEquals(patchDTO.getClassification(), saveProduct.getClassification().getId());
        assertEquals(patchDTO.getPrice(), saveProduct.getProductPrice());
        assertEquals(patchDTO.getIsOpen(), saveProduct.isOpen());
        assertEquals(patchDTO.getDiscount(), saveProduct.getProductDiscount());
        assertEquals(patchDTO.getOptionList().size(), saveProduct.getProductOptions().size());
        assertEquals(imageDTO.getThumbnail().size(), saveProduct.getProductThumbnails().size());
        assertEquals(imageDTO.getInfoImage().size(), saveProduct.getProductInfoImages().size());
    }

    @Test
    @DisplayName(value = "상품 등록. 대표 썸네일이 없는 경우")
    void postProductFirstThumbnailIsNull() {
        List<PatchOptionDTO> optionList = IntStream.range(0, 2)
                .mapToObj(v -> new PatchOptionDTO(
                        0L,
                        "postSize" + v,
                        "postColor" + v,
                        100,
                        true
                ))
                .toList();
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                optionList
        );
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                null,
                null,
                thumbnail,
                null,
                infoImage,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.postProduct(patchDTO, imageDTO)
        );

        try {
            verify(fileService, never()).imageInsert(any(MultipartFile.class));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName(value = "상품 수정 처리")
    void patchProduct() {
        Product productFixture = productList.get(0);
        List<Long> deleteOptionList = List.of(productFixture.getProductOptions().get(0).getId());
        List<PatchOptionDTO> addOptionList = List.of(new PatchOptionDTO(
                        0L,
                        "newPostSize",
                        "newPostColor",
                        100,
                        true
                )
        );
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                addOptionList
        );

        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumbnail,
                "deleteFirstThumbnailName",
                thumbnail,
                List.of("deleteThumbnailName"),
                infoImage,
                List.of("deleteInfoImageName")
        );

        try {
            given(fileService.imageInsert(any(MultipartFile.class)))
                    .willReturn("saved-first-thumb.jpg");

            willDoNothing().given(fileService).deleteImage(anyString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        String result = assertDoesNotThrow(() -> adminProductService.patchProduct(productFixture.getId(), deleteOptionList, patchDTO, imageDTO));

        verify(fileService, times(3)).deleteImage(anyString());

        assertNotNull(result);
        assertEquals(productFixture.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 수정 처리. 추가하는 대표 썸네일은 있으나, 삭제하는 대표 썸네일이 없는 경우")
    void patchProductDeleteFirstThumbnailException() {
        Product productFixture = productList.get(0);
        List<Long> deleteOptionList = List.of(productFixture.getProductOptions().get(0).getId());
        List<PatchOptionDTO> addOptionList = List.of(new PatchOptionDTO(
                        0L,
                        "newPostSize",
                        "newPostColor",
                        100,
                        true
                )
        );
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                addOptionList
        );

        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumbnail,
                null,
                thumbnail,
                List.of("deleteThumbnailName"),
                infoImage,
                List.of("deleteInfoImageName")
        );

        try {
            given(fileService.imageInsert(any(MultipartFile.class)))
                    .willReturn("saved-first-thumb.jpg");

            willDoNothing().given(fileService).deleteImage(anyString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.patchProduct(productFixture.getId(), deleteOptionList, patchDTO, imageDTO)
        );

        verify(fileService, times(6)).deleteImage(anyString());
    }

    @Test
    @DisplayName(value = "상품 수정 처리. 추가하는 대표 썸네일은 없으나, 삭제하는 대표 썸네일이 있는 경우")
    void patchProductFirstThumbnailException() {
        Product productFixture = productList.get(0);
        List<Long> deleteOptionList = List.of(productFixture.getProductOptions().get(0).getId());
        List<PatchOptionDTO> addOptionList = List.of(new PatchOptionDTO(
                        0L,
                        "newPostSize",
                        "newPostColor",
                        100,
                        true
                )
        );
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                addOptionList
        );

        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                null,
                "deleteFirstThumbnailName",
                thumbnail,
                List.of("deleteThumbnailName"),
                infoImage,
                List.of("deleteInfoImageName")
        );

        try {
            given(fileService.imageInsert(any(MultipartFile.class)))
                    .willReturn("saved-first-thumb.jpg");

            willDoNothing().given(fileService).deleteImage(anyString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.patchProduct(productFixture.getId(), deleteOptionList, patchDTO, imageDTO)
        );

        verify(fileService, times(6)).deleteImage(anyString());
    }

    @Test
    @DisplayName(value = "상품 수정 처리. 상품 데이터가 없는 경우(아이디가 잘못 된 경우)")
    void patchProductNotFound() {
        Product productFixture = productList.get(0);
        List<Long> deleteOptionList = List.of(productFixture.getProductOptions().get(0).getId());
        List<PatchOptionDTO> addOptionList = List.of(new PatchOptionDTO(
                        0L,
                        "newPostSize",
                        "newPostColor",
                        100,
                        true
                )
        );
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                addOptionList
        );

        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumbnail,
                "deleteFirstThumbnailName",
                thumbnail,
                List.of("deleteThumbnailName"),
                infoImage,
                List.of("deleteInfoImageName")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.patchProduct("noneProductId", deleteOptionList, patchDTO, imageDTO)
        );

        try {
            verify(fileService, never()).deleteImage(anyString());
            verify(fileService, never()).imageInsert(any(MultipartFile.class));
        }catch (Exception e) {
            e.printStackTrace();
            fail("verify fail");
        }
    }

    @Test
    @DisplayName(value = "상품 수정 처리. 상품 분류 데이터가 없는 경우 (상품 분류 아이디가 잘못 된 경우)")
    void patchProductClassificationNotFound() {
        Product productFixture = productList.get(0);
        List<Long> deleteOptionList = List.of(productFixture.getProductOptions().get(0).getId());
        List<PatchOptionDTO> addOptionList = List.of(new PatchOptionDTO(
                        0L,
                        "newPostSize",
                        "newPostColor",
                        100,
                        true
                )
        );
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                "postProduct",
                "noneClassification",
                20000,
                true,
                0,
                addOptionList
        );

        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        List<MultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
        List<MultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumbnail,
                "deleteFirstThumbnailName",
                thumbnail,
                List.of("deleteThumbnailName"),
                infoImage,
                List.of("deleteInfoImageName")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> adminProductService.patchProduct(productFixture.getId(), deleteOptionList, patchDTO, imageDTO)
        );

        try {
            verify(fileService, never()).deleteImage(anyString());
            verify(fileService, never()).imageInsert(any(MultipartFile.class));
        }catch (Exception e) {
            e.printStackTrace();
            fail("verify fail");
        }
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 반환")
    void getProductStock() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(productList.size(), pageDTO.amount());
        List<Product> fixture = productList.stream()
                                        .sorted(
                                                Comparator.comparingInt(product ->
                                                    product.getProductOptions().stream()
                                                            .mapToInt(ProductOption::getStock)
                                                            .sum()
                                                )
                                        )
                                        .limit(pageDTO.amount())
                                        .toList();

        PagingListDTO<AdminProductStockDTO> result = assertDoesNotThrow(() -> adminProductService.getProductStock(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(pageDTO.amount(), result.content().size());
        assertEquals(productList.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());
        assertFalse(result.pagingData().isEmpty());

        for(int i = 0; i < fixture.size(); i++) {
            Product product = fixture.get(i);
            AdminProductStockDTO resultDTO = result.content().get(i);

            assertEquals(product.getProductName(), resultDTO.productName());
            assertEquals(product.getProductOptions().stream().mapToInt(ProductOption::getStock).sum(), resultDTO.totalStock());
        }
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 반환. 상품명 기반 검색")
    void getProductStockSearchProductName() {
        Product fixture = productList.get(0);
        AdminPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminPageDTO(fixture.getProductName(), 1);
        int totalStock = fixture.getProductOptions().stream().mapToInt(ProductOption::getStock).sum();

        PagingListDTO<AdminProductStockDTO> result = assertDoesNotThrow(() -> adminProductService.getProductStock(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.content().size());
        assertEquals(1, result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());
        assertFalse(result.pagingData().isEmpty());

        AdminProductStockDTO resultDTO = result.content().get(0);

        assertEquals(fixture.getProductName(), resultDTO.productName());
        assertEquals(totalStock, resultDTO.totalStock());
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 반환. 상품명 기반 검색. 데이터가 없는 경우")
    void getProductStockSearchProductNameEmpty() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminPageDTO("noneProductName", 1);

        PagingListDTO<AdminProductStockDTO> result = assertDoesNotThrow(() -> adminProductService.getProductStock(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
        assertTrue(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "할인 중인 상품 목록 조회.")
    void getDiscountProduct() {
        List<Product> fixtureList = productList.stream().filter(v -> v.getProductDiscount() > 0).toList();
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.amount());

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(fixtureList.size(), result.pagingData().getTotalElements());
        assertEquals(totalPages, result.pagingData().getTotalPages());

        result.content().forEach(v -> assertTrue(v.discount() > 0));
    }

    @Test
    @DisplayName(value = "할인 중인 상품 목록 조회. 상품명 기반 검색")
    void getDiscountProductSearchProductName() {
        List<Product> fixtureList = productList.stream().filter(v -> v.getProductDiscount() > 0).toList();
        Product fixture = fixtureList.get(0);
        AdminPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminPageDTO(fixture.getProductName(), 1);

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(1, result.content().size());
        assertEquals(1, result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());

        AdminDiscountResponseDTO resultDTO = result.content().get(0);
        assertEquals(fixture.getId(), resultDTO.productId());
        assertEquals(fixture.getProductName(), resultDTO.productName());
        assertEquals(fixture.getProductDiscount(), resultDTO.discount());
    }

    @Test
    @DisplayName(value = "할인 중인 상품 목록 조회. 상품명 기반 검색. 데이터가 없는 경우")
    void getDiscountProductSearchProductNameEmpty() {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createSearchAdminPageDTO("noneProductName", 1);

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertTrue(result.pagingData().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회.")
    void getSelectDiscountProduct() {
        List<AdminDiscountProductDTO> result = assertDoesNotThrow(() -> adminProductService.getSelectDiscountProduct(classificationList.get(0).getId()));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(productList.size(), result.size());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회. 데이터가 없는 경우")
    void getSelectDiscountProductEmpty() {
        List<AdminDiscountProductDTO> result = assertDoesNotThrow(() -> adminProductService.getSelectDiscountProduct(classificationList.get(1).getId()));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 할인율 수정. 단일 수정")
    void patchDiscountOneProduct() {
        Product fixture = productList.get(0);
        List<String> productIdList = List.of(fixture.getId());
        int discount = fixture.getProductDiscount() + 10;

        AdminDiscountPatchDTO patchDTO = new AdminDiscountPatchDTO(productIdList, discount);

        String result = assertDoesNotThrow(() -> adminProductService.patchDiscountProduct(patchDTO));

        entityManager.flush();
        entityManager.clear();

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        Product resultEntity = productRepository.findById(fixture.getId()).orElseThrow(IllegalArgumentException::new);

        assertEquals(discount, resultEntity.getProductDiscount());
    }

    @Test
    @DisplayName(value = "상품 할인율 수정. 다중 수정")
    void patchDiscountProduct() {
        List<Product> fixtureList = List.of(productList.get(0), productList.get(1));
        List<String> productIdList = fixtureList.stream().map(Product::getId).toList();
        int discount = 60;
        AdminDiscountPatchDTO patchDTO = new AdminDiscountPatchDTO(productIdList, discount);

        String result = assertDoesNotThrow(() -> adminProductService.patchDiscountProduct(patchDTO));

        assertNotNull(result);
        assertEquals(Result.OK.getResultKey(), result);

        entityManager.flush();
        entityManager.clear();

        Product resultEntity1 = productRepository.findById(fixtureList.get(0).getId()).orElseThrow(IllegalArgumentException::new);
        Product resultEntity2 = productRepository.findById(fixtureList.get(1).getId()).orElseThrow(IllegalArgumentException::new);

        assertEquals(discount, resultEntity1.getProductDiscount());
        assertEquals(discount, resultEntity2.getProductDiscount());
    }
}

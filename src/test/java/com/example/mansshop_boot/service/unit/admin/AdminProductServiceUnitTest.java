package com.example.mansshop_boot.service.unit.admin;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.domain.dto.admin.business.*;
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
import com.example.mansshop_boot.service.unit.fixture.ProductUnitFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class AdminProductServiceUnitTest {

    @InjectMocks
    @Spy
    private AdminProductServiceImpl adminProductService;

    @Mock
    private FileServiceImpl fileServiceMock;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClassificationRepository classificationRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductThumbnailRepository productThumbnailRepository;

    @Mock
    private ProductInfoImageRepository productInfoImageRepository;

    private MockMultipartFile createMockMultipartFile(String imageName) {
        return new MockMultipartFile("fieldName", imageName, "image/jpeg", new byte[]{1, 2, 3, 4, 5});
    }

    @Test
    @DisplayName(value = "상품 목록 조회")
    void getProductList() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(30);
        List<AdminProductListDTO> mockData = productFixtureList.stream()
                .map(v -> {
                    int stock = v.getProductOptions().stream().mapToInt(ProductOption::getStock).sum();

                    return new AdminProductListDTO(v.getId(),
                            v.getClassification().getId(),
                            v.getProductName(),
                            stock,
                            (long) v.getProductOptions().size(),
                            v.getProductPrice()
                    );
                })
                .limit(pageDTO.amount())
                .toList();

        when(productRepository.findAdminProductList(pageDTO))
                .thenReturn(mockData);
        when(productRepository.findAdminProductListCount(pageDTO)).thenReturn((long) productFixtureList.size());

        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(2, result.pagingData().getTotalPages());
        assertEquals(productFixtureList.size(), result.pagingData().getTotalElements());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(1, result.pagingData().getNumber());
    }

    @Test
    @DisplayName(value = "상품 목록 조회. 상품이 없는 경우")
    void getProductListEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);

        when(productRepository.findAdminProductList(pageDTO))
                .thenReturn(List.of());
        when(productRepository.findAdminProductListCount(pageDTO)).thenReturn(0L);

        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalPages());
        assertEquals(0, result.pagingData().getTotalElements());
        assertTrue(result.pagingData().isEmpty());
        assertEquals(1, result.pagingData().getNumber());
    }

    @Test
    @DisplayName(value = "상품 목록 조회")
    void getProductListSearch() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Product productFixture = ProductFixture.createDefaultProductByOUTER(1).get(0);
        int stock = productFixture.getProductOptions().stream().mapToInt(ProductOption::getStock).sum();
        List<AdminProductListDTO> mockData = List.of(new AdminProductListDTO(productFixture.getId(),
                productFixture.getClassification().getId(),
                productFixture.getProductName(),
                stock,
                (long) productFixture.getProductOptions().size(),
                productFixture.getProductPrice())
        );

        when(productRepository.findAdminProductList(pageDTO))
                .thenReturn(mockData);
        when(productRepository.findAdminProductListCount(pageDTO)).thenReturn((long) mockData.size());

        PagingListDTO<AdminProductListDTO> result = assertDoesNotThrow(() -> adminProductService.getProductList(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.pagingData().getTotalPages());
        assertEquals(mockData.size(), result.pagingData().getTotalElements());
        assertFalse(result.pagingData().isEmpty());
        assertEquals(1, result.pagingData().getNumber());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회")
    void getClassification() {
        List<Classification> classificationFixtureList = ClassificationFixture.createClassification()
                .stream()
                .sorted(Comparator.comparing(Classification::getClassificationStep))
                .toList();

        when(classificationRepository.findAll(Sort.by("classificationStep").ascending()))
                .thenReturn(classificationFixtureList);

        List<String> result = assertDoesNotThrow(() -> adminProductService.getClassification());

        assertFalse(result.isEmpty());
        assertEquals(classificationFixtureList.size(), result.size());

        for(int i = 0; i < result.size(); i++) {
            Classification classification = classificationFixtureList.get(i);
            String resultData = result.get(i);

            assertEquals(classification.getId(), resultData);
        }
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회")
    void getProductDetail() {
        Product productFixture = ProductFixture.createDefaultProductByOUTER(1).get(0);
        List<AdminProductOptionDTO> optionFixture = productFixture.getProductOptions()
                .stream()
                .map(v ->
                        new AdminProductOptionDTO(v.getId(),
                                v.getSize(),
                                v.getColor(),
                                v.getStock(),
                                v.isOpen()
                        )
                )
                .toList();
        List<String> thumbnailFixture = productFixture.getProductThumbnails()
                .stream()
                .map(ProductThumbnail::getImageName)
                .toList();
        List<String> infoImageFixture = productFixture.getProductInfoImages()
                .stream()
                .map(ProductInfoImage::getImageName)
                .toList();
        when(productRepository.findById(productFixture.getId()))
                .thenReturn(Optional.of(productFixture));
        when(productOptionRepository.findAllByProductId(productFixture.getId()))
                .thenReturn(optionFixture);
        when(productThumbnailRepository.findByProductId(productFixture.getId()))
                .thenReturn(thumbnailFixture);
        when(productInfoImageRepository.findByProductId(productFixture.getId()))
                .thenReturn(infoImageFixture);

        AdminProductDetailDTO result = assertDoesNotThrow(() -> adminProductService.getProductDetail(productFixture.getId()));

        assertNotNull(result);
        assertFalse(result.optionList().isEmpty());
        assertFalse(result.thumbnailList().isEmpty());
        assertFalse(result.infoImageList().isEmpty());
        assertEquals(productFixture.getId(), result.productId());
        assertEquals(productFixture.getClassification().getId(), result.classification());
        assertEquals(productFixture.getThumbnail(), result.firstThumbnail());
        assertEquals(optionFixture.size(), result.optionList().size());
        assertEquals(thumbnailFixture.size(), result.thumbnailList().size());
        assertEquals(infoImageFixture.size(), result.infoImageList().size());
        assertEquals(productFixture.getProductPrice(), result.price());
        assertEquals(productFixture.isOpen(), result.isOpen());
        assertEquals(productFixture.getProductSalesQuantity(), result.sales());
        assertEquals(productFixture.getProductDiscount(), result.discount());
    }

    @Test
    @DisplayName(value = "존재하지 않는 상품 정보를 검색하는 경우")
    void getProductDetailEmpty() {

        when(productRepository.findById("fakeProductId"))
                .thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> adminProductService.getProductDetail("fakeProductId"));
    }

    @Test
    @DisplayName(value = "상품 추가 요청")
    void postProduct() {
        Product product = ProductFixture.createDefaultProductByOUTER(1).get(0);
        MockMultipartFile firstThumb = createMockMultipartFile(product.getThumbnail());
        List<MockMultipartFile> thumbnailMock = product.getProductThumbnails()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> thumbnails = new ArrayList<>(thumbnailMock);
        List<MockMultipartFile> infoImagesMock = product.getProductInfoImages()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> infoImages = new ArrayList<>(infoImagesMock);
        List<PatchOptionDTO> optionDTOList = product.getProductOptions()
                .stream()
                .map(v -> new PatchOptionDTO(0L, v.getSize(), v.getColor(), v.getStock(), v.isOpen()))
                .toList();
        AdminProductPatchDTO postDTO = new AdminProductPatchDTO(
                product.getProductName(),
                product.getClassification().getId(),
                product.getProductPrice(),
                product.isOpen(),
                product.getProductDiscount(),
                optionDTOList
        );
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumb,
                null,
                thumbnails,
                null,
                infoImages,
                null
        );

        try {
            when(fileServiceMock.imageInsert(any(MultipartFile.class)))
                    .thenReturn("mockFile.jpg");
        }catch (Exception e) {
            fail("Mocking imageInsert failed : " + e.getMessage());
        }

        when(productRepository.save(any()))
                .thenReturn(product);
        when(productOptionRepository.saveAll(any()))
                .thenReturn(product.getProductOptions());
        when(productThumbnailRepository.saveAll(any()))
                .thenReturn(product.getProductThumbnails());
        when(productInfoImageRepository.saveAll(any()))
                .thenReturn(product.getProductInfoImages());

        String result = assertDoesNotThrow(() -> adminProductService.postProduct(postDTO, imageDTO));

        assertEquals(product.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 추가 요청. 대표 썸네일이 없는 경우 IllegalArgumentException 발생")
    void postProductEmptyThumbnail() {
        Product product = ProductFixture.createDefaultProductByOUTER(1).get(0);
        List<MockMultipartFile> thumbnailMock = product.getProductThumbnails()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> thumbnails = new ArrayList<>(thumbnailMock);
        List<MockMultipartFile> infoImagesMock = product.getProductInfoImages()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> infoImages = new ArrayList<>(infoImagesMock);
        List<PatchOptionDTO> optionDTOList = product.getProductOptions()
                .stream()
                .map(v -> new PatchOptionDTO(0L, v.getSize(), v.getColor(), v.getStock(), v.isOpen()))
                .toList();
        AdminProductPatchDTO postDTO = new AdminProductPatchDTO(
                product.getProductName(),
                product.getClassification().getId(),
                product.getProductPrice(),
                product.isOpen(),
                product.getProductDiscount(),
                optionDTOList
        );
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                null,
                null,
                thumbnails,
                null,
                infoImages,
                null
        );

        assertThrows(IllegalArgumentException.class,
                () -> adminProductService.postProduct(postDTO, imageDTO)
        );

        verify(fileServiceMock, never()).deleteImage(any());
        verify(productRepository, never()).save(any());
        verify(productOptionRepository, never()).saveAll(any());
        verify(productThumbnailRepository, never()).saveAll(any());
        verify(productInfoImageRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName(value = "상품 수정을 위한 데이터 조회")
    void getPatchProductData() {
        List<Classification> classificationFixture = ClassificationFixture.createClassification()
                .stream()
                .sorted(Comparator.comparing(Classification::getClassificationStep).reversed())
                .toList();
        Product productFixture = ProductFixture.createDefaultProductByOUTER(1).get(0);
        List<AdminProductOptionDTO> optionFixture = productFixture.getProductOptions()
                .stream()
                .map(v ->
                        new AdminProductOptionDTO(v.getId(),
                                v.getSize(),
                                v.getColor(),
                                v.getStock(),
                                v.isOpen()
                        )
                )
                .toList();
        List<String> thumbnailFixture = productFixture.getProductThumbnails()
                .stream()
                .map(ProductThumbnail::getImageName)
                .toList();
        List<String> infoImageFixture = productFixture.getProductInfoImages()
                .stream()
                .map(ProductInfoImage::getImageName)
                .toList();

        when(classificationRepository.findAll(Sort.by("classificationStep").descending()))
                .thenReturn(classificationFixture);
        when(productRepository.findById(productFixture.getId()))
                .thenReturn(Optional.of(productFixture));
        when(productOptionRepository.findAllByProductId(productFixture.getId()))
                .thenReturn(optionFixture);
        when(productThumbnailRepository.findByProductId(productFixture.getId()))
                .thenReturn(thumbnailFixture);
        when(productInfoImageRepository.findByProductId(productFixture.getId()))
                .thenReturn(infoImageFixture);

        AdminProductPatchDataDTO result = assertDoesNotThrow(() -> adminProductService.getPatchProductData(productFixture.getId()));

        assertNotNull(result);
        assertFalse(result.optionList().isEmpty());
        assertFalse(result.thumbnailList().isEmpty());
        assertFalse(result.infoImageList().isEmpty());
        assertFalse(result.classificationList().isEmpty());
        assertEquals(productFixture.getId(), result.productId());
        assertEquals(productFixture.getProductName(), result.productName());
        assertEquals(productFixture.getClassification().getId(), result.classificationId());
        assertEquals(productFixture.getThumbnail(), result.firstThumbnail());
        assertEquals(productFixture.getProductPrice(), result.price());
        assertEquals(productFixture.isOpen(), result.isOpen());
        assertEquals(productFixture.getProductDiscount(), result.discount());
        assertEquals(optionFixture.size(), result.optionList().size());
        assertEquals(thumbnailFixture.size(), result.thumbnailList().size());
        assertEquals(infoImageFixture.size(), result.infoImageList().size());
        assertEquals(classificationFixture.size(), result.classificationList().size());
    }

    @Test
    @DisplayName(value = "상품 수정 요청. 삭제 또는 추가할 파일이 전혀 없는 경우")
    void patchProductFileEmpty() {
        Product product = ProductUnitFixture.createSaveProductList(1, "OUTER").get(0);
        List<PatchOptionDTO> optionDTOList = product.getProductOptions()
                .stream()
                .map(v -> new PatchOptionDTO(1L, v.getSize(), v.getColor(), v.getStock(), v.isOpen()))
                .toList();
        AdminProductPatchDTO patchDTO = new AdminProductPatchDTO(
                product.getProductName(),
                product.getClassification().getId(),
                product.getProductPrice(),
                product.isOpen(),
                product.getProductDiscount(),
                optionDTOList
        );
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any()))
                .thenReturn(product);
        when(productOptionRepository.saveAll(any()))
                .thenReturn(product.getProductOptions());
        when(productThumbnailRepository.saveAll(any()))
                .thenReturn(product.getProductThumbnails());
        when(productInfoImageRepository.saveAll(any()))
                .thenReturn(product.getProductInfoImages());

        String result = assertDoesNotThrow(() -> adminProductService.patchProduct(product.getId(), null, patchDTO, imageDTO));

        try {
            verify(fileServiceMock, never()).imageInsert(any(MultipartFile.class));
        }catch (Exception e){
            fail("이미지 저장 메서드에 접근하면 안되는데 접근 후 오류 발생");
        }

        verify(productOptionRepository, never()).deleteAllById(anyList());
        verify(fileServiceMock, never()).deleteImage(any());

        assertEquals(product.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 수정 요청. 수정할 파일이 모두 존재하는 경우")
    void patchProduct() {
        Product product = ProductUnitFixture.createSaveProductList(1, "OUTER").get(0);
        MockMultipartFile firstThumb = createMockMultipartFile(product.getThumbnail());
        List<MockMultipartFile> thumbnailMock = product.getProductThumbnails()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> thumbnails = new ArrayList<>(thumbnailMock);
        List<MockMultipartFile> infoImagesMock = product.getProductInfoImages()
                .stream()
                .map(v -> createMockMultipartFile(v.getImageName()))
                .toList();
        List<MultipartFile> infoImages = new ArrayList<>(infoImagesMock);
        List<PatchOptionDTO> optionDTOList = product.getProductOptions()
                .stream()
                .map(v -> new PatchOptionDTO(v.getId(), v.getSize(), v.getColor(), v.getStock(), v.isOpen()))
                .toList();
        AdminProductPatchDTO postDTO = new AdminProductPatchDTO(
                product.getProductName(),
                product.getClassification().getId(),
                product.getProductPrice(),
                product.isOpen(),
                product.getProductDiscount(),
                optionDTOList
        );
        AdminProductImageDTO imageDTO = new AdminProductImageDTO(
                firstThumb,
                "deleteThumbnail",
                thumbnails,
                List.of("deleteThumb1", "deleteThumb2", "deleteThumb3"),
                infoImages,
                List.of("deleteInfo1", "deleteInfo2", "deleteInfo3")
        );

        try {
            when(fileServiceMock.imageInsert(any(MultipartFile.class)))
                    .thenReturn("mockFile.jpg");
        }catch (Exception e) {
            fail("Mocking imageInsert failed : " + e.getMessage());
        }

        doNothing().when(fileServiceMock).deleteImage(anyString());

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any()))
                .thenReturn(product);
        when(productOptionRepository.saveAll(any()))
                .thenReturn(product.getProductOptions());
        when(productThumbnailRepository.saveAll(any()))
                .thenReturn(product.getProductThumbnails());
        when(productInfoImageRepository.saveAll(any()))
                .thenReturn(product.getProductInfoImages());
        doNothing().when(productThumbnailRepository).deleteByImageName(any());
        doNothing().when(productInfoImageRepository).deleteByImageName(any());

        List<Long> deleteOptionList = List.of(100L, 101L, 102L);

        String result = assertDoesNotThrow(() -> adminProductService.patchProduct(product.getId(), deleteOptionList, postDTO, imageDTO));

        assertEquals(product.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 조회")
    void getProductStock() {
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(20);
        List<String> productIds = productFixtureList.stream().map(Product::getId).toList();
        List<AdminProductStockDataDTO> productStockList = new ArrayList<>();
        List<AdminOptionStockDTO> optionStockList = new ArrayList<>();
        for(Product product : productFixtureList) {
            int totalStock = 0;

            for(ProductOption option : product.getProductOptions()) {
                totalStock += option.getStock();
                optionStockList.add(new AdminOptionStockDTO(product.getId(),
                        option.getSize(),
                        option.getColor(),
                        option.getStock(),
                        option.isOpen())
                );
            }

            productStockList.add(
                    new AdminProductStockDataDTO(product.getId(),
                            product.getClassification().getId(),
                            product.getProductName(),
                            totalStock,
                            product.isOpen())
            );
        }
        List<AdminProductStockDTO> dataList = productStockList.stream()
                .map(v -> {
                    List<AdminProductOptionStockDTO> optionList = optionStockList.stream()
                            .filter(option -> v.productId().equals(option.productId()))
                            .map(AdminProductOptionStockDTO::new)
                            .toList();

                    return new AdminProductStockDTO(v.productId(), v, optionList);
                })
                .toList();

        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);

        when(productRepository.findStockData(pageDTO))
                .thenReturn(productStockList);
        when(productRepository.findStockCount(pageDTO))
                .thenReturn((long) productStockList.size());
        when(productOptionRepository.findAllOptionByProductIdList(productIds))
                .thenReturn(optionStockList);

        PagingListDTO<AdminProductStockDTO> result = assertDoesNotThrow(() -> adminProductService.getProductStock(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(productStockList.size(), result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());
        assertEquals(1, result.pagingData().getNumber());
        assertEquals(productStockList.size(), result.content().size());

        for(int i = 0; i < result.content().size(); i++) {
            AdminProductStockDTO data = dataList.get(i);
            AdminProductStockDTO resultData = result.content().get(i);

            assertEquals(data.productId(), resultData.productId());
            assertEquals(data.classification(), resultData.classification());
            assertEquals(data.productName(), resultData.productName());
            assertEquals(data.totalStock(), resultData.totalStock());
            assertEquals(data.isOpen(), resultData.isOpen());
            assertEquals(data.optionList().size(), resultData.optionList().size());
        }
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 조회. 검색했으나 데이터가 없는 경우.")
    void getProductStockSearchEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO("fakeProduct", 1);

        when(productRepository.findStockData(pageDTO))
                .thenReturn(new ArrayList<>());
        when(productRepository.findStockCount(pageDTO))
                .thenReturn(0L);
        when(productOptionRepository.findAllOptionByProductIdList(new ArrayList<>()))
                .thenReturn(new ArrayList<>());


        PagingListDTO<AdminProductStockDTO> result = assertDoesNotThrow(() -> adminProductService.getProductStock(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertTrue(result.pagingData().isEmpty());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회")
    void getDiscountProduct() {
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(30);
        List<Product> productDiscountList = productFixtureList.stream()
                .filter(v -> v.getProductDiscount() > 0)
                .toList();
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        when(productRepository.getDiscountProduct(pageDTO, pageable))
                .thenReturn(new PageImpl<>(productDiscountList));

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(productDiscountList.size(), result.pagingData().getTotalElements());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회. 검색")
    void getDiscountProductSearch() {
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(10);
        Product discountProduct = productFixtureList.stream()
                .filter(v -> v.getProductDiscount() > 0)
                .findFirst()
                .get();

        AdminPageDTO pageDTO = new AdminPageDTO(discountProduct.getProductName(), 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        when(productRepository.getDiscountProduct(pageDTO, pageable))
                .thenReturn(new PageImpl<>(List.of(discountProduct)));

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.pagingData().getTotalElements());
        assertEquals(1, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회. 할인중인 상품이 없는 경우")
    void getDiscountProductEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        when(productRepository.getDiscountProduct(pageDTO, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        PagingListDTO<AdminDiscountResponseDTO> result = assertDoesNotThrow(() -> adminProductService.getDiscountProduct(pageDTO));

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pagingData().getTotalElements());
        assertEquals(0, result.pagingData().getTotalPages());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회")
    void getSelectDiscountProduct() {
        List<Product> productFixtureList = ProductFixture.createDefaultProductByOUTER(30);
        List<AdminDiscountProductDTO> resultList = productFixtureList.stream()
                .map(v ->
                        new AdminDiscountProductDTO(v.getId(),
                                v.getProductName(),
                                v.getProductPrice()
                        )
                )
                .toList();


        String classification = "OUTER";
        when(productRepository.getProductByClassification(classification))
                .thenReturn(resultList);

        List<AdminDiscountProductDTO> result = assertDoesNotThrow(() -> adminProductService.getSelectDiscountProduct(classification));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(resultList.size(), result.size());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회. 상품 분류에 해당하는 상품이 없는 경우")
    void getSelectDiscountProductEmpty() {
        String classification = "TOP";
        when(productRepository.getProductByClassification(classification))
                .thenReturn(new ArrayList<>());

        List<AdminDiscountProductDTO> result = assertDoesNotThrow(() -> adminProductService.getSelectDiscountProduct(classification));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 할인율 수정 요청")
    void patchDiscountProduct() {
        List<String> productIds = ProductFixture.createDefaultProductByOUTER(5).stream().map(Product::getId).toList();
        int discount = 10;
        AdminDiscountPatchDTO patchDTO = new AdminDiscountPatchDTO(productIds, discount);
        doNothing().when(productRepository).patchProductDiscount(patchDTO);

        String result = assertDoesNotThrow(() -> adminProductService.patchDiscountProduct(patchDTO));

        assertEquals(Result.OK.getResultKey(), result);
    }
}

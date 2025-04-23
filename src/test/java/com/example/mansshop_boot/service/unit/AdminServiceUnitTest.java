package com.example.mansshop_boot.service.unit;

import com.example.mansshop_boot.Fixture.ClassificationFixture;
import com.example.mansshop_boot.Fixture.MemberAndAuthFixture;
import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.Fixture.ProductOrderFixture;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.cache.CacheProperties;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.RedisCaching;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnAReplyRepository;
import com.example.mansshop_boot.repository.memberQnA.MemberQnARepository;
import com.example.mansshop_boot.repository.periodSales.PeriodSalesSummaryRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderDetailRepository;
import com.example.mansshop_boot.repository.productOrder.ProductOrderRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnAReplyRepository;
import com.example.mansshop_boot.repository.productQnA.ProductQnARepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewReplyRepository;
import com.example.mansshop_boot.repository.productReview.ProductReviewRepository;
import com.example.mansshop_boot.repository.productSales.ProductSalesSummaryRepository;
import com.example.mansshop_boot.repository.qnaClassification.QnAClassificationRepository;
import com.example.mansshop_boot.service.AdminServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = MansShopBootApplication.class)
@ActiveProfiles("test")
public class AdminServiceUnitTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private AdminServiceImpl adminServiceMock;

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ClassificationRepository classificationRepository;

    @Mock
    private ProductThumbnailRepository productThumbnailRepository;

    @Mock
    private ProductInfoImageRepository productInfoImageRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private ProductOrderDetailRepository productOrderDetailRepository;

    @Mock
    private ProductQnARepository productQnARepository;

    @Mock
    private ProductQnAReplyRepository productQnAReplyRepository;

    @Mock
    private MemberQnARepository memberQnARepository;

    @Mock
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Mock
    private QnAClassificationRepository qnAClassificationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductReviewReplyRepository productReviewReplyRepository;

    @Mock
    private PeriodSalesSummaryRepository periodSalesSummaryRepository;

    @Mock
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperation;

    /*@BeforeEach
    void init() {
        adminService.init();
    }*/

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

        PagingListDTO<AdminProductListDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getProductList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(productFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getNumber());
    }

    @Test
    @DisplayName(value = "상품 목록 조회. 상품이 없는 경우")
    void getProductListEmpty() {
        AdminPageDTO pageDTO = new AdminPageDTO(null, 1);

        when(productRepository.findAdminProductList(pageDTO))
                .thenReturn(List.of());
        when(productRepository.findAdminProductListCount(pageDTO)).thenReturn(0L);

        PagingListDTO<AdminProductListDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getProductList(pageDTO));

        System.out.println(result.pagingData());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertTrue(result.pagingData().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getNumber());
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

        PagingListDTO<AdminProductListDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getProductList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());
        Assertions.assertEquals(mockData.size(), result.pagingData().getTotalElements());
        Assertions.assertFalse(result.pagingData().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getNumber());
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

        List<String> result = Assertions.assertDoesNotThrow(() -> adminService.getClassification());

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(classificationFixtureList.size(), result.size());

        for(int i = 0; i < result.size(); i++) {
            Classification classification = classificationFixtureList.get(i);
            String resultData = result.get(i);

            Assertions.assertEquals(classification.getId(), resultData);
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

        AdminProductDetailDTO result = Assertions.assertDoesNotThrow(() -> adminService.getProductDetail(productFixture.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.optionList().isEmpty());
        Assertions.assertFalse(result.thumbnailList().isEmpty());
        Assertions.assertFalse(result.infoImageList().isEmpty());
        Assertions.assertEquals(productFixture.getId(), result.productId());
        Assertions.assertEquals(productFixture.getClassification().getId(), result.classification());
        Assertions.assertEquals(productFixture.getThumbnail(), result.firstThumbnail());
        Assertions.assertEquals(optionFixture.size(), result.optionList().size());
        Assertions.assertEquals(thumbnailFixture.size(), result.thumbnailList().size());
        Assertions.assertEquals(infoImageFixture.size(), result.infoImageList().size());
        Assertions.assertEquals(productFixture.getProductPrice(), result.price());
        Assertions.assertEquals(productFixture.isOpen(), result.isOpen());
        Assertions.assertEquals(productFixture.getProductSalesQuantity(), result.sales());
        Assertions.assertEquals(productFixture.getProductDiscount(), result.discount());
    }

    @Test
    @DisplayName(value = "존재하지 않는 상품 정보를 검색하는 경우")
    void getProductDetailEmpty() {

        when(productRepository.findById("fakeProductId"))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.getProductDetail("fakeProductId"));
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

        AdminProductPatchDataDTO result = Assertions.assertDoesNotThrow(() -> adminService.getPatchProductData(productFixture.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.optionList().isEmpty());
        Assertions.assertFalse(result.thumbnailList().isEmpty());
        Assertions.assertFalse(result.infoImageList().isEmpty());
        Assertions.assertFalse(result.classificationList().isEmpty());
        Assertions.assertEquals(productFixture.getId(), result.productId());
        Assertions.assertEquals(productFixture.getProductName(), result.productName());
        Assertions.assertEquals(productFixture.getClassification().getId(), result.classificationId());
        Assertions.assertEquals(productFixture.getThumbnail(), result.firstThumbnail());
        Assertions.assertEquals(productFixture.getProductPrice(), result.price());
        Assertions.assertEquals(productFixture.isOpen(), result.isOpen());
        Assertions.assertEquals(productFixture.getProductDiscount(), result.discount());
        Assertions.assertEquals(optionFixture.size(), result.optionList().size());
        Assertions.assertEquals(thumbnailFixture.size(), result.thumbnailList().size());
        Assertions.assertEquals(infoImageFixture.size(), result.infoImageList().size());
        Assertions.assertEquals(classificationFixture.size(), result.classificationList().size());
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
            when(adminServiceMock.imageInsert(any(MultipartFile.class)))
                    .thenReturn("mockFile.jpg");

        }catch (Exception e) {
            System.out.println("test image insert mock error");
            e.printStackTrace();
            throw new RuntimeException();
        }

        when(productRepository.save(any()))
                .thenReturn(product);
        when(productOptionRepository.saveAll(any()))
                .thenReturn(product.getProductOptions());
        when(productThumbnailRepository.saveAll(any()))
                .thenReturn(product.getProductThumbnails());
        when(productInfoImageRepository.saveAll(any()))
                .thenReturn(product.getProductInfoImages());

        String result = Assertions.assertDoesNotThrow(() -> adminService.postProduct(postDTO, imageDTO));

        Assertions.assertEquals(product.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 수정 요청. 삭제 또는 추가할 파일이 전혀 없는 경우")
    void patchProductFileEmpty() {
        Product product = ProductFixture.createSaveProductList(1, "OUTER").get(0);
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

        String result = Assertions.assertDoesNotThrow(() -> adminService.patchProduct(product.getId(), null, patchDTO, imageDTO));

        Assertions.assertEquals(product.getId(), result);
    }

    @Test
    @DisplayName(value = "상품 수정 요청. 수정할 파일이 모두 존재하는 경우")
    void patchProduct() {
        Product product = ProductFixture.createSaveProductList(1, "OUTER").get(0);
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
            when(adminServiceMock.imageInsert(any(MultipartFile.class)))
                    .thenReturn("mockFile.jpg");
            doNothing().when(adminServiceMock).deleteImage(anyString());
        }catch (Exception e) {
            System.out.println("test image insert mock error");
            e.printStackTrace();
            throw new RuntimeException();
        }

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

        String result = Assertions.assertDoesNotThrow(() -> adminService.patchProduct(product.getId(), deleteOptionList, postDTO, imageDTO));

        Assertions.assertEquals(product.getId(), result);
    }

    private MockMultipartFile createMockMultipartFile(String imageName) {
        return new MockMultipartFile("fieldName", imageName, "image/jpeg", new byte[]{1, 2, 3, 4, 5});
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

        PagingListDTO<AdminProductStockDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getProductStock(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(productStockList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());
        Assertions.assertEquals(1, result.pagingData().getNumber());
        Assertions.assertEquals(productStockList.size(), result.content().size());

        for(int i = 0; i < result.content().size(); i++) {
            AdminProductStockDTO data = dataList.get(i);
            AdminProductStockDTO resultData = result.content().get(i);

            Assertions.assertEquals(data.productId(), resultData.productId());
            Assertions.assertEquals(data.classification(), resultData.classification());
            Assertions.assertEquals(data.productName(), resultData.productName());
            Assertions.assertEquals(data.totalStock(), resultData.totalStock());
            Assertions.assertEquals(data.isOpen(), resultData.isOpen());
            Assertions.assertEquals(data.optionList().size(), resultData.optionList().size());
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


        PagingListDTO<AdminProductStockDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getProductStock(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertTrue(result.pagingData().isEmpty());
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

        PagingListDTO<AdminDiscountResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getDiscountProduct(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(productDiscountList.size(), result.pagingData().getTotalElements());
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

        PagingListDTO<AdminDiscountResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getDiscountProduct(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(1, result.pagingData().getTotalElements());
        Assertions.assertEquals(1, result.pagingData().getTotalPages());
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

        PagingListDTO<AdminDiscountResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getDiscountProduct(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.content().isEmpty());
        Assertions.assertEquals(0, result.pagingData().getTotalElements());
        Assertions.assertEquals(0, result.pagingData().getTotalPages());
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

        List<AdminDiscountProductDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getSelectDiscountProduct(classification));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(resultList.size(), result.size());
    }

    @Test
    @DisplayName(value = "상품 분류에 해당하는 상품 리스트 조회. 상품 분류에 해당하는 상품이 없는 경우")
    void getSelectDiscountProductEmpty() {
        String classification = "TOP";
        when(productRepository.getProductByClassification(classification))
                .thenReturn(new ArrayList<>());

        List<AdminDiscountProductDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getSelectDiscountProduct(classification));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 할인율 수정 요청")
    void patchDiscountProduct() {
        List<String> productIds = ProductFixture.createDefaultProductByOUTER(5).stream().map(Product::getId).toList();
        int discount = 10;
        AdminDiscountPatchDTO patchDTO = new AdminDiscountPatchDTO(productIds, discount);
        doNothing().when(productRepository).patchProductDiscount(patchDTO);

        String result = Assertions.assertDoesNotThrow(() -> adminService.patchDiscountProduct(patchDTO));

        Assertions.assertEquals(Result.OK.getResultKey(), result);
    }

    @Test
    @DisplayName(value = "모든 주문 목록 조회")
    void getAllOrderList() {
        List<Member> memberFixtureList = MemberAndAuthFixture.createDefaultMember(30).memberList();
        List<ProductOption> productOptionFixtureList = ProductFixture.createDefaultProductByOUTER(20)
                .stream()
                .flatMap(v -> v.getProductOptions().stream())
                .toList();
        List<ProductOrder> orderFixtureList = ProductOrderFixture.createSaveProductOrder(memberFixtureList, productOptionFixtureList);
        List<AdminOrderDTO> resultOrderDTO = orderFixtureList.stream()
                .map(v ->
                        new AdminOrderDTO(v.getId(),
                                v.getRecipient(),
                                v.getMember().getUserId(),
                                v.getOrderPhone(),
                                v.getCreatedAt(),
                                v.getOrderAddress(),
                                v.getOrderStat()
                        )
                )
                .limit(20)
                .toList();
        List<AdminOrderDetailListDTO> resultOrderDetailList = orderFixtureList.stream()
                                                                        .flatMap(v -> v.getProductOrderDetailSet().stream())
                                                                        .toList()
                                                                        .stream()
                                                                        .map(v ->
                                                                            new AdminOrderDetailListDTO(
                                                                                    v.getProductOrder().getId(),
                                                                                    v.getProduct().getClassification().getId(),
                                                                                    v.getProduct().getProductName(),
                                                                                    v.getProductOption().getSize(),
                                                                                    v.getProductOption().getColor(),
                                                                                    v.getOrderDetailCount(),
                                                                                    v.getOrderDetailPrice(),
                                                                                    v.isOrderReviewStatus()
                                                                            )
                                                                        )
                                                                        .toList();

        AdminOrderPageDTO pageDTO = new AdminOrderPageDTO(null, null, 1);
        List<Long> orderIds = resultOrderDTO.stream().mapToLong(AdminOrderDTO::orderId).boxed().toList();
        when(productOrderRepository.findAllOrderList(pageDTO))
                .thenReturn(resultOrderDTO);
        when(redisTemplate.opsForValue()).thenReturn(valueOperation);
        when(redisTemplate.opsForValue().get(RedisCaching.ADMIN_ORDER_COUNT.getKey())).thenReturn((long) orderFixtureList.size());
        when(productOrderDetailRepository.findByOrderIds(orderIds))
                .thenReturn(resultOrderDetailList);

        PagingListDTO<AdminOrderResponseDTO> result = Assertions.assertDoesNotThrow(() -> adminService.getAllOrderList(pageDTO));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.content().isEmpty());
        Assertions.assertEquals(orderFixtureList.size(), result.pagingData().getTotalElements());
        Assertions.assertEquals(2, result.pagingData().getTotalPages());
        Assertions.assertEquals(pageDTO.amount(), result.content().size());
    }
}

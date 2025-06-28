package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.Fixture.*;
import com.example.mansshop_boot.Fixture.domain.member.MemberAndAuthFixtureDTO;
import com.example.mansshop_boot.MansShopBootApplication;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.ExceptionEntity;
import com.example.mansshop_boot.controller.fixture.TokenFixture;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.admin.business.PatchOptionDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.auth.AuthRepository;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.member.MemberRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.service.FileServiceImpl;
import com.example.mansshop_boot.util.PaginationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = MansShopBootApplication.class)
@EnableJpaRepositories(basePackages = "com.example")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerIT {

    @MockBean
    private FileServiceImpl fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private TokenFixture tokenFixture;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    @Autowired
    private ProductInfoImageRepository productInfoImageRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("#{jwt['token.access.header']}")
    private String accessHeader;

    @Value("#{jwt['token.refresh.header']}")
    private String refreshHeader;

    @Value("#{jwt['cookie.ino.header']}")
    private String inoHeader;

    private Map<String, String> tokenMap;

    private String accessTokenValue;

    private String refreshTokenValue;

    private String inoValue;

    private List<Classification> classificationList;

    private List<Product> outerProductList;

    private List<Product> topProductList;

    private List<Product> allProductList;

    private static final String URL_PREFIX = "/api/admin/";

    @BeforeEach
    void init() {
        MemberAndAuthFixtureDTO adminFixture = MemberAndAuthFixture.createAdmin();
        Member admin = adminFixture.memberList().get(0);
        memberRepository.save(admin);
        authRepository.saveAll(adminFixture.authList());

        tokenMap = tokenFixture.createAndSaveAllToken(admin);
        accessTokenValue = tokenMap.get(accessHeader);
        refreshTokenValue = tokenMap.get(refreshHeader);
        inoValue = tokenMap.get(inoHeader);

        classificationList = ClassificationFixture.createClassification();
        classificationRepository.saveAll(classificationList);

        int firstProductListSize = 30;

        outerProductList = ProductFixture.createSaveProductList(firstProductListSize, classificationList.get(0));
        topProductList = ProductFixture.createAdditionalProduct(firstProductListSize + 1, 20, classificationList.get(1));
        allProductList = new ArrayList<>(outerProductList);
        allProductList.addAll(topProductList);
        List<ProductOption> productOptionList = allProductList.stream()
                        .flatMap(v -> v.getProductOptions().stream())
                        .toList();
        List<ProductThumbnail> productThumbnailList = allProductList.stream()
                .flatMap(v -> v.getProductThumbnails().stream())
                .toList();
        List<ProductInfoImage> productInfoImageList = allProductList.stream()
                .flatMap(v -> v.getProductInfoImages().stream())
                .toList();
        productRepository.saveAll(allProductList);
        productOptionRepository.saveAll(productOptionList);
        productThumbnailRepository.saveAll(productThumbnailList);
        productInfoImageRepository.saveAll(productInfoImageList);

        em.flush();
        em.clear();
    }

    @AfterEach
    void cleanUP() {
        String accessKey = tokenMap.get("accessKey");
        String refreshKey = tokenMap.get("refreshKey");

        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);
    }

    @Test
    @DisplayName(value = "전체 상품 목록 조회")
    void getProductList() throws Exception {
        AdminPageDTO pageDTOFixture = PageDTOFixture.createDefaultAdminPageDTO(1);
        int contentSize = Math.min(allProductList.size(), pageDTOFixture.amount());
        int totalPages = PaginationUtils.getTotalPages(allProductList.size(), pageDTOFixture.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product")
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue)))
                            .andExpect(status().isOk())
                            .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductListDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "전체 상품 목록 조회. 상품명 검색. 전체 데이터가 포함되는 'testProduct' 로 검색")
    void getProductListSearchMultipleResult() throws Exception {
        AdminPageDTO pageDTOFixture = PageDTOFixture.createDefaultAdminPageDTO(1);
        int contentSize = Math.min(allProductList.size(), pageDTOFixture.amount());
        int totalPages = PaginationUtils.getTotalPages(allProductList.size(), pageDTOFixture.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", "testProduct"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductListDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "전체 상품 목록 조회. 상품명 검색. 하나의 상품데이터 상품명으로 검색")
    void getProductListSearch() throws Exception {
        Product fixture = allProductList.get(0);
        int stock = fixture.getProductOptions()
                        .stream()
                        .mapToInt(ProductOption::getStock)
                        .sum();
        Long optionCount = (long) fixture.getProductOptions().size();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", fixture.getProductName()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductListDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalPages());

        AdminProductListDTO responseData = response.content().get(0);
        assertEquals(fixture.getId(), responseData.productId());
        assertEquals(fixture.getClassification().getId(), responseData.classification());
        assertEquals(fixture.getProductName(), responseData.productName());
        assertEquals(stock, responseData.stock());
        assertEquals(optionCount, responseData.optionCount());
        assertEquals(fixture.getProductPrice(), responseData.price());
    }

    @Test
    @DisplayName(value = "전체 상품 목록 조회. 데이터가 없는 경우")
    void getProductListEmpty() throws Exception {
        productRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductListDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회")
    void getClassificationList() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/classification")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(classificationList.size(), response.size());
        classificationList.forEach(v -> assertTrue(response.contains(v.getId())));
    }

    @Test
    @DisplayName(value = "상품 분류 리스트 조회. 데이터가 없는 경우")
    void getClassificationListEmpty() throws Exception {
        productRepository.deleteAll();
        classificationRepository.deleteAll();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/classification")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회")
    void getProductDetail() throws Exception {
        Product fixture = allProductList.get(0);
        List<AdminProductOptionDTO> productOptionDTOFixture = fixture.getProductOptions()
                .stream()
                .map(v -> new AdminProductOptionDTO(
                        v.getId(),
                        v.getSize(),
                        v.getColor(),
                        v.getStock(),
                        v.isOpen()
                ))
                .toList();
        List<String> productThumbnailFixture = fixture.getProductThumbnails()
                .stream()
                .map(ProductThumbnail::getImageName)
                .toList();
        List<String> productInfoImageFixture = fixture.getProductInfoImages()
                .stream()
                .map(ProductInfoImage::getImageName)
                .toList();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/detail/" + fixture.getId())
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        AdminProductDetailDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(fixture.getId(), response.productId());
        assertEquals(fixture.getClassification().getId(), response.classification());
        assertEquals(fixture.getProductName(), response.productName());
        assertEquals(fixture.getThumbnail(), response.firstThumbnail());
        assertEquals(fixture.getProductPrice(), response.price());
        assertEquals(fixture.isOpen(), response.isOpen());
        assertEquals(fixture.getProductSalesQuantity(), response.sales());
        assertEquals(fixture.getProductDiscount(), response.discount());

        assertEquals(productThumbnailFixture.size(), response.thumbnailList().size());
        productThumbnailFixture.forEach(v -> assertTrue(response.thumbnailList().contains(v)));

        assertEquals(productInfoImageFixture.size(), response.infoImageList().size());
        productInfoImageFixture.forEach(v -> assertTrue(response.infoImageList().contains(v)));

        assertEquals(productOptionDTOFixture.size(), response.optionList().size());
        productOptionDTOFixture.forEach(v -> assertTrue(response.optionList().contains(v)));
    }

    @Test
    @DisplayName(value = "상품 상세 정보 조회. 상품 아이디가 잘못된 경우")
    void getProductDetailWrongProductId() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/detail/noneProductId")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
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

    private List<PatchOptionDTO> getPostPatchOptionDTOList() {
        return IntStream.range(0, 2)
                .mapToObj(v -> new PatchOptionDTO(
                        0L,
                        "postSize" + v,
                        "postColor" + v,
                        100,
                        true
                ))
                .toList();
    }

    private AdminProductPatchDTO getAdminProductPatchDTOFixture(List<PatchOptionDTO> optionList) {

        return new AdminProductPatchDTO(
                "postProduct",
                "TOP",
                20000,
                true,
                0,
                optionList
                );
    }

    private MockMultipartHttpServletRequestBuilder createMockMultipartRequestBuilder(String requestURL,
                                                                                     List<PatchOptionDTO> optionList,
                                                                                     boolean imageListFlag) throws Exception {
        AdminProductPatchDTO patchDTO = getAdminProductPatchDTOFixture(optionList);

        MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(requestURL)
                .param("productName", patchDTO.getProductName())
                .param("classification", patchDTO.getClassification())
                .param("price", String.valueOf(patchDTO.getPrice()))
                .param("isOpen", String.valueOf(patchDTO.getIsOpen()))
                .param("discount", String.valueOf(patchDTO.getDiscount()));

        for(int i = 0; i < patchDTO.getOptionList().size(); i++) {
            PatchOptionDTO patchOptionDTO = patchDTO.getOptionList().get(i);

            builder
                    .param("optionList[" + i + "].optionId",
                            String.valueOf(patchOptionDTO.getOptionId()))
                    .param("optionList[" + i + "].size",
                            patchOptionDTO.getSize())
                    .param("optionList[" + i + "].color",
                            patchOptionDTO.getColor())
                    .param("optionList[" + i + "].optionStock",
                            String.valueOf(patchOptionDTO.getOptionStock()))
                    .param("optionList[" + i + "].optionIsOpen",
                            String.valueOf(patchOptionDTO.isOptionIsOpen()));
        }

        MockMultipartFile firstThumbnail = createMockMultipartFile("firstThumbnail", "firstThumb");
        builder.file(firstThumbnail);

        if(imageListFlag){
            List<MockMultipartFile> thumbnail = new ArrayList<>(createMockMultipartFileList("thumbnail", 3));
            List<MockMultipartFile> infoImage = new ArrayList<>(createMockMultipartFileList("info", 3));

            for(MockMultipartFile file : thumbnail)
                builder.file(file);

            for(MockMultipartFile file : infoImage)
                builder.file(file);
        }


        return builder;
    }

    @Test
    @DisplayName(value = "상품 추가")
    void postProduct() throws Exception {
        String requestURL = URL_PREFIX + "product";
        List<PatchOptionDTO> optionList = getPostPatchOptionDTOList();
        AdminProductPatchDTO patchFixture = getAdminProductPatchDTOFixture(optionList);
        MockMultipartHttpServletRequestBuilder builder = createMockMultipartRequestBuilder(requestURL, optionList, true);

        given(fileService.imageInsert(any(MultipartFile.class)))
                .willReturn("saved-first-thumb.jpg");

        MvcResult result = mockMvc.perform(
                        builder
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseIdDTO<String> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);

        Product saveProduct = productRepository.findById(response.id()).orElse(null);
        assertNotNull(saveProduct);

        assertEquals(patchFixture.getProductName(), saveProduct.getProductName());
        assertEquals(patchFixture.getClassification(), saveProduct.getClassification().getId());
        assertEquals(patchFixture.getPrice(), saveProduct.getProductPrice());
        assertEquals(patchFixture.getIsOpen(), saveProduct.isOpen());
        assertEquals(patchFixture.getDiscount(), saveProduct.getProductDiscount());
        assertEquals(patchFixture.getOptionList().size(), saveProduct.getProductOptions().size());
    }

    @Test
    @DisplayName(value = "상품 수정")
    void patchProduct() throws Exception {
        Product fixture = allProductList.get(0);
        String requestURL = URL_PREFIX + "product/" + fixture.getId();
        List<ProductOption> optionFixture = fixture.getProductOptions();
        List<PatchOptionDTO> patchOptionList = new ArrayList<>();
        for(int i = 1; i < optionFixture.size(); i++) {
            ProductOption option = optionFixture.get(i);
            patchOptionList.add(
                    new PatchOptionDTO(
                            option.getId(),
                            "patchSize" + i,
                            "patchColor" + i,
                            option.getStock() + 100,
                            true
                    )
            );
        }
        patchOptionList.addAll(getPostPatchOptionDTOList());

        AdminProductPatchDTO patchFixture = getAdminProductPatchDTOFixture(patchOptionList);
        int optionCountFixture = fixture.getProductOptions().size() + 1;
        MockMultipartHttpServletRequestBuilder builder = createMockMultipartRequestBuilder(requestURL, patchOptionList, true);
        List<Long> deleteOptionIds = List.of(fixture.getProductOptions().get(0).getId());
        long deleteOptionId = deleteOptionIds.get(0);
        String deleteOptionIdsJson = om.writeValueAsString(deleteOptionIds);
        MockMultipartFile deleteOptionList = new MockMultipartFile(
                "deleteOptionList",
                "",
                "application/json",
                deleteOptionIdsJson.getBytes(StandardCharsets.UTF_8)
        );

        builder.file(deleteOptionList)
                .param("deleteFirstThumbnail", fixture.getThumbnail())
                .param("deleteThumbnail", fixture.getProductThumbnails().get(0).getImageName())
                .param("deleteInfoImage", fixture.getProductInfoImages().get(0).getImageName())
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        given(fileService.imageInsert(any(MultipartFile.class)))
                .willReturn("saved-first-thumb.jpg");
        willDoNothing().given(fileService).deleteImage(anyString());

        MvcResult result = mockMvc.perform(
                        builder
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseIdDTO<String> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);

        em.flush();
        em.clear();

        Product saveProduct = productRepository.findById(response.id()).orElse(null);
        assertNotNull(saveProduct);

        assertEquals(patchFixture.getProductName(), saveProduct.getProductName());
        assertEquals(patchFixture.getClassification(), saveProduct.getClassification().getId());
        assertEquals(patchFixture.getPrice(), saveProduct.getProductPrice());
        assertEquals(patchFixture.getIsOpen(), saveProduct.isOpen());
        assertEquals(patchFixture.getDiscount(), saveProduct.getProductDiscount());
        assertEquals(optionCountFixture, saveProduct.getProductOptions().size());
        boolean flag = false;
        for(ProductOption saveOption : saveProduct.getProductOptions()) {
            if(deleteOptionId == saveOption.getId()){
                flag = true;
                break;
            }
        }

        if(flag)
            fail("Deleted deleteOptionId Exists");
    }

    @Test
    @DisplayName(value = "상품 수정. 대표 썸네일 추가 파일이 존재하지만, 삭제 파일명은 존재하지 않는 경우 400 발생")
    void patchProductWrongFirstThumb() throws Exception {
        Product fixture = allProductList.get(0);
        String requestURL = URL_PREFIX + "product/" + fixture.getId();
        List<ProductOption> optionFixture = fixture.getProductOptions();
        List<PatchOptionDTO> patchOptionList = new ArrayList<>();
        for (int i = 1; i < optionFixture.size(); i++) {
            ProductOption option = optionFixture.get(i);
            patchOptionList.add(
                    new PatchOptionDTO(
                            option.getId(),
                            "patchSize" + i,
                            "patchColor" + i,
                            option.getStock() + 100,
                            true
                    )
            );
        }
        patchOptionList.addAll(getPostPatchOptionDTOList());

        int optionCountFixture = fixture.getProductOptions().size();
        MockMultipartHttpServletRequestBuilder builder = createMockMultipartRequestBuilder(requestURL, patchOptionList, true);
        List<Long> deleteOptionIds = List.of(fixture.getProductOptions().get(0).getId());
        long deleteOptionId = deleteOptionIds.get(0);
        String deleteOptionIdsJson = om.writeValueAsString(deleteOptionIds);
        MockMultipartFile deleteOptionList = new MockMultipartFile(
                "deleteOptionList",
                "",
                "application/json",
                deleteOptionIdsJson.getBytes(StandardCharsets.UTF_8)
        );

        builder.file(deleteOptionList)
                .param("deleteThumbnail", fixture.getProductThumbnails().get(0).getImageName())
                .param("deleteInfoImage", fixture.getProductInfoImages().get(0).getImageName())
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        given(fileService.imageInsert(any(MultipartFile.class)))
                .willReturn("saved-first-thumb.jpg");
        willDoNothing().given(fileService).deleteImage(anyString());

        MvcResult result = mockMvc.perform(
                        builder
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {
                }
        );

        em.flush();
        em.clear();

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());

        Product saveProduct = productRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(saveProduct);
        assertEquals(optionCountFixture, saveProduct.getProductOptions().size());
    }

    @Test
    @DisplayName(value = "상품 수정. 상품 아이디가 잘못된 경우")
    void patchProductWrongProductId() throws Exception {
        Product fixture = allProductList.get(0);
        String requestURL = URL_PREFIX + "product/noneProductId";
        List<ProductOption> optionFixture = fixture.getProductOptions();
        List<PatchOptionDTO> patchOptionList = new ArrayList<>();
        for(int i = 1; i < optionFixture.size(); i++) {
            ProductOption option = optionFixture.get(i);
            patchOptionList.add(
                    new PatchOptionDTO(
                            option.getId(),
                            "patchSize" + i,
                            "patchColor" + i,
                            option.getStock() + 100,
                            true
                    )
            );
        }
        patchOptionList.addAll(getPostPatchOptionDTOList());

        MockMultipartHttpServletRequestBuilder builder = createMockMultipartRequestBuilder(requestURL, patchOptionList, true);
        List<Long> deleteOptionIds = List.of(fixture.getProductOptions().get(0).getId());
        String deleteOptionIdsJson = om.writeValueAsString(deleteOptionIds);
        MockMultipartFile deleteOptionList = new MockMultipartFile(
                "deleteOptionList",
                "",
                "application/json",
                deleteOptionIdsJson.getBytes(StandardCharsets.UTF_8)
        );

        builder.file(deleteOptionList)
                .param("deleteFirstThumbnail", fixture.getThumbnail())
                .param("deleteThumbnail", fixture.getProductThumbnails().get(0).getImageName())
                .param("deleteInfoImage", fixture.getProductInfoImages().get(0).getImageName())
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                });

        given(fileService.imageInsert(any(MultipartFile.class)))
                .willReturn("saved-first-thumb.jpg");
        willDoNothing().given(fileService).deleteImage(anyString());

        MvcResult result = mockMvc.perform(
                        builder
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(accessHeader, accessTokenValue)
                                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                .cookie(new Cookie(inoHeader, inoValue))
                )
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 조회")
    void getProductStockList() throws Exception {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        int totalPages = PaginationUtils.getTotalPages(allProductList.size(), pageDTO.amount());
        List<Product> fixture = allProductList.stream()
                .sorted(
                        Comparator.comparingInt(product ->
                                product.getProductOptions().stream()
                                        .mapToInt(ProductOption::getStock)
                                        .sum()
                        )
                )
                .limit(pageDTO.amount())
                .toList();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/stock")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue)))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductStockDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(fixture.size(), response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 조회. 데이터가 없는 경우")
    void getProductStockListEmpty() throws Exception {
        productRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/stock")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductStockDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "상품 재고 리스트 조회. 상품명 기반 검색")
    void getProductStockListSearchProductName() throws Exception {
        Product fixture = allProductList.get(0);
        int totalStock = fixture.getProductOptions()
                                .stream()
                                .mapToInt(ProductOption::getStock)
                                .sum();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/stock")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", fixture.getProductName()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminProductStockDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalPages());
        AdminProductStockDTO responseDTO = response.content().get(0);
        assertEquals(fixture.getId(), responseDTO.productId());
        assertEquals(fixture.getClassification().getId(), responseDTO.classification());
        assertEquals(fixture.getProductName(), responseDTO.productName());
        assertEquals(totalStock, responseDTO.totalStock());
        assertEquals(fixture.isOpen(), responseDTO.isOpen());
        assertEquals(fixture.getProductOptions().size(), responseDTO.optionList().size());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회")
    void getDiscountList() throws Exception {
        AdminPageDTO pageDTO = AdminPageDTOFixture.createDefaultAdminPageDTO();
        List<Product> fixtureList = allProductList.stream()
                .filter(v -> v.getProductDiscount() > 0)
                .toList();
        int contentSize = Math.min(fixtureList.size(), pageDTO.amount());
        int totalPages = PaginationUtils.getTotalPages(fixtureList.size(), pageDTO.amount());

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/discount")
                .header(accessHeader, accessTokenValue)
                .cookie(new Cookie(refreshHeader, refreshTokenValue))
                .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminDiscountResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(contentSize, response.content().size());
        assertEquals(totalPages, response.totalPages());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회. 데이터가 없는 경우")
    void getDiscountListEmpty() throws Exception {
        productRepository.deleteAll();

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/discount")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminDiscountResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertTrue(response.empty());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName(value = "할인중인 상품 목록 조회. 상품명 기반 검색")
    void getDiscountListSearchProductName() throws Exception {
        Product fixture = allProductList.stream()
                .filter(v -> v.getProductDiscount() > 0)
                .toList()
                .get(0);
        int discountPrice = (int) (fixture.getProductPrice() * (1 - ((double)fixture.getProductDiscount() / 100)));

        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/discount")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .param("keyword", fixture.getProductName()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PagingResponseDTO<AdminDiscountResponseDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertFalse(response.content().isEmpty());
        assertFalse(response.empty());
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalPages());

        AdminDiscountResponseDTO responseDTO = response.content().get(0);
        assertEquals(fixture.getId(), responseDTO.productId());
        assertEquals(fixture.getClassification().getId(), responseDTO.classification());
        assertEquals(fixture.getProductName(), responseDTO.productName());
        assertEquals(fixture.getProductPrice(), responseDTO.price());
        assertEquals(fixture.getProductDiscount(), responseDTO.discount());
        assertEquals(discountPrice, responseDTO.totalPrice());
    }

    @Test
    @DisplayName(value = "할인 설정에서 선택한 상품 분류에 해당하는 상품 목록 조회")
    void getDiscountProductSelectList() throws Exception {
        List<Product> fixtureList = outerProductList.stream()
                                            .sorted(Comparator.comparing(Product::getProductName))
                                            .toList();
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "product/discount/select/" + classificationList.get(0).getId())
                                        .header(accessHeader, accessTokenValue)
                                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                        .cookie(new Cookie(inoHeader, inoValue)))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String content = result.getResponse().getContentAsString();
        List<AdminDiscountProductDTO> response = om.readValue(
                content,
                new TypeReference<>() {}
        );
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(fixtureList.size(), response.size());

        for(int i = 0; i < fixtureList.size(); i++) {
            Product fixture = fixtureList.get(i);
            AdminDiscountProductDTO responseDTO = response.get(i);

            assertEquals(fixture.getId(), responseDTO.productId());
            assertEquals(fixture.getProductName(), responseDTO.productName());
            assertEquals(fixture.getProductPrice(), responseDTO.productPrice());
        }
    }

    @Test
    @DisplayName(value = "상품 할인 설정. 단일 상품만 수정하는 경우")
    void patchDiscountProductOne() throws Exception {
        Product fixture = allProductList.stream()
                            .filter(v -> v.getProductDiscount() == 0)
                            .findFirst()
                            .get();
        AdminDiscountPatchDTO discountDTO = new AdminDiscountPatchDTO(List.of(fixture.getId()), 30);

        String requestDTO = om.writeValueAsString(discountDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "product/discount")
                                    .header(accessHeader, accessTokenValue)
                                    .cookie(new Cookie(refreshHeader, refreshTokenValue))
                                    .cookie(new Cookie(inoHeader, inoValue))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestDTO))
                                .andExpect(status().isOk())
                                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        em.flush();
        em.clear();

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        Product patchProduct = productRepository.findById(fixture.getId()).orElse(null);
        assertNotNull(patchProduct);
        assertEquals(discountDTO.discount(), patchProduct.getProductDiscount());
    }

    @Test
    @DisplayName(value = "상품 할인 설정. 여러 상품을 수정하는 경우")
    void patchDiscountProductMultiple() throws Exception {
        List<String> patchProductIds = allProductList.stream()
                                                .filter(v -> v.getProductDiscount() == 0)
                                                .limit(2)
                                                .map(Product::getId)
                                                .toList();

        AdminDiscountPatchDTO discountDTO = new AdminDiscountPatchDTO(patchProductIds, 30);

        String requestDTO = om.writeValueAsString(discountDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "product/discount")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ResponseMessageDTO response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        em.flush();
        em.clear();

        assertNotNull(response);
        assertEquals(Result.OK.getResultKey(), response.message());

        List<Product> patchProductList = productRepository.findAllById(patchProductIds);

        assertFalse(patchProductList.isEmpty());
        patchProductList.forEach(v -> assertEquals(discountDTO.discount(), v.getProductDiscount()));
    }


    @Test
    @DisplayName(value = "상품 할인 설정. 상품 아이디가 잘못된 경우")
    void patchDiscountProductWrongProductId() throws Exception {
        AdminDiscountPatchDTO discountDTO = new AdminDiscountPatchDTO(
                List.of("noneProductId1","noneProductId2"), 30);

        String requestDTO = om.writeValueAsString(discountDTO);

        MvcResult result = mockMvc.perform(patch(URL_PREFIX + "product/discount")
                        .header(accessHeader, accessTokenValue)
                        .cookie(new Cookie(refreshHeader, refreshTokenValue))
                        .cookie(new Cookie(inoHeader, inoValue))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(status().is(400))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionEntity response = om.readValue(
                content,
                new TypeReference<>() {}
        );

        assertNotNull(response);
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), response.errorMessage());
    }
}

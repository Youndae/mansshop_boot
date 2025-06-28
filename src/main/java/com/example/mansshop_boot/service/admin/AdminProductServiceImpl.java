package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.in.AdminDiscountPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductImageDTO;
import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.classification.ClassificationRepository;
import com.example.mansshop_boot.repository.product.ProductInfoImageRepository;
import com.example.mansshop_boot.repository.product.ProductOptionRepository;
import com.example.mansshop_boot.repository.product.ProductRepository;
import com.example.mansshop_boot.repository.product.ProductThumbnailRepository;
import com.example.mansshop_boot.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;

    private final ClassificationRepository classificationRepository;

    private final ProductOptionRepository productOptionRepository;

    private final ProductThumbnailRepository productThumbnailRepository;

    private final ProductInfoImageRepository productInfoImageRepository;

    private final FileService fileService;

    /**
     *
     * @param pageDTO
     *
     * 상품 목록 조회
     */
    @Override
    public PagingListDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO) {

        List<AdminProductListDTO> dto = productRepository.findAdminProductList(pageDTO);
        Long totalElements = 0L;
        if(!dto.isEmpty())
            totalElements = productRepository.findAdminProductListCount(pageDTO);

        //PagingMapping 하나 만들어서 서비스로.
        // totalElements가 필요한 매핑과 필요없는 매핑으로 구분. 특정 DTO로 반환.
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(dto, pagingMappingDTO);
    }

    /**
     *
     * 상품 분류 리스트 조회.
     * 테이블 데이터를 그대로 넘기지는 않고 분류명만 리스트화해서 반환.
     */
    @Override
    public List<String> getClassification() {
        List<Classification> classification = classificationRepository.findAll(Sort.by("classificationStep").ascending());

        return classification.stream().map(Classification::getId).toList();
    }

    /**
     *
     * @param productId
     *
     * 상품 정보 조회
     * 리뷰나 QnA는 제외하고 순수하게 상품에 대한 데이터만 조회해서 반환.
     *
     * 아이디, 분류명, 상품명, 대표 썸네일, 썸네일 리스트, 정보 이미지 리스트,
     * 옵션 리스트, 가격, 공개 여부, 판매량, 할인율
     */
    @Override
    public AdminProductDetailDTO getProductDetail(String productId) {

        return getProductData(productId);
    }

    /**
     *
     * @param productId
     *
     * 상품 정보 조회와 조회한 데이터의 매핑을 처리.
     * 상품 상세 정보 조회와 수정 데이터 조회에서 호출.
     */
    private AdminProductDetailDTO getProductData(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        List<AdminProductOptionDTO> productOptionList = productOptionRepository.findAllByProductId(productId);
        List<String> thumbnailList = productThumbnailRepository.findByProductId(productId);
        List<String> infoImageList = productInfoImageRepository.findByProductId(productId);

        return new AdminProductDetailDTO(productId, product, thumbnailList, infoImageList, productOptionList);
    }

    /**
     *
     * @param productId
     *
     * 상품 수정 페이지에 출력할 상품 정보 데이터 조회.
     * 상세 페이지와 다른점으로는 상품 분류명 리스트를 같이 전달.
     */
    @Override
    public AdminProductPatchDataDTO getPatchProductData(String productId) {
        AdminProductDetailDTO dto = getProductData(productId);
        List<Classification> entity = classificationRepository.findAll(Sort.by("classificationStep").descending());
        List<String> classificationList = entity.stream().map(Classification::getId).toList();

        return new AdminProductPatchDataDTO(dto, classificationList);
    }

    /**
     *
     * @param patchDTO
     * @param imageDTO
     *
     * 싱픔 추가 처리.
     * ProductEntity 안에 ProductOption, ProductThumbnail, ProductInfoImage Entity들에 대한 연관관계 설정과 Set으로 담을 수 있도록 처리.
     * 양방향 매핑은 각 엔티티에 대한 데이터 파싱을 편하게 하기 위한 용도로만 사용하고,
     * 저장은 각 Repository를 통해서 저장하도록 처리.
     * CascadeType.ALL 사용을 지양하기 위함.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {
        Product product = patchDTO.toPostEntity();
        String resultId;
        List<String> saveImages = new ArrayList<>();
        try {
            String firstThumbnail = setProductFirstThumbnail(product, imageDTO.getFirstThumbnail());
            if(firstThumbnail != null){
                saveImages = saveProductImage(product, imageDTO);
                saveImages.add(firstThumbnail);
            } else
                throw new IllegalArgumentException("Failed postProduct. firstThumbnail is null");

            patchDTO.getProductOptionList(product).forEach(product::addProductOption);

            resultId = productRepository.save(product).getId();
            productOptionRepository.saveAll(product.getProductOptions());
            productThumbnailRepository.saveAll(product.getProductThumbnails());
            productInfoImageRepository.saveAll(product.getProductInfoImages());
        }catch (Exception e) {
            log.warn("Filed admin postProduct");
            e.printStackTrace();
            saveImages.forEach(fileService::deleteImage);

            throw new IllegalArgumentException("Failed postProduct", e);
        }

        return resultId;
    }

    /**
     *
     * @param productId
     * @param deleteOptionList
     * @param patchDTO
     * @param imageDTO
     *
     * 상품 수정 처리.
     * 상품 추가 처리와 대부분 동일.
     *
     * 데이터 저장 처리 후 삭제해야할 옵션과 썸네일, 정보이미지에 대한 처리 진행 후 응답
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {
        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        Classification classification = product.getClassification();

        if(!product.getClassification().getId().equals(patchDTO.getClassification()))
            classification = classificationRepository.findById(patchDTO.getClassification()).orElseThrow(IllegalArgumentException::new);

        product.setPatchData(patchDTO, classification);
        List<String> saveImages = new ArrayList<>();

        try{
            //option 제거를 먼저 하지 않는다면, setProductOptionData에서 getProductOptions()로 인해
            //ProductOption이 영속성 컨텍스트에 의해 관리된다.
            //그렇게 되면 운영 환경이나 서비스 통합 테스트에서는 문제가 발생하지 않지만,
            //컨트롤러 통합 테스트에서는 영속성 컨텍스트에서 관리하는 productOption에 의해 DB에 다시 올라가게 된다.
            //서비스 통합 테스트의 경우 테스트 클래스의 @Transactional이 Service, Repository 전체를 감싸고 있지만,
            //컨트롤러 통합 테스트에서는 MockMvc 기반이기 때문에 내부적으로 별도의 트랜잭션이 나뉠 수 있기 때문이다.
            //또한 flush, clear가 자동으로 일어나지 않기 때문에 운영 또는 서비스 테스트 환경과 다른 결과를 볼 수 있게 된다.

            setProductOptionData(product, patchDTO);
            saveImages = saveProductImage(product, imageDTO);

            if((imageDTO.getDeleteFirstThumbnail() == null && imageDTO.getFirstThumbnail() != null) ||
                    (imageDTO.getDeleteFirstThumbnail() != null && imageDTO.getFirstThumbnail() == null))
                throw new IllegalArgumentException();

            String firstThumbnail = setProductFirstThumbnail(product, imageDTO.getFirstThumbnail());

            if(firstThumbnail != null)
                saveImages.add(firstThumbnail);

            productOptionRepository.saveAll(product.getProductOptions());
            productThumbnailRepository.saveAll(product.getProductThumbnails());
            productInfoImageRepository.saveAll(product.getProductInfoImages());
            productRepository.save(product);

            if(deleteOptionList != null)
                productOptionRepository.deleteAllById(deleteOptionList);
        }catch (Exception e) {
            log.warn("Failed admin patchProduct");
            e.printStackTrace();
            saveImages.forEach(fileService::deleteImage);

            throw new IllegalArgumentException("Failed patchProduct", e);
        }

        deleteProductImage(imageDTO);

        return productId;
    }

    /**
     *
     * @param product
     * @param firstThumbnail
     * @throws Exception
     *
     * 대표 썸네일 파일 저장 및 Entity set
     */
    private String setProductFirstThumbnail(Product product, MultipartFile firstThumbnail) throws Exception{
        String thumbnail = null;

        if(firstThumbnail != null){
            String saveName = fileService.imageInsert(firstThumbnail);
            thumbnail = saveName;
            product.setThumbnail(saveName);
        }

        return thumbnail;
    }

    /**
     *
     * @param product
     * @param patchDTO
     *
     * AdminProductPatchDTO에 존재하는 수정된 OptionDTO를 통해 ProductOption Entity를 수정.
     * 이렇게 처리하지 않으면 PersistenceContext에 의해 오류가 발생하기 때문에 ProductOption 리스트를 별도로 save 처리해야 함.
     */
    private void setProductOptionData(Product product, AdminProductPatchDTO patchDTO) {
        List<PatchOptionDTO> optionDTOList = patchDTO.getOptionList();
        List<ProductOption> optionEntities = product.getProductOptions();

        for(int i = 0; i < optionDTOList.size(); i++) {
            PatchOptionDTO dto = optionDTOList.get(i);
            long dtoOptionId = dto.getOptionId();
            boolean patchStatus = true;

            for(int j = 0; j < optionEntities.size(); j++) {
                ProductOption option = optionEntities.get(j);

                if(option.getId() != null && dtoOptionId == option.getId()){
                    option.patchOptionData(dto);
                    patchStatus = false;
                    break;
                }
            }

            if(patchStatus)
                product.addProductOption(dto.toEntity());
        }
    }

    /**
     *
     * @param product
     * @param imageDTO
     * @throws Exception
     *
     * 상품 썸네일, 정보 이미지 파일 저장 처리 메서드 호출
     * 저장된 이미지 파일명들을 리스트화 해서 반환
     * 예외는 최상위 메서드에서 제어하기 위해 throws
     */
    private List<String> saveProductImage(Product product, AdminProductImageDTO imageDTO) throws Exception{
        List<String> thumbnails = saveThumbnail(product, imageDTO.getThumbnail());
        List<String> infoImages = saveInfoImage(product, imageDTO.getInfoImage());

        thumbnails.addAll(infoImages);

        return thumbnails;
    }

    /**
     *
     * @param product
     * @param imageList
     * @throws Exception
     *
     * 상품 썸네일 이미지 저장 처리 및 저장 파일명 리스트 반환, Product Entity 내부 필드에 추가
     * 예외는 최상위 메서드에서 제어하기 위해 throws
     */
    private List<String> saveThumbnail(Product product, List<MultipartFile> imageList) throws Exception{
        List<String> thumbnailList = new ArrayList<>();

        if(imageList != null){
            for(MultipartFile image : imageList){
                String saveName = fileService.imageInsert(image);
                thumbnailList.add(saveName);
                product.addProductThumbnail(
                        ProductThumbnail.builder()
                                .product(product)
                                .imageName(saveName)
                                .build()
                );
            }
        }

        return thumbnailList;
    }

    /**
     *
     * @param product
     * @param imageList
     * @throws Exception
     *
     * 상품 정보 이미지 저장 처리 및 저장 파일명 리스트 반환, Product Entity 내부 필드에 추가
     * 예외는 최상위 메서드에서 제어하기 위해 throws
     */
    private List<String> saveInfoImage(Product product, List<MultipartFile> imageList) throws Exception{
        List<String> infoImages = new ArrayList<>();

        if(imageList != null) {
            for(MultipartFile image : imageList) {
                String saveName = fileService.imageInsert(image);
                infoImages.add(saveName);
                product.addProductInfoImage(
                        ProductInfoImage.builder()
                                .product(product)
                                .imageName(saveName)
                                .build()
                );
            }
        }

        return infoImages;
    }

    /**
     *
     * @param imageDTO
     *
     * 상품 수정 과정에서 삭제해야 할 이미지 파일 처리
     */
    private void deleteProductImage(AdminProductImageDTO imageDTO) {
        deleteFirstThumbnail(imageDTO.getDeleteFirstThumbnail());
        deleteThumbnail(imageDTO.getDeleteThumbnail());
        deleteInfoImage(imageDTO.getDeleteInfoImage());
    }

    /**
     *
     * @param image
     *
     * 상품 대표 썸네일 삭제
     */
    private void deleteFirstThumbnail(String image) {
        if(image != null)
            fileService.deleteImage(image);
    }

    /**
     *
     * @param deleteList
     *
     * 상품 썸네일 리스트 삭제
     */
    private void deleteThumbnail(List<String> deleteList) {
        if(deleteList != null && !deleteList.isEmpty()){
            productThumbnailRepository.deleteByImageName(deleteList);
            deleteList.forEach(fileService::deleteImage);
        }
    }

    /**
     *
     * @param deleteList
     *
     * 상품 정보 이미지 리스트 삭제
     */
    private void deleteInfoImage(List<String> deleteList) {
        if(deleteList != null && !deleteList.isEmpty()){
            productInfoImageRepository.deleteByImageName(deleteList);
            deleteList.forEach(fileService::deleteImage);
        }
    }

    /**
     *
     * @param pageDTO
     *
     * 상품 재고 리스트 반환.
     * 분류 상관없이 재고가 적은 순서대로 조회.
     *
     * 응답 데이터는 상품 아이디, 상품 분류명, 상품명, 총 재고, 공개여부, 옵션 정보[] 형태로 처리해야 하므로
     * Pageable로 상품 데이터만 조회 후 해당 상품 아이디 리스트를 통해 Option 리스트를 조회한 다음 매핑하도록 처리.
     */
    @Override
    public PagingListDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO) {
        List<AdminProductStockDataDTO> dataList = productRepository.findStockData(pageDTO);
        Long totalElements = 0L;
        if(dataList.isEmpty()){
            PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());
            return new PagingListDTO<>(Collections.emptyList(), pagingMappingDTO);
        }

        totalElements = productRepository.findStockCount(pageDTO);
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());
        List<String> productIdList = dataList.stream().map(AdminProductStockDataDTO::productId).toList();
        List<AdminOptionStockDTO> optionList = productOptionRepository.findAllOptionByProductIdList(productIdList);

        List<AdminProductStockDTO> responseContent = new ArrayList<>();

        for(int i = 0; i < dataList.size(); i++) {
            AdminProductStockDataDTO stockDTO = dataList.get(i);
            String productId = stockDTO.productId();

            List<AdminProductOptionStockDTO> responseOptionList = optionList.stream()
                    .filter(option ->
                            productId.equals(option.productId()))
                    .map(AdminProductOptionStockDTO::new)
                    .toList();

            responseContent.add(new AdminProductStockDTO(productId, stockDTO, responseOptionList));
        }


        return new PagingListDTO<>(responseContent, pagingMappingDTO);
    }

    /**
     *
     * @param pageDTO
     *
     * 할인중인 상품 목록 조회
     * 원가와 할인가가 필요한데 할인율에 대한 연산을 쿼리에서 처리하는 것 보다
     * 여기서 처리하는 것이 낫다고 생각해 Product 타입으로 조회하고 DTO에 매핑하도록해
     * DTO 생성자 내부에서 연산 후 DTO 인스턴스 생성하도록 처리.
     */
    @Override
    public PagingListDTO<AdminDiscountResponseDTO> getDiscountProduct(AdminPageDTO pageDTO) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("updatedAt").descending());

        Page<Product> entityList = productRepository.getDiscountProduct(pageDTO, pageable);
        List<AdminDiscountResponseDTO> responseDTOList = entityList.getContent()
                .stream()
                .map(AdminDiscountResponseDTO::new)
                .toList();

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(entityList.getTotalElements(), pageDTO.page(), pageDTO.amount());
        return new PagingListDTO<>(responseDTOList, pagingMappingDTO);
    }

    /**
     *
     * @param classification
     *
     * 상품 분류에 해당하는 상품 리스트 조회.
     * 아이디, 상품명, 가격만 반환
     */
    @Override
    public List<AdminDiscountProductDTO> getSelectDiscountProduct(String classification) {

        return productRepository.getProductByClassification(classification);
    }

    /**
     *
     * @param patchDTO
     *
     * 상품 할인율 수정
     */
    @Override
    public String patchDiscountProduct(AdminDiscountPatchDTO patchDTO) {
        List<Product> productList = productRepository.findAllById(patchDTO.productIdList());

        if(productList.isEmpty() || productList.size() != patchDTO.productIdList().size())
            throw new IllegalArgumentException("patchDiscountProduct IllegalArgumentException");

        productRepository.patchProductDiscount(patchDTO);

        return Result.OK.getResultKey();
    }
}

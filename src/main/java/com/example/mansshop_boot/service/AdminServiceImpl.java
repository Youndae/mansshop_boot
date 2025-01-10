package com.example.mansshop_boot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.in.*;
import com.example.mansshop_boot.domain.dto.admin.out.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.PagingMappingDTO;
import com.example.mansshop_boot.domain.dto.response.serviceResponse.PagingListDTO;
import com.example.mansshop_boot.domain.entity.*;
import com.example.mansshop_boot.domain.enumuration.AdminListType;
import com.example.mansshop_boot.domain.enumuration.OrderStatus;
import com.example.mansshop_boot.domain.enumuration.Result;
import com.example.mansshop_boot.repository.*;
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

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    private final PrincipalService principalService;

    private final ClassificationRepository classificationRepository;

    private final ProductThumbnailRepository productThumbnailRepository;

    private final ProductInfoImageRepository productInfoImageRepository;

    private final ProductOrderRepository productOrderRepository;

    private final ProductOrderDetailRepository productOrderDetailRepository;

    private final ProductQnARepository productQnARepository;

    private final ProductQnAReplyRepository productQnAReplyRepository;

    private final MemberQnARepository memberQnARepository;

    private final MemberQnAReplyRepository memberQnAReplyRepository;

    private final QnAClassificationRepository qnAClassificationRepository;

    private final MemberRepository memberRepository;

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewReplyRepository productReviewReplyRepository;

    private final MyPageService myPageService;

    /**
     *
     * @param pageDTO
     *
     * 최소 Page<AdminProductListDTO>를 반환해야 한다
     * PagingResponseDTO 매핑은 상위 메소드나 컨트롤러에서 처리하도록 수정해야 한다.
     *
     */
    @Override
    public PagingListDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO) {

        List<AdminProductListDTO> dto = productRepository.findAdminProductList(pageDTO);
        Long totalElements = productRepository.findAdminProductListCount(pageDTO);

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
    public AdminProductDetailDTO getProductData(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        List<AdminProductOptionDTO> productOptionList = productOptionRepository.findAllByProductId(productId);
        List<String> thumbnailList = productThumbnailRepository.findByProductId(productId);
        List<String> infoImageList = productInfoImageRepository.findByProductId(productId);

        return new AdminProductDetailDTO(productId, product, thumbnailList, infoImageList, productOptionList);
    }

    /**
     *
     * @param productId
     * @param principal
     *
     * 상품 수정 페이지에 출력할 상품 정보 데이터 조회.
     * 상세 페이지와 다른점으로는 상품 분류명 리스트를 같이 전달.
     */
    @Override
    public AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal) {
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
     * Entity별로 save 요청하는 것이 아닌 데이터를 모두 담아준 뒤 ProductRepository.save로 처리.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {
        Product product = patchDTO.toPostEntity();
        String resultId;
        try {
            setProductFirstThumbnail(product, imageDTO.getFirstThumbnail());
            patchDTO.getProductOptionList(product).forEach(product::addProductOption);
            saveProductImage(product, imageDTO);

            resultId = productRepository.save(product).getId();
        }catch (Exception e) {
            log.warn("Filed admin postProduct");
            e.printStackTrace();
            if(product.getThumbnail() != null)
                deleteFirstThumbnailToException(product);

            deleteImageToException(product);

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
        product.setPatchData(patchDTO);

        try{
            setProductFirstThumbnail(product, imageDTO.getFirstThumbnail());
            setProductOptionData(product, patchDTO);
            productRepository.save(product);

            if(deleteOptionList != null)
                productOptionRepository.deleteAllById(deleteOptionList);
        }catch (Exception e) {
            log.warn("Filed admin patchProduct");
            e.printStackTrace();
            deleteFirstThumbnailToException(product);

            throw new IllegalArgumentException("Failed patchProduct", e);
        }

        try {
            saveProductImage(product, imageDTO);
        }catch (Exception e) {
            log.warn("Failed admin patchProduct");
            e.printStackTrace();
            deleteImageToException(product);

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
    public void setProductFirstThumbnail(Product product, MultipartFile firstThumbnail) throws Exception{
        if(firstThumbnail != null)
            product.setThumbnail(imageInsert(firstThumbnail));
    }

    /**
     *
     * @param product
     * @param patchDTO
     *
     * AdminProductPatchDTO에 존재하는 수정된 OptionDTO를 통해 ProductOption Entity를 수정.
     * 이렇게 처리하지 않으면 PersistenceContext에 의해 오류가 발생하기 때문에 ProductOption 리스트를 별도로 save 처리해야 함.
     */
    @Transactional
    public void setProductOptionData(Product product, AdminProductPatchDTO patchDTO) {
        List<PatchOptionDTO> optionDTOList = patchDTO.getOptionList();
        List<ProductOption> optionEntities = product.getProductOptionSet();

        for(int i = 0; i < optionDTOList.size(); i++) {
            PatchOptionDTO dto = optionDTOList.get(i);
            long dtoOptionId = dto.getOptionId();
            boolean patchStatus = true;

            for(int j = 0; j < optionEntities.size(); j++) {
                ProductOption option = optionEntities.get(j);

                if(dtoOptionId == option.getId()){
                    option.patchOptionData(dto);
                    patchStatus = false;
                    break;
                }
            }

            if(patchStatus)
                product.addProductOption(dto.toEntity());
        }
    }

    public void saveProductImage(Product product, AdminProductImageDTO imageDTO) throws Exception{
        saveThumbnail(product, imageDTO.getThumbnail());
        saveInfoImage(product, imageDTO.getInfoImage());
    }

    public void saveThumbnail(Product product, List<MultipartFile> imageList) throws Exception{
        if(imageList != null){
            for(MultipartFile image : imageList)
                product.addProductThumbnail(
                        ProductThumbnail.builder()
                                .product(product)
                                .imageName(imageInsert(image))
                                .build()
                );
        }

    }

    public void saveInfoImage(Product product, List<MultipartFile> imageList) throws Exception{
        if(imageList != null) {
            for(MultipartFile image : imageList)
                product.addProductInfoImage(
                        ProductInfoImage.builder()
                                .product(product)
                                .imageName(imageInsert(image))
                                .build()
                );
        }
    }

    public void deleteProductImage(AdminProductImageDTO imageDTO) {
        deleteFirstThumbnail(imageDTO.getDeleteFirstThumbnail());
        deleteThumbnail(imageDTO.getDeleteThumbnail());
        deleteInfoImage(imageDTO.getDeleteInfoImage());
    }

    public void deleteFirstThumbnail(String image) {
        deleteImage(image);
    }

    public void deleteThumbnail(List<String> deleteList) {
        if(deleteList != null){
            productThumbnailRepository.deleteByImageName(deleteList);
            deleteList.forEach(this::deleteImage);
        }
    }

    public void deleteInfoImage(List<String> deleteList) {
        if(deleteList != null){
            productInfoImageRepository.deleteByImageName(deleteList);
            deleteList.forEach(this::deleteImage);
        }
    }

    public void deleteFirstThumbnailToException(Product product) {
        if(product.getThumbnail() != null)
            deleteFirstThumbnail(product.getThumbnail());
    }

    public void deleteImageToException(Product product) {
        if(!product.getProductThumbnailSet().isEmpty()) {
            List<String> thumbnailList = product.getProductThumbnailSet()
                    .stream()
                    .map(ProductThumbnail::getImageName)
                    .toList();

            deleteThumbnail(thumbnailList);
        }

        if(!product.getProductInfoImageSet().isEmpty()) {
            List<String> infoImageList = product.getProductInfoImageSet()
                    .stream()
                    .map(ProductInfoImage::getImageName)
                    .toList();
            deleteInfoImage(infoImageList);
        }
    }

    /**
     *
     * @param image
     *
     * 파일 저장 처리.
     * 저장명을 반환.
     */
    public String imageInsert(MultipartFile image) throws Exception{
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")))
                .toString();
        String saveFile = filePath + saveName;

        image.transferTo(new File(saveFile));

        return saveName;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;

    /**
     *
     * @param image
     * S3에 파일 저장
     */
    /*public String imageInsert(MultipartFile image) throws Exception{
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")))
                .toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try{
            amazonS3.putObject(
                    new PutObjectRequest(
                            bucket
                            , saveName
                            , image.getInputStream()
                            , objectMetadata
                    )
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        }catch (Exception e) {
            log.warn("productImage insert IOException");
            e.printStackTrace();
            throw new NullPointerException();
        }

        return saveName;
    }*/

    /**
     *
     * @param imageName
     *
     * 파일 삭제 처리.
     */
    public void deleteImage(String imageName) {
        File file = new File(filePath + imageName);

        if(file.exists())
            file.delete();
    }

    /**
     *
     * @param imageName
     *
     * S3 파일 삭제
     */
    /*public void deleteImage(String imageName) {
        amazonS3.deleteObject(
                new DeleteObjectRequest(bucket, imageName)
        );
    }*/

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
        Long totalElements = productRepository.findStockCount(pageDTO);
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());
        List<String> productIdList = dataList.stream().map(AdminProductStockDataDTO::productId).toList();
        List<ProductOption> optionList = productOptionRepository.findAllOptionByProductIdList(productIdList);

        List<AdminProductStockDTO> responseContent = new ArrayList<>();

        for(int i = 0; i < dataList.size(); i++) {
            AdminProductStockDataDTO stockDTO = dataList.get(i);
            String productId = stockDTO.productId();

            List<AdminProductOptionStockDTO> responseOptionList = optionList.stream()
                                            .filter(option ->
                                                    productId.equals(option.getProduct().getId()))
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
        productRepository.patchProductDiscount(patchDTO);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     *
     * 모든 주문 목록 및 상세 데이터 조회.
     * 주문 목록의 경우 단기간에 많이 쌓일 수 있는 데이터이기 때문에 Pageable보다 직접 구현이 더 빠르다는 것을 테스트로 확인.
     * 데이터량이 상대적으로 적거나 증가폭이 낮은 조회에 대해서는 Pageable을 사용해 처리했지만 이렇게 증가폭이 높을 가능성이 있고
     * 주석 작성일 기준 250만개 데이터를 넣어두었기에 직접 구현으로 처리.
     *
     * 클라이언트에서 주문 목록 출력 후 클릭 시 모달창을 통해 상세 정보를 출력하므로
     * 재요청을 하도록 하는 것이 아닌 처음부터 상세 정보까지 같이 담아 반환하도록 처리.
     */
    @Override
    public PagingListDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO) {
        List<AdminOrderDTO> orderDTOList = productOrderRepository.findAllOrderList(pageDTO);
        Long totalElements = productOrderRepository.findAllOrderListCount(pageDTO);

        return mappingOrderDataAndPagingData(orderDTOList, totalElements, pageDTO);
    }

    /**
     *
     * @param pageDTO
     *
     * 미처리 주문 목록 및 상세 데이터 조회.
     * 요청 당일 16시 이전의 주문건 중 미처리 주문건만 조회해서 반환.
     *
     * 전체 주문 목록과 마찬가지로 모달창으로 처리하므로 상세 데이터까지 매핑해서 반환하도록 처리.
     */
    @Override
    public PagingListDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO) {

        LocalDateTime todayLastOrderTime = LocalDateTime.now()
                                                        .withHour(16)
                                                        .withMinute(0)
                                                        .withSecond(0)
                                                        .withNano(0);

        List<AdminOrderDTO> orderDTOList = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime);
        Long totalElements = productOrderRepository.findAllNewOrderListCount(pageDTO, todayLastOrderTime);

        return mappingOrderDataAndPagingData(orderDTOList, totalElements, pageDTO);
    }

    /**
     *
     * @param orderDTOList
     * @param totalElements
     * @param pageDTO
     *
     * 조회한 주문 목록에 대해 상세 정보 리스트를 조회.
     * 이후 주문 목록에 대한 상세 정보를 매핑 한 뒤 페이징 정보와 같이 반환.
     */
    public PagingListDTO<AdminOrderResponseDTO> mappingOrderDataAndPagingData(List<AdminOrderDTO> orderDTOList, Long totalElements, AdminOrderPageDTO pageDTO) {
        List<Long> orderIdList = orderDTOList.stream().map(AdminOrderDTO::orderId).toList();
        List<ProductOrderDetail> detailList = productOrderDetailRepository.findByOrderIds(orderIdList);
        List<AdminOrderResponseDTO> responseContent = new ArrayList<>();

        for(int i = 0; i < orderDTOList.size(); i++) {
            AdminOrderDTO orderDTO = orderDTOList.get(i);
            List<AdminOrderDetailDTO> detailDTOList = detailList.stream()
                                                            .filter(entity -> orderDTO.orderId() == entity.getProductOrder().getId())
                                                            .map(AdminOrderDetailDTO::new)
                                                            .toList();

            responseContent.add(orderDTO.toResponseDTO(detailDTOList));
        }

        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseContent, pagingMappingDTO);
    }

    /**
     *
     * @param orderId
     *
     * 주문 상태 주문 확인중 -> 상품 준비중 으로 수정.
     * 관리자가 주문 내역을 확인 후 버튼 클릭 시 수정.
     */
    @Override
    public String orderPreparation(long orderId) {
        ProductOrder productOrder = productOrderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        productOrder.setOrderStat(OrderStatus.PREPARATION.getStatusStr());
        productOrderRepository.save(productOrder);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     *
     * 상품 문의 목록 조회
     * pageDTO의 SearchType은 new, all 두가지로 구분.
     * 미처리와 전체 목록을 의미. 그래서 쿼리에서는 이 SearchType에 따라 조회.
     * keyword는 무조건 사용자 nickname을 기준. like가 아닌 equals로 조회하기 때문에 정확한 닉네임을 입력하도록 처리.
     */
    @Override
    public PagingListDTO<AdminQnAListResponseDTO> getProductQnAList(AdminOrderPageDTO pageDTO) {
        List<AdminQnAListResponseDTO> responseDTO = productQnARepository.findAllByAdminProductQnA(pageDTO);
        Long totalElements = productQnARepository.findAllByAdminProductQnACount(pageDTO);
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseDTO, pagingMappingDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 상품 문의 답변 완료 처리.
     * 다른 처리 없이 답변 상태만 완료 상태로 수정.
     */
    @Override
    public String patchProductQnAComplete(long qnaId) {
        ProductQnA productQnA = productQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        productQnA.setProductQnAStat(true);
        productQnARepository.save(productQnA);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 상품 답변 작성 처리.
     * ProductQnAReply에 save 처리 후 상태값 변경을 위해 patchProductQnAComplete 메소드를 호출해 수정한다.
     */
    @Override
    public String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {
        Member member = memberRepository.findById(principal.getName()).orElseThrow(IllegalArgumentException::new);
        ProductQnA productQnA = productQnARepository.findById(insertDTO.qnaId()).orElseThrow(IllegalArgumentException::new);

        ProductQnAReply productQnAReply = ProductQnAReply.builder()
                                                            .member(member)
                                                            .productQnA(productQnA)
                                                            .replyContent(insertDTO.content())
                                                            .build();

        productQnAReplyRepository.save(productQnAReply);

        return patchProductQnAComplete(insertDTO.qnaId());
    }

    /**
     *
     * @param replyDTO
     * @param principal
     *
     * 상품문의 답변 수정 처리.
     */
    @Override
    public String patchProductQnAReply(QnAReplyDTO replyDTO, Principal principal) {
        ProductQnAReply productQnAReply = productQnAReplyRepository.findById(replyDTO.replyId()).orElseThrow(IllegalArgumentException::new);
        String userId = principalService.getUserIdByPrincipal(principal);

        if(!productQnAReply.getMember().getUserId().equals(userId))
            throw new IllegalArgumentException();

        productQnAReply.setReplyContent(replyDTO.content());
        productQnAReplyRepository.save(productQnAReply);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     *
     * 회원 문의 목록 조회.
     * 상품 문의와 마찬가지로 pageDTO의 SearchType은 new, all 두가지로 구분.
     * 미처리와 전체 목록을 의미. 그래서 쿼리에서는 이 SearchType에 따라 조회.
     * keyword는 무조건 사용자 nickname을 기준. like가 아닌 equals로 조회하기 때문에 정확한 닉네임을 입력하도록 처리.
     */
    @Override
    public PagingListDTO<AdminQnAListResponseDTO> getMemberQnAList(AdminOrderPageDTO pageDTO) {
        List<AdminQnAListResponseDTO> responseDTO = memberQnARepository.findAllByAdminMemberQnA(pageDTO);
        Long totalElements = memberQnARepository.findAllByAdminMemberQnACount(pageDTO);
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(responseDTO, pagingMappingDTO);
    }

    /**
     *
     * @param qnaId
     *
     * 회원 문의 답변 완료 처리
     */
    @Override
    public String patchMemberQnAComplete(long qnaId) {
        MemberQnA memberQnA = memberQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        memberQnA.setMemberQnAStat(true);
        memberQnARepository.save(memberQnA);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param insertDTO
     * @param principal
     *
     * 회원 문의 답변 작성
     */
    @Override
    public String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {
        String postReplyResult = myPageService.postMemberQnAReply(insertDTO, principal);

        if(postReplyResult.equals(Result.OK.getResultKey())){
            return patchMemberQnAComplete(insertDTO.qnaId());
        }else {
            throw new IllegalArgumentException();
        }

    }

    /**
     *
     * 문의 분류 리스트 조회
     * Entity를 그대로 반환하지 않도록 하기 위해 DTO에 매핑 후 반환.
     */
    @Override
    public List<AdminQnAClassificationDTO> getQnAClassification() {
        List<QnAClassification> entity = qnAClassificationRepository.findAll(Sort.by("id").ascending());

        return entity.stream()
                        .map(AdminQnAClassificationDTO::new)
                        .toList();
    }

    /**
     *
     * @param classificationName
     *
     * 문의 분류 추가 처리
     */
    @Override
    public String postQnAClassification(String classificationName) {
        QnAClassification entity = QnAClassification.builder()
                                                .qnaClassificationName(classificationName)
                                                .build();

        qnAClassificationRepository.save(entity);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param classificationId
     *
     * 문의 분류 삭제 처리
     */
    @Override
    public String deleteQnAClassification(long classificationId) {
        qnAClassificationRepository.deleteById(classificationId);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     * @param listType
     *
     * 리뷰 리스트 조회.
     * 미답변 상태인 new, 전체인 all을 동적으로 처리
     * AdminListType이라는 enum을 통해 관리.
     * 검색 타입은 userName || nickname으로 검색하는 'user' 와
     * productName으로 검색하는 'product' 두가지가 존재.
     */
    @Override
    public PagingListDTO<AdminReviewDTO> getReviewList(AdminOrderPageDTO pageDTO, AdminListType listType) {
        List<AdminReviewDTO> content = productReviewRepository.findAllByAdminReviewList(pageDTO, listType.name());
        content.forEach(v -> log.info("AdminServiceImpl.getReviewList :: content forEach : {}", v));
        Long totalElements = productReviewRepository.countByAdminReviewList(pageDTO, listType.name());
        PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());

        return new PagingListDTO<>(content, pagingMappingDTO);
    }

    /**
     *
     * @param reviewId
     *
     * 리뷰 상세 페이지 데이터
     */
    @Override
    public AdminReviewDetailDTO getReviewDetail(long reviewId) {
//        return productReviewRepository.findByAdminReviewDetail(reviewId);

        AdminReviewDetailDTO dto =  productReviewRepository.findByAdminReviewDetail(reviewId);
        System.out.println("detail DTO : " + dto);

        return dto;
    }

    @Override
    public String postReviewReply(AdminReviewRequestDTO postDTO
                                , Principal principal) {
        Member member = memberRepository.findById(principal.getName())
                .orElseThrow(IllegalArgumentException::new);

        ProductReview reviewEntity = productReviewRepository.findById(postDTO.reviewId())
                .orElseThrow(IllegalArgumentException::new);

        ProductReviewReply entity = ProductReviewReply.builder()
                .member(member)
                .replyContent(postDTO.content())
                .productReview(reviewEntity)
                .build();

        productReviewReplyRepository.save(entity);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param pageDTO
     *
     * 회원 목록 조회
     * 가입일이 최근인 기준으로 정렬
     */
    @Override
    public Page<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO) {
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                        , pageDTO.amount()
                                        , Sort.by("createdAt").descending());

        return memberRepository.findMember(pageDTO, pageable);
    }

    /**
     *
     * @param pointDTO
     *
     * 회원 포인트 지급
     */
    @Override
    public String postPoint(AdminPostPointDTO pointDTO) {
        Member member = memberRepository.findById(pointDTO.userId()).orElseThrow(IllegalArgumentException::new);
        member.setMemberPoint(pointDTO.point());
        memberRepository.save(member);

        return Result.OK.getResultKey();
    }

    /**
     *
     * @param term
     *
     * 기간별 매출 조회.
     * term으로는 YYYY로 연도가 전달.
     * 해당 연도에 대해 매출, 판매량, 주문량, 월별 매출 리스트을 조회.
     *
     * 월 매출 리스트로는 월, 매출, 판매량, 주문량으로 조회.
     * 데이터가 전혀 없는 달이라도 0으로 출력하기 위해 반복문 횟수를 12번으로 고정.
     * 데이터가 없으면 월을 제외한 모든 데이터에 0으로 처리.
     */
    @Override
    public AdminPeriodSalesResponseDTO getPeriodSales(int term) {
        List<AdminPeriodSalesListDTO> selectList = productOrderRepository.findPeriodList(term);
        List<AdminPeriodSalesListDTO> contentList = new ArrayList<>();
        long yearSales = 0;
        long yearSalesQuantity = 0;
        long yearOrderQuantity = 0;

        for(int i = 1; i < 13; i++){
            AdminPeriodSalesListDTO content = new AdminPeriodSalesListDTO(i);

            for(int j = 0; j < selectList.size(); j++) {
                if(selectList.get(j).date() == i){
                    content = selectList.get(j);
                    yearSales += content.sales();
                    yearSalesQuantity += content.salesQuantity();
                    yearOrderQuantity += content.orderQuantity();

                    selectList.remove(j);
                    break;
                }
            }
            contentList.add(content);
        }

        return new AdminPeriodSalesResponseDTO(
                                contentList
                                , yearSales
                                , yearSalesQuantity
                                , yearOrderQuantity
                        );
    }

    /**
     *
     * @param term
     *
     * 기간별 매출 상세 조회
     * 연월 기준으로 조회.
     * term값으로는 YYYY-MM 구조로 받는다.
     * 해당 월의 매출, 판매량, 주문량, 전년 동월과의 매출 비교, 전년 동월의 매출, 판매량, 주문량
     * 해당 월의 베스트 매출 상품 5개, 상품 분류별 월 매출, 일별 매출을 조회한다.
     */
    @Override
    public AdminPeriodMonthDetailResponseDTO getPeriodSalesDetail(String term) {
        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);

        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        int lastDay = startDate.getMonth().length(startDate.toLocalDate().isLeapYear());
        LocalDateTime endDate = startDate.plusMonths(1);

        AdminPeriodSalesStatisticsDTO monthStatistics = productOrderRepository.findPeriodStatistics(startDate, endDate); // 1.505 Index -> createdAt 하는 경우 0.150 정도로 감소.
        List<AdminBestSalesProductDTO> bestProductList = productOrderRepository.findPeriodBestProductOrder(startDate, endDate);
        List<AdminPeriodSalesListDTO> dailySalesResponseDTO = getPeriodSalesList(lastDay, startDate, endDate);
        List<AdminPeriodClassificationDTO> classificationResponseDTO = getClassificationResponse(startDate, endDate);

        startDate = startDate.minusYears(1);
        endDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO lastYearStatistics = productOrderRepository.findPeriodStatistics(startDate, endDate);

        return new AdminPeriodMonthDetailResponseDTO(monthStatistics
                                                    , lastYearStatistics
                                                    , bestProductList
                                                    , classificationResponseDTO
                                                    , dailySalesResponseDTO);
    }

    public List<AdminPeriodClassificationDTO> getClassificationResponse(LocalDateTime startDate, LocalDateTime endDate) {
        List<AdminPeriodClassificationDTO> classificationList = productOrderDetailRepository.findPeriodClassification(startDate, endDate);
        List<Classification> classification = classificationRepository.findAll(Sort.by("classificationStep").descending()); // 0.063

        List<AdminPeriodClassificationDTO> classificationResponseDTO = new ArrayList<>();

        //매출이 전혀 없는 상품 분류의 경우 classificationList에 담겨있지 않을 것이므로
        //classification의 크기와 classificationList의 크기가 다르다면
        //조회되지 않은 상품 분류를 매출 0 으로 처리하기 위해 반복문을 통해 처리.
        if(classification.size() != classificationList.size()){
            for(int i = 0; i < classification.size(); i++) {
                String id = classification.get(i).getId();
                AdminPeriodClassificationDTO content = new AdminPeriodClassificationDTO(id, 0, 0);

                for(int j = 0; j < classificationList.size(); j++) {
                    if(id.equals(classificationList.get(j).classification())){
                        content = classificationList.get(j);
                        break;
                    }
                }

                classificationResponseDTO.add(content);
            }
        }else
            classificationResponseDTO = classificationList;

        return classificationResponseDTO;
    }

    public List<AdminPeriodSalesListDTO> getPeriodSalesList(int lastDay, LocalDateTime startDate, LocalDateTime endDate) {
        List<AdminPeriodSalesListDTO> dailySalesList = productOrderRepository.findPeriodDailyList(startDate, endDate); // 0.924
        List<AdminPeriodSalesListDTO> dailySalesResponseDTO = new ArrayList<>();

        //상품 분류별 데이터 매핑과 마찬가지로
        //매출이 없는 일자에 대해 0으로 처리 하기위해 크기가 다른 경우
        //반복문을 통해 0으로 처리.
        if(dailySalesList.size() != lastDay){
            for(int i = 1; i <= lastDay; i++) {
                int day = i;
                AdminPeriodSalesListDTO content = new AdminPeriodSalesListDTO(day);

                for(int j = 0; j < dailySalesList.size(); j++) {
                    if(day == dailySalesList.get(j).date()){
                        content = dailySalesList.get(j);
                        dailySalesList.remove(j);
                        break;
                    }
                }

                dailySalesResponseDTO.add(content);
            }
        }else
            dailySalesResponseDTO = dailySalesList;

        return dailySalesResponseDTO;
    }

    /**
     *
     * @param term
     * @param classification
     *
     * 상품 분류의 월 매출 내역 조회.
     * term은 YYYY-MM 구조.
     * 상품명, 해당 월 매출, 판매량, 상품별 매출[] 형태로 반환.
     * 상품별 매출 데이터의 경우 상품명, 사이즈, 컬러, 매출, 판매량이 반환된다.
     */
    @Override
    public AdminClassificationSalesResponseDTO getSalesByClassification(String term, String classification) {
        int[] termSplit = Arrays.stream(term.split("-"))
                                .mapToInt(Integer::parseInt)
                                .toArray();

        LocalDate start = LocalDate.of(termSplit[0], termSplit[1], 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(end, LocalTime.MAX);

        AdminClassificationSalesDTO classificationSalesDTO = productOrderDetailRepository.findPeriodClassificationSales(startDate, endDate, classification);
        List<AdminClassificationSalesProductListDTO> productList = productOrderDetailRepository.findPeriodClassificationProductSales(startDate, endDate, classification);

        return new AdminClassificationSalesResponseDTO(classification, classificationSalesDTO, productList);
    }

    /**
     *
     * @param term
     *
     * 일매출 정보 조회.
     * term은 YYYY-MM-DD 구조.
     * 해당 일의 분류별 매출, 판매량을 조회하고 그 날의 매출, 판매량, 주문량을 조회한다.
     */
    @Override
    public AdminPeriodSalesResponseDTO getSalesByDay(String term) {
        int[] termSplit = Arrays.stream(term.split("-"))
                                .mapToInt(Integer::parseInt)
                                .toArray();
        LocalDate start = LocalDate.of(termSplit[0], termSplit[1], termSplit[2]);
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);

        AdminClassificationSalesDTO salesDTO = productOrderRepository.findDailySales(startDate, endDate);
        List<AdminPeriodClassificationDTO> classificationList = productOrderDetailRepository.findPeriodClassification(startDate, endDate);

        return new AdminPeriodSalesResponseDTO(
                        classificationList
                        , salesDTO.sales()
                        , salesDTO.salesQuantity()
                        , salesDTO.orderQuantity()
                );
    }

    /**
     *
     * @param term
     * @param page
     *
     * 해당 일자의 주문 내역 조회
     * term은 YYYY-MM-DD 구조.
     * 해당 일자의 모든 주문 내역과 상세 내역을 조회.
     */
    @Override
    public PagingListDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page) {
        int[] termSplit = Arrays.stream(term.split("-"))
                                .mapToInt(Integer::parseInt)
                                .toArray();
        LocalDate start = LocalDate.of(termSplit[0], termSplit[1], termSplit[2]);
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(start, LocalTime.MAX);

        Pageable pageable = PageRequest.of(page - 1
                                        , 30
                                        , Sort.by("createdAt").descending());

        Page<ProductOrder> orderList = productOrderRepository.findAllByDay(startDate, endDate, pageable);
        List<Long> orderIdList = orderList.stream().map(ProductOrder::getId).toList();
        List<ProductOrderDetail> orderDetailList = productOrderDetailRepository.findByOrderIds(orderIdList);

        List<AdminDailySalesResponseDTO> content = new ArrayList<>();

        for(int i = 0; i < orderList.getContent().size(); i++) {
            ProductOrder productOrder = orderList.getContent().get(i);
            List<AdminDailySalesDetailDTO> detailContent = orderDetailList.stream()
                                                        .filter(orderDetail ->
                                                                productOrder.getId() == orderDetail.getProductOrder().getId()
                                                        )
                                                        .map(AdminDailySalesDetailDTO::new)
                                                        .toList();

            content.add(new AdminDailySalesResponseDTO(productOrder, detailContent));
        }

        PagingMappingDTO pagingMappingDTO = PagingMappingDTO.builder()
                                                .totalElements(orderList.getTotalElements())
                                                .number(orderList.getNumber())
                                                .empty(orderList.isEmpty())
                                                .totalPages(orderList.getTotalPages())
                                                .build();

        return new PagingListDTO<>(content, pagingMappingDTO);
    }

    /**
     *
     * @param pageDTO
     *
     * 상품별 매출 조회.
     * 상품 분류의 순서를 기준으로 정렬.
     */
    @Override
    public Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO) {
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                        , pageDTO.amount()
                                        , Sort.by("classificationStep").ascending());

        return productOrderRepository.getProductSalesList(pageDTO, pageable);
    }

    /**
     *
     * @param productId
     *
     * 상품의 매출 조회.
     *
     * 상품명, 총 매출, 총 판매량, 올해 매출, 올해 판매량, 전년과 비교, 전년 매출, 전년 판매량,
     * 월별 매출[월, 매출, 판매량, 주문량], 옵션별 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량],
     * 옵션별 올해 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량], 옵션별 전년도 매출[옵션 아이디, 사이즈, 컬러, 매출, 판매량]
     */
    @Override
    public AdminProductSalesDetailDTO getProductSalesDetail(String productId) {
        LocalDate date = LocalDate.now();
        int year = date.getYear();

        AdminProductSalesDTO totalSalesDTO = productOrderRepository.getProductSales(productId);
        AdminSalesDTO yearSalesDTO = productOrderRepository.getProductPeriodSales(year, productId);
        AdminSalesDTO lastYearSalesDTO = productOrderRepository.getProductPeriodSales(year - 1, productId);
        List<AdminPeriodSalesListDTO> monthSalesDTO = productOrderRepository.getProductMonthPeriodSales(year, productId);
        List<AdminProductSalesOptionDTO> optionTotalSalesList = productOrderDetailRepository.getProductOptionSales(0, productId);
        List<AdminProductSalesOptionDTO> optionYearSalesList = productOrderDetailRepository.getProductOptionSales(year, productId);
        List<AdminProductSalesOptionDTO> optionLastYearSalesList = productOrderDetailRepository.getProductOptionSales(year - 1, productId);
        List<ProductOption> productOption = productOptionRepository.findAllOptionByProductId(productId);

        // stream().filter()를 통한 방법, 반복문을 사용하는 방법
        // 또 그 중에서 기존 리스트에 add 해준 뒤 정렬하는 방법들도 수행해봤으나 이게 제일 빠름.
        // 찾으면 break 하는게 한몫 한다.
        // 만약 여러 데이터를 찾는다면 비슷할듯.
        List<AdminPeriodSalesListDTO> monthSalesMappingDTO = new ArrayList<>();
        for(int i = 1; i < 13; i++) {
            AdminPeriodSalesListDTO monthContent = new AdminPeriodSalesListDTO(i);

            for(int j = 0; j < monthSalesDTO.size(); j++) {
                if(i == monthSalesDTO.get(j).date()){
                    monthContent = monthSalesDTO.get(j);
                    break;
                }
            }

            monthSalesMappingDTO.add(monthContent);
        }

        optionTotalSalesList = optionDataMapping(optionTotalSalesList, productOption);
        optionYearSalesList = optionDataMapping(optionYearSalesList, productOption);
        optionLastYearSalesList = optionDataMapping(optionLastYearSalesList, productOption);

        return new AdminProductSalesDetailDTO(
                            totalSalesDTO
                            , yearSalesDTO
                            , lastYearSalesDTO
                            , monthSalesMappingDTO
                            , optionTotalSalesList
                            , optionYearSalesList
                            , optionLastYearSalesList
                    );
    }

    /**
     *
     * @param optionList
     * @param productOption
     *
     * 옵션 리스트와 옵션별 집계 리스트를 매핑.
     */
    public List<AdminProductSalesOptionDTO> optionDataMapping(List<AdminProductSalesOptionDTO> optionList, List<ProductOption> productOption) {
        List<AdminProductSalesOptionDTO> returnDTOList = new ArrayList<>();

        if(productOption.size() != optionList.size()){
            for(int i = 0; i < productOption.size(); i++){
                ProductOption option = productOption.get(i);
                AdminProductSalesOptionDTO optionDTO = new AdminProductSalesOptionDTO(
                                                                    option
                                                                    , 0
                                                                    , 0
                                                            );
                for(int j = 0; j < optionList.size(); j++) {
                    if(option.getId() == optionList.get(j).optionId()){
                        optionDTO = optionList.get(j);
                        break;
                    }
                }
                returnDTOList.add(optionDTO);
            }

            return returnDTOList;
        }else
            return optionList;
    }
}

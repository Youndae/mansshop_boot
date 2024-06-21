package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.config.customException.exception.CustomNotFoundException;
import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.*;
import com.example.mansshop_boot.domain.dto.mypage.qna.req.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.*;
import com.example.mansshop_boot.domain.entity.*;
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
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private final MyPageService myPageService;

    private static final String adminNickname = "관리자";

    @Override
    public PagingResponseDTO<AdminProductListDTO> getProductList(AdminPageDTO pageDTO) {

        log.info("pageDTO : {}", pageDTO);

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("createdAt").descending());

        Page<AdminProductListDTO> dto = productRepository.findAdminProductList(pageDTO, pageable);

        log.info("dto.content : {}", dto.getContent());

        return new PagingResponseDTO<>(dto, adminNickname);
    }

    @Override
    public ResponseDTO<AdminProductDetailDTO> getProductDetail(String productId) {

        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        List<AdminProductOptionDTO> productOptionList = productOptionRepository.findAllByProductId(productId);
        List<String> thumbnailList = productThumbnailRepository.findByProductId(productId);
        List<String> infoImageList = productInfoImageRepository.findByProductId(productId);

        AdminProductDetailDTO detailDTO = AdminProductDetailDTO.builder()
                .classification(product.getClassification().getId())
                .productName(product.getProductName())
                .firstThumbnail(product.getThumbnail())
                .thumbnailList(thumbnailList)
                .infoImageList(infoImageList)
                .optionList(productOptionList)
                .price(product.getProductPrice())
                .isOpen(product.isOpen())
                .sales(product.getProductSales())
                .discount(product.getProductDiscount())
                .build();

        return new ResponseDTO<>(detailDTO, new UserStatusDTO(adminNickname));
    }

    @Override
    public AdminProductPatchDataDTO getPatchProductData(String productId, Principal principal) {

        String nickname = principalService.getNicknameByPrincipal(principal);
        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        List<AdminProductOptionDTO> optionList = productOptionRepository.findAllByProductId(productId);
        List<String> thumbnailList = productThumbnailRepository.findByProductId(productId);
        List<String> infoImageList = productInfoImageRepository.findByProductId(productId);
        List<Classification> entity = classificationRepository.findAll(Sort.by("classificationStep").descending());
        List<String> classificationList = new ArrayList<>();
        entity.forEach(en ->
                classificationList.add(en.getId())
        );

        return AdminProductPatchDataDTO.builder()
                .product(product)
                .optionList(optionList)
                .thumbnailList(thumbnailList)
                .infoImageList(infoImageList)
                .classificationList(classificationList)
                .nickname(nickname)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseIdDTO<String> postProduct(AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {

        Product product = patchDTO.toPostEntity();
        System.out.println("AdminService.postProduct :: product : " + product);
        product.setThumbnail(imageInsert(imageDTO.getFirstThumbnail()));
        productRepository.save(product);

        List<ProductOption> optionList = patchDTO.getProductOptionList(product);
        productOptionRepository.saveAll(optionList);

        saveAndDeleteProductImage(product, imageDTO);

        return new ResponseIdDTO<String>(product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseIdDTO<String> patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {

        Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        product.setPatchData(patchDTO);

        if(imageDTO.getFirstThumbnail() != null)
            product.setThumbnail(imageInsert(imageDTO.getFirstThumbnail()));

        productRepository.save(product);

        List<ProductOption> optionList = patchDTO.getProductOptionList(product);
        productOptionRepository.saveAll(optionList);
        productOptionRepository.deleteAllById(deleteOptionList);

        saveAndDeleteProductImage(product, imageDTO);

        return new ResponseIdDTO<String>(productId);
    }

    public void saveAndDeleteProductImage(Product product, AdminProductImageDTO imageDTO){
        List<ProductThumbnail> thumbnailEntityList = new ArrayList<>();
        List<ProductInfoImage> infoImageEntityList = new ArrayList<>();

        if(imageDTO.getThumbnail() != null){
            for(int i = 0; i < imageDTO.getThumbnail().size(); i++) {
                thumbnailEntityList.add(
                        ProductThumbnail.builder()
                                .product(product)
                                .imageName(imageInsert(imageDTO.getThumbnail().get(i)))
                                .build()
                );
            }

            productThumbnailRepository.saveAll(thumbnailEntityList);
        }

        if(imageDTO.getInfoImage() != null){
            for(int i = 0; i < imageDTO.getInfoImage().size(); i++) {
                infoImageEntityList.add(
                        ProductInfoImage.builder()
                                .product(product)
                                .imageName(imageInsert(imageDTO.getInfoImage().get(i)))
                                .build()
                );
            }

            productInfoImageRepository.saveAll(infoImageEntityList);
        }

        if(imageDTO.getDeleteFirstThumbnail() != null)
            deleteImage(imageDTO.getDeleteFirstThumbnail());

        if(imageDTO.getDeleteThumbnail() != null){
            List<String> deleteList = imageDTO.getDeleteThumbnail();
            productThumbnailRepository.deleteByImageName(deleteList);
            deleteList.forEach(this::deleteImage);
        }

        if(imageDTO.getDeleteInfoImage() != null) {
            List<String> deleteList = imageDTO.getDeleteInfoImage();
            productInfoImageRepository.deleteByImageName(deleteList);
            deleteList.forEach(this::deleteImage);
        }
    }

    public String imageInsert(MultipartFile image) {
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")))
                .toString();
        String saveFile = filePath + saveName;

        try {
            image.transferTo(new File(saveFile));
        }catch (Exception e){
            log.warn("productImage insert IOException");
            e.printStackTrace();
            throw new NullPointerException();
        }

        return saveName;
    }

    public void deleteImage(String imageName) {
        File file = new File(filePath + imageName);

        if(file.exists())
            file.delete();
    }

    @Override
    public PagingResponseDTO<AdminProductStockDTO> getProductStock(AdminPageDTO pageDTO) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("totalStock").ascending());

        Page<AdminProductStockDataDTO> dataList = productRepository.findStockData(pageDTO, pageable);
        List<String> productIdList = new ArrayList<>();
        dataList.getContent().forEach(dto -> productIdList.add(dto.productId()));


        List<ProductOption> optionList = productOptionRepository.findAllOptionByProductId(productIdList);

        List<AdminProductStockDTO> responseContent = new ArrayList<>();
        List<AdminProductOptionStockDTO> responseOptionList = new ArrayList<>();

        for(int i = 0; i < dataList.getContent().size(); i++) {
            AdminProductStockDataDTO stockDTO = dataList.getContent().get(i);
            String productId = stockDTO.productId();

            for(int j = 0; j < optionList.size(); j++) {
                if(productId.equals(optionList.get(j).getProduct().getId())){
                    ProductOption productOption = optionList.get(j);
                    responseOptionList.add(
                            AdminProductOptionStockDTO.builder()
                                    .size(productOption.getSize())
                                    .color(productOption.getColor())
                                    .optionStock(productOption.getStock())
                                    .optionIsOpen(productOption.isOpen())
                                    .build()
                    );
                }
            }

            responseContent.add(
                    AdminProductStockDTO.builder()
                            .productId(productId)
                            .classification(stockDTO.classification())
                            .productName(stockDTO.productName())
                            .totalStock(stockDTO.totalStock())
                            .isOpen(stockDTO.isOpen())
                            .optionList(responseOptionList)
                            .build()
            );

            responseOptionList = new ArrayList<>();
        }

        return new PagingResponseDTO<>(responseContent, dataList.isEmpty(), dataList.getNumber(), dataList.getTotalPages(), adminNickname);
    }

    @Override
    public PagingResponseDTO<AdminDiscountResponseDTO> getDiscountProduct(AdminPageDTO pageDTO) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("updatedAt").descending());

        Page<Product> entityList = productRepository.getDiscountProduct(pageDTO, pageable);
        List<AdminDiscountResponseDTO> responseDTOList = new ArrayList<>();

        int discount = 30;

        entityList.getContent().forEach(entity ->
                    responseDTOList.add(
                            AdminDiscountResponseDTO.builder()
                                    .productId(entity.getId())
                                    .classification(entity.getClassification().getId())
                                    .productName(entity.getProductName())
                                    .price(entity.getProductPrice())
                                    .discount(entity.getProductDiscount())
                                    .build()
                    )
                );


        return new PagingResponseDTO<>(responseDTOList, entityList.isEmpty(), entityList.getNumber(), entityList.getTotalPages(), adminNickname);
    }

    @Override
    public ResponseListDTO<String> getClassification() {

        List<Classification> classification = classificationRepository.findAll(Sort.by("classificationStep").ascending());
        List<String> responseList = new ArrayList<>();

        classification.forEach(entity -> responseList.add(entity.getId()));

        return new ResponseListDTO<>(responseList, new UserStatusDTO(adminNickname));
    }

    @Override
    public ResponseListDTO<AdminDiscountProductDTO> getSelectDiscountProduct(String classification) {

        List<Product> product = productRepository.getProductByClassification(classification);
        List<AdminDiscountProductDTO> responseContent = new ArrayList<>();

        product.forEach(entity -> responseContent.add(
                AdminDiscountProductDTO.builder()
                        .productName(entity.getProductName())
                        .productId(entity.getId())
                        .productPrice(entity.getProductPrice())
                        .build()
        ));

        return new ResponseListDTO<>(responseContent, new UserStatusDTO(adminNickname));
    }

    @Override
    public ResponseMessageDTO patchDiscountProduct(AdminDiscountPatchDTO patchDTO) {

        productRepository.patchProductDiscount(patchDTO);

        return new ResponseMessageDTO(Result.OK.getResultKey());
    }

    @Override
    public PagingResponseDTO<AdminOrderResponseDTO> getAllOrderList(AdminOrderPageDTO pageDTO) {

        //Page<dto> orderList
        //orderIdList
        //select orderDetail orderIdList
        //mapping

        log.info("AdminServiceImpl.getAllOrderList :: pageDTO : {}", pageDTO);

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                        , pageDTO.amount()
                                        , Sort.by("createdAt").descending());

        Page<AdminOrderDTO> orderDTOList = productOrderRepository.findAllOrderList(pageDTO, pageable);

        log.info("AdminServiceImpl.getAllOrderList :: orderDTOList : {}", orderDTOList);
        log.info("AdminServiceImpl.getAllOrderList :: orderDTOList.getContent() : {}", orderDTOList.getContent());

        List<AdminOrderResponseDTO> responseContent = mappingOrderData(orderDTOList);

        log.info("AdminServiceImpl.getAllOrderList :: responseContent : {}", responseContent);



        return new PagingResponseDTO<>(
                responseContent
                , orderDTOList.isEmpty()
                , orderDTOList.getNumber()
                , orderDTOList.getTotalPages()
                , adminNickname
        );
    }

    @Override
    public PagingResponseDTO<AdminOrderResponseDTO> getNewOrderList(AdminOrderPageDTO pageDTO) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("createdAt").descending());

        LocalDate today = LocalDate.now();
        LocalDateTime todayLastOrderTime = LocalDateTime.of(
                today.getYear()
                , today.getMonth()
                , today.getDayOfMonth()
                , 16
                , 0
                , 0
        );

        Page<AdminOrderDTO> orderDTOList = productOrderRepository.findAllNewOrderList(pageDTO, todayLastOrderTime, pageable);
        List<AdminOrderResponseDTO> responseContent = mappingOrderData(orderDTOList);

        return new PagingResponseDTO<>(
                responseContent
                , orderDTOList.isEmpty()
                , orderDTOList.getNumber()
                , orderDTOList.getTotalPages()
                , adminNickname
        );
    }

    private List<AdminOrderResponseDTO> mappingOrderData(Page<AdminOrderDTO> orderDTOList) {
        List<Long> orderIdList = new ArrayList<>();
        orderDTOList.getContent().forEach(dto -> orderIdList.add(dto.orderId()));
        List<ProductOrderDetail> detailList = productOrderDetailRepository.findAllById(orderIdList);
        List<AdminOrderDetailDTO> detailDTOList = new ArrayList<>();
        List<AdminOrderResponseDTO> responseContent = new ArrayList<>();

        for(int i = 0; i < orderDTOList.getContent().size(); i++) {
            AdminOrderDTO orderDTO = orderDTOList.getContent().get(i);
            long orderId = orderDTO.orderId();

            for(int j = 0; j < detailList.size(); j++) {
                if(orderId == detailList.get(j).getProductOrder().getId()){
                    detailDTOList.add(
                            new AdminOrderDetailDTO(detailList.get(j))
                    );
                }
            }

            responseContent.add(orderDTO.toResponseDTO(detailDTOList));
            detailDTOList = new ArrayList<>();
        }


        return responseContent;
    }

    @Override
    public PagingResponseDTO<AdminQnAListResponseDTO> getProductQnAList(AdminPageDTO pageDTO, String listType) {

        /*
            select *
            from productQnA
            where stat = 0

            all
            where x
         */

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                            , pageDTO.amount()
                                            , Sort.by("createdAt").descending());

        Page<AdminQnAListResponseDTO> responseDTO = productQnARepository.findAllByAdminProductQnA(pageDTO, listType, pageable);


        return new PagingResponseDTO<>(responseDTO, adminNickname);
    }

    @Override
    public ProductQnADetailDTO getProductQnADetail(long qnaId) {

        MyPageProductQnADTO qnaDTO = productQnARepository.findByQnAId(qnaId);

        List<MyPageQnAReplyDTO> replyDTOList = productQnAReplyRepository.findAllByQnAId(qnaId);

        return new ProductQnADetailDTO(qnaDTO, replyDTOList, adminNickname);
    }

    @Override
    public String patchProductQnAComplete(long qnaId) {

        ProductQnA productQnA = productQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        productQnA.setProductQnAStat(true);
        productQnARepository.save(productQnA);

        return Result.OK.getResultKey();
    }

    @Override
    public String postProductQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {

        String postReplyResult = myPageService.postProductQnAReply(insertDTO, principal);

        if(postReplyResult.equals(Result.OK.getResultKey())){
            return patchProductQnAComplete(insertDTO.qnaId());
        }else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public PagingResponseDTO<AdminQnAListResponseDTO> getMemberQnAList(AdminPageDTO pageDTO, String listType) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("createdAt").descending());

        Page<AdminQnAListResponseDTO> responseDTO = memberQnARepository.findAllByAdminMemberQnA(pageDTO, listType, pageable);


        return new PagingResponseDTO<>(responseDTO, adminNickname);
    }

    @Override
    public MemberQnADetailDTO getMemberQnADetail(long qnaId) {

        MemberQnADTO qnaDTO = memberQnARepository.findByQnAId(qnaId);

        List<MyPageQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(qnaId);

        return new MemberQnADetailDTO(qnaDTO, replyDTOList, adminNickname);
    }

    @Override
    public String patchMemberQnAComplete(long qnaId) {
        MemberQnA memberQnA = memberQnARepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);

        memberQnA.setMemberQnAStat(true);
        memberQnARepository.save(memberQnA);

        return Result.OK.getResultKey();
    }

    @Override
    public String postMemberQnAReply(QnAReplyInsertDTO insertDTO, Principal principal) {

        log.info("AdminServiceImpl.postMemberQnAReply :: insertDTO : {}", insertDTO);

        String postReplyResult = myPageService.postMemberQnAReply(insertDTO, principal);

        if(postReplyResult.equals(Result.OK.getResultKey())){
            return patchMemberQnAComplete(insertDTO.qnaId());
        }else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public ResponseListDTO<AdminQnAClassificationDTO> getQnAClassification() {

        List<QnAClassification> entity = qnAClassificationRepository.findAll(Sort.by("id").ascending());
        List<AdminQnAClassificationDTO> responseList = new ArrayList<>();

        entity.forEach(value ->
                responseList.add(AdminQnAClassificationDTO.builder()
                        .id(value.getId())
                        .name(value.getQnaClassificationName())
                        .build())
        );


        return new ResponseListDTO<>(responseList, new UserStatusDTO(adminNickname));
    }

    @Override
    public String postQnAClassification(String classificationName) {

        QnAClassification entity = QnAClassification.builder()
                .qnaClassificationName(classificationName)
                .build();

        qnAClassificationRepository.save(entity);

        return Result.OK.getResultKey();
    }

    @Override
    public String deleteQnAClassification(long classificationId) {

        qnAClassificationRepository.deleteById(classificationId);

        return Result.OK.getResultKey();
    }

    @Override
    public PagingResponseDTO<AdminMemberDTO> getMemberList(AdminPageDTO pageDTO) {

        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                                        , pageDTO.amount()
                                        , Sort.by("createdAt").descending());

        Page<AdminMemberDTO> responseContent = memberRepository.findMember(pageDTO, pageable);

        return new PagingResponseDTO<>(responseContent, adminNickname);
    }
}

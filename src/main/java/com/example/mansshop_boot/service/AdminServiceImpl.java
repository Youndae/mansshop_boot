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

    @Override
    public ResponseDTO<AdminPeriodSalesResponseDTO> getPeriodSales(int term) {
        /*
            content 리스트 조회
            productOrderRepository.findPeriodList(dateTime);

            해당연도의 총 매출
            해당 연도의 판매량
            해당 연도의 주문량

            1. term 연도에 따른 LocalDateTime 값 생성
            2. 리스트 조회(findPeriodList)
            3. 1 ~ 12까지 반복문을 돌면서 content 리스트 생성 -> 프론트에서 따로 처리하지 않도록 하기 위해 백에서 모두 파싱해주고 넘긴다.
                3-1. 반복문을 수행하면서 매출, 판매량, 주문량을 계속해서 ++
            4. 반복문 종료 후 responseDTO 빌드
            5. 반환

            사용 DTO
                조회와 리스트로 처리될 contentDTO
                    int month
                    long monthSales
                    long monthDeliveryFee
                    long monthSalesQuantity
                    long monthOrderQuantity
                반환될 responseDTO
                    List<contentDTO>
                    long yearSales
                    long yearDeliveryFee
                    long yearSalesQuantity
                    long yearOrderQuantity
         */


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



        return new ResponseDTO<AdminPeriodSalesResponseDTO>(
                new AdminPeriodSalesResponseDTO(
                        contentList
                        , yearSales
                        , yearSalesQuantity
                        , yearOrderQuantity
                ),
                new UserStatusDTO(adminNickname)
        );
    }

    @Override
    public ResponseDTO<AdminPeriodMonthDetailResponseDTO> getPeriodSalesDetail(String term) {

        /*
            term format = "YYYY-MM"

            splice["YYYY", "MM"];
            Integer.parseInt([0], [1]);

            LocalDateTime create

            응답할 데이터
                월 매출
                월 판매량
                월 주문량
                전년동월 매출 비교
                전년동월 판매량 비교
                전년동월 주문량 비교

                List<?> 최고 판매량 상품. size = 5
                    상품명
                    판매량
                    매출

                List<?> 분류별 매출 정보(view table?)
                    OUTER
                    TOP
                    PANTS
                    SHOES
                    BAGS
                        매출
                        판매량

                List<?> 일별 매출 정보. size = 28 ~ 31
                    날짜 ("YYYY-MM-DD")
                    매출
                    판매량

            1. 월 정보 조회. group by로 통계만 필요. 전년동월 데이터 조회까지 2번 조회 요청.
            2. productOrderDetail에서 productId를 기준으로 판매량 역순 정렬 limit 5
            3.      ""               productId 를 통해 classification 테이블과 조인, 그 상태에서 group by로 통계
            4. productOrder에서 createdAt을 기준으로 group by. 월별 조회와 동일하게 처리하되 조회 기준만 일별로 수정.

            사용 DTO
                responseDTO
                    long monthSales
                    long monthSalesQuantity
                    long monthOrderQuantity
                    long beforeYearComparison

                    List<?> bestProduct
                    List<?> classificationSalesList
                    List<?> dailySalesList

                return ResponseDTO
         */

        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);

        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        int lastDay = startDate.getMonth().length(startDate.toLocalDate().isLeapYear());
        LocalDateTime endDate = LocalDateTime.of(
                year
                , month
                , lastDay
                , 0
                , 0
        );

        AdminPeriodSalesStatisticsDTO monthStatistics = productOrderRepository.findPeriodStatistics(startDate, endDate);
        List<AdminBestSalesProductDTO> bestProductList = productOrderDetailRepository.findPeriodBestProduct(startDate, endDate);
        List<AdminPeriodClassificationDTO> classificationList = productOrderDetailRepository.findPeriodClassification(startDate, endDate);
        List<Classification> classification = classificationRepository.findAll(Sort.by("classificationStep").descending());
        List<AdminPeriodSalesListDTO> dailySalesList = productOrderRepository.findPeriodDailyList(startDate, endDate);

        startDate = startDate.minusYears(1);
        endDate = endDate.minusYears(1);

        AdminPeriodSalesStatisticsDTO lastYearStatistics = productOrderRepository.findPeriodStatistics(startDate, endDate);

        List<AdminPeriodClassificationDTO> classificationResponseDTO = new ArrayList<>();
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

        List<AdminPeriodSalesListDTO> dailySalesResponseDTO = new ArrayList<>();

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

        AdminPeriodMonthDetailResponseDTO responseContent = new AdminPeriodMonthDetailResponseDTO(
                                                                    monthStatistics
                                                                    , lastYearStatistics
                                                                    , bestProductList
                                                                    , classificationResponseDTO
                                                                    , dailySalesResponseDTO
                                                            );




        return new ResponseDTO<AdminPeriodMonthDetailResponseDTO>(responseContent, new UserStatusDTO(adminNickname));
    }

    @Override
    public ResponseDTO<AdminClassificationSalesResponseDTO> getSalesByClassification(String term, String classification) {

        /*
            상품 분류별 월 매출 정보 조회.

            term = "YYYY-MM"

            월별 상세와 마찬가지로 LocalDateTime 생성

            해당 기간에 속하는 데이터 group by로 통계.
            상품명을 기준으로 처리하되 option을 같이 조회.

            조회 데이터
                상품명
                사이즈
                컬러
                판매량
                매출

            조회 이후 반복문으로 훑으면서 전체 매출과 판매량 연산 후 반환

            사용 DTO
                responseDTO
                    String classification
                    long totalSales
                    long totalSalesQuantity
                    List<?> productList
                        String productName
                        String size
                        String color
                        long productSales
                        long productSalesQuantity

                return ResponseDTO
         */

        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);

        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        int lastDay = startDate.getMonth().length(startDate.toLocalDate().isLeapYear());
        LocalDateTime endDate = LocalDateTime.of(
                year
                , month
                , lastDay
                , 0
                , 0
        );

        AdminClassificationSalesDTO classificationSalesDTO = productOrderDetailRepository.findPeriodClassificationSales(startDate, endDate, classification);
        List<AdminClassificationSalesProductListDTO> productList = productOrderDetailRepository.findPeriodClassificationProductSales(startDate, endDate, classification);

        AdminClassificationSalesResponseDTO responseContent = new AdminClassificationSalesResponseDTO(classification, classificationSalesDTO, productList);

        return new ResponseDTO<AdminClassificationSalesResponseDTO>(responseContent, new UserStatusDTO(adminNickname));
    }

    @Override
    public ResponseDTO<AdminPeriodSalesResponseDTO> getSalesByDay(String term) {

        /*
            일 매출 정보

            월 매출 정보와 마찬가지로 출력.
            일 매출에 대한 집계와 각 상품 분류별 집계를 반환.

            term = "YYYY-MM-DD"

            해당일의 데이터를 group by로 집계
            상품 분류별 데이터를 집계

            사용 DTO
                responseDTO
                    long totalSales
                    long totalSalesQuantity
                    long totalOrderQuantity

                    List<?> classificationSalesList(월 매출 정보 반환시 사용한 DTO 재활용)
                        classificationName
                        classificationSales
                        classificationSalesQuantity

            return ResponseDTO
         */
        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);
        int day = Integer.parseInt(termSplit[2]);

        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59, 59, 999999999);

        AdminClassificationSalesDTO salesDTO = productOrderRepository.findDailySales(startDate, endDate);
        List<AdminPeriodClassificationDTO> classificationList = productOrderDetailRepository.findPeriodClassification(startDate, endDate);

        return new ResponseDTO<>(
                new AdminPeriodSalesResponseDTO(
                        classificationList
                        , salesDTO.sales()
                        , salesDTO.salesQuantity()
                        , salesDTO.orderQuantity()
                )
                , new UserStatusDTO(adminNickname)
        );
    }

    @Override
    public PagingResponseDTO<AdminDailySalesResponseDTO> getOrderListByDay(String term, int page) {

        /*
            해당 날짜의 주문 내역 조회

            term = "YYYY-MM-DD"

            해당 일의 모든 주문 내역 조회(ProductOrder)
            조회된 내역의 id를 리스트화
            리스트를 통해 ProductOrderDetail 조회

            두 데이터를 매핑 후 전달.
            매핑 시 판매량, 매출, 주문량, 카드 결제, 현금 결제 연산

            사용 DTO
                responseDTO
                    long totalSales
                    long totalSalesQuantity
                    long totalOrderQuantity

                    List<?> content(이것도 재활용할 DTO 있을듯?)
                        long orderTotalPrice
                        long deliveryFee
                        String paymentType
                        List<?> detailList(재활용 할 DTO가 있지 않을까?)
                            productName
                            size
                            color
                            count
                            price
                    pagingData

            return PagingResponseDTO(새로 생성 필요)
         */

        String[] termSplit = term.split("-");
        int year = Integer.parseInt(termSplit[0]);
        int month = Integer.parseInt(termSplit[1]);
        int day = Integer.parseInt(termSplit[2]);

        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59, 59, 999999999);

        Pageable pageable = PageRequest.of(page - 1
                                        , 30
                                        , Sort.by("createdAt").descending());

        AdminClassificationSalesDTO salesDTO = productOrderRepository.findDailySales(startDate, endDate);
        Page<ProductOrder> orderList = productOrderRepository.findAllByDay(startDate, endDate, pageable);
        List<Long> orderIdList = new ArrayList<>();
        orderList.forEach(data -> orderIdList.add(data.getId()));
        List<ProductOrderDetail> orderDetailList = productOrderDetailRepository.findByOrderIds(orderIdList);

        List<AdminDailySalesResponseDTO> content = new ArrayList<>();
        List<AdminDailySalesDetailDTO> detailContent = new ArrayList<>();

        for(int i = 0; i < orderList.getContent().size(); i++) {
            ProductOrder productOrder = orderList.getContent().get(i);

            for(int j = 0; j < orderDetailList.size(); j++) {
                if(productOrder.getId() == orderDetailList.get(j).getProductOrder().getId()){
                    ProductOrderDetail productOrderDetail = orderDetailList.get(j);
                    detailContent.add(
                            new AdminDailySalesDetailDTO(
                                    productOrderDetail.getProduct().getProductName()
                                    , productOrderDetail.getProductOption().getSize()
                                    , productOrderDetail.getProductOption().getColor()
                                    , productOrderDetail.getOrderDetailCount()
                                    , productOrderDetail.getOrderDetailPrice()
                            )
                    );
                }
            }

            content.add(
                    new AdminDailySalesResponseDTO(
                            productOrder.getOrderTotalPrice()
                            , productOrder.getDeliveryFee()
                            , productOrder.getPaymentType()
                            , detailContent
                    )
            );
            detailContent = new ArrayList<>();
        }


        return new PagingResponseDTO<AdminDailySalesResponseDTO>(
                content
                , orderList.isEmpty()
                , orderList.getNumber()
                , orderList.getTotalPages()
                , adminNickname
        );
    }
}

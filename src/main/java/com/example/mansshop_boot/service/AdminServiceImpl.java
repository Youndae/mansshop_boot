package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.member.UserStatusDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.response.PagingResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseListDTO;
import com.example.mansshop_boot.domain.entity.*;
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
    public ResponseListDTO<String> getClassification(Principal principal) {
        String nickname = principalService.getNicknameByPrincipal(principal);

        List<Classification> entity = classificationRepository.findAll(Sort.by("classificationStep").descending());
        List<String> responseList = new ArrayList<>();

        entity.forEach(value -> responseList.add(value.getId()));


        return new ResponseListDTO<>(responseList, new UserStatusDTO(nickname));
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
}

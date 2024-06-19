package com.example.mansshop_boot.domain.dto.admin;

import com.example.mansshop_boot.domain.entity.Classification;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdminProductPatchDTO {

    private String productName;

    private String classification;

    private int price;

    private Boolean isOpen;

    private int discount;

    private List<PatchOptionDTO> optionList;

    public Product toPostEntity() {
        StringBuffer sb = new StringBuffer();
        return Product.builder()
                .id(
                        sb.append(classification)
                            .append(
                                    new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
                            )
                            .toString()
                )
                .classification(Classification.builder().id(classification).build())
                .productName(productName)
                .productPrice(price)
                .isOpen(isOpen)
                .productSales(0L)
                .productDiscount(discount)
                .build();
    }

    public List<ProductOption> getProductOptionList(Product product) {
        List<ProductOption> responseList = new ArrayList<>();
        this.getOptionList().forEach(option -> responseList.add(option.toEntity(product)));

        return responseList;
    }
}

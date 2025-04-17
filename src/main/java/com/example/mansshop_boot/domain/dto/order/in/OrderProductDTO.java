package com.example.mansshop_boot.domain.dto.order.in;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderProductDTO{
        @Schema(name = "optionId", description = "상품 옵션 아이디")
        private long optionId;
        @Schema(name = "productName", description = "상품명")
        private String productName;
        @Schema(name = "productId", description = "상품 아이디")
        private String productId;
        @Schema(name = "detailCount", description = "상품 수량")
        private Integer detailCount;
        @Schema(name = "detailPrice", description = "상품 총 가격")
        private int detailPrice;

    public ProductOrderDetail toOrderDetailEntity() {
        return ProductOrderDetail.builder()
                .productOption(
                        ProductOption.builder()
                                .id(optionId)
                                .build()
                )
                .product(
                        Product.builder()
                                .id(productId)
                                .build()
                )
                .orderDetailCount(detailCount)
                .orderDetailPrice(detailPrice)
                .build();
    }
}

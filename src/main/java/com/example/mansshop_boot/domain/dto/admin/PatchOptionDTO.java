package com.example.mansshop_boot.domain.dto.admin;

import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PatchOptionDTO {

    private Long optionId;

    private String size;

    private String color;

    private int optionStock;

    private boolean optionIsOpen;

    public ProductOption toEntity(Product product) {
        return ProductOption.builder()
                .id(optionId == 0 ? null : optionId)
                .product(product)
                .size(size)
                .color(color)
                .stock(optionStock)
                .isOpen(isOptionIsOpen())
                .build();
    }
}

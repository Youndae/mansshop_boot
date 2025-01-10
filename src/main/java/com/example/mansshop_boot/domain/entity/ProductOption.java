package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.admin.business.PatchOptionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    private String size;

    private String color;

    private int stock;

    private boolean isOpen;

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void patchOptionData(PatchOptionDTO dto) {
        this.size = dto.getSize();
        this.color = dto.getColor();
        this.stock = dto.getOptionStock();
        this.isOpen = dto.isOptionIsOpen();
    }
}

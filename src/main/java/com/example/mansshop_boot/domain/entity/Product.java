package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.admin.in.AdminProductPatchDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "product")
public class Product {

    @Id
    @Column(length = 200)
    private String id;

    @ManyToOne
    @JoinColumn(name = "classificationId", nullable = false)
    private Classification classification;

    @Column(length = 200,
            nullable = false
    )
    private String productName;

    private int productPrice;

    @Column(length = 200,
            nullable = false
    )
    private String thumbnail;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1",
            nullable = false
    )
    private boolean isOpen;

    @Column(columnDefinition = "BIGINT DEFAULT 0",
            nullable = false
    )
    private Long productSales;

    @Column(columnDefinition = "INT DEFAULT 0",
            nullable = false
    )
    private int productDiscount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private final List<ProductOption> productOptionSet = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private final List<ProductThumbnail> productThumbnailSet = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private final List<ProductInfoImage> productInfoImageSet = new ArrayList<>();

    public void addProductOption(ProductOption productOption) {
        productOptionSet.add(productOption);
        productOption.setProduct(this);
    }

    public void addProductThumbnail(ProductThumbnail productThumbnail) {
        productThumbnailSet.add(productThumbnail);
        productThumbnail.setProduct(this);
    }

    public void addProductInfoImage(ProductInfoImage productInfoImage) {
        productInfoImageSet.add(productInfoImage);
        productInfoImage.setProduct(this);
    }

    public void setProductSales(long productSales) {
        this.productSales = productSales;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setId(String productId){
        this.id = productId;
    }

    public void setPatchData(AdminProductPatchDTO patchDTO) {
        this.productName = patchDTO.getProductName();
        this.classification = Classification.builder().id(patchDTO.getClassification()).build();
        this.productPrice = patchDTO.getPrice();
        this.isOpen = patchDTO.getIsOpen();
        this.productDiscount = patchDTO.getDiscount();
    }
}

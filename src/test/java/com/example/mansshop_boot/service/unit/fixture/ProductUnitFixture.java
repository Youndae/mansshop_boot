package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.Fixture.ProductFixture;
import com.example.mansshop_boot.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

public class ProductUnitFixture {

    public static List<Product> createSaveProductList(int count, String classification) {
        List<Product> result = new ArrayList<>();

        for(int i = 1; i <= count; i++) {
            Product product = createProduct(i, Classification.builder().id(classification).build());
            createSaveOptionThumbnailAndInfoImage(product, i * 3);

            result.add(product);
        }

        return result;
    }

    private static Product createProduct(int i, Classification classification) {
        return Product.builder()
                .id("testProduct" + i)
                .classification(classification)
                .productName("testProduct" + i + "Name")
                .productPrice(i * 1000)
                .thumbnail("testProductThumbnail.jpg")
                .isOpen(true)
                .productSalesQuantity(i * 10L)
                .productDiscount(i < 10 ? 0 : i / 2)
                .build();
    }

    private static void createSaveOptionThumbnailAndInfoImage(Product product, int count) {
        List<ProductOption> options = createSaveProductOption(count);
        List<ProductThumbnail> thumbnails = ProductFixture.createSaveProductThumbnail(product.getProductName(), count);
        List<ProductInfoImage> infoImages = ProductFixture.createSaveProductInfoImage(product.getProductName(), count);

        options.forEach(product::addProductOption);
        thumbnails.forEach(product::addProductThumbnail);
        infoImages.forEach(product::addProductInfoImage);
    }

    private static List<ProductOption> createSaveProductOption(int count) {
        List<ProductOption> options = new ArrayList<>();

        for(int i = count - 3; i < count; i++) {
            options.add(
                    ProductOption.builder()
                            .id((long) i)
                            .size("size" + i)
                            .color("color" + i)
                            .stock(i * 10)
                            .isOpen(i % 2 != 0)
                            .build()
            );
        }

        return options;
    }
}

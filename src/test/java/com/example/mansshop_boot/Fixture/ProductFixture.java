package com.example.mansshop_boot.Fixture;


import com.example.mansshop_boot.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

public class ProductFixture {

    public static List<Product> createDefaultProductByOUTER(int count) {
        List<Product> result = new ArrayList<>();
        for(int i = 1; i <= count; i++) {
            Product product = createProduct(i, Classification.builder().id("OUTER").build());
            createOptionAndThumbnailAndInfoImage(product);

            result.add(product);
        }

        return result;
    }

    public static List<Product> createSaveProductList(int count, Classification classification) {
        List<Product> result = new ArrayList<>();

        for(int i = 1; i <= count; i++) {
            Product product = createProduct(i, classification);
            createSaveOptionThumbnailAndInfoImage(product, i * 3);

            result.add(product);
        }

        return result;
    }

    private static void createSaveOptionThumbnailAndInfoImage(Product product, int count) {
        List<ProductOption> options = createSaveProductOption(count);
        List<ProductThumbnail> thumbnails = createSaveProductThumbnail(product.getProductName(), count);
        List<ProductInfoImage> infoImages = createSaveProductInfoImage(product.getProductName(), count);

        options.forEach(product::addProductOption);
        thumbnails.forEach(product::addProductThumbnail);
        infoImages.forEach(product::addProductInfoImage);
    }

    private static List<ProductOption> createSaveProductOption(int count) {
        List<ProductOption> options = new ArrayList<>();

        for(int i = count - 3; i < count; i++) {
            options.add(
                    ProductOption.builder()
                            .size("size" + i)
                            .color("color" + i)
                            .stock(i * 10)
                            .isOpen(i % 2 != 0)
                            .build()
            );
        }

        return options;
    }

    public static List<ProductThumbnail> createSaveProductThumbnail(String productName, int count) {
        List<ProductThumbnail> thumbnails = new ArrayList<>();

        for(int i = count - 3; i < count; i++) {
            thumbnails.add(
                    ProductThumbnail.builder()
                            .imageName(productName + "thumbnail" + i + ".jpg")
                            .build()
            );
        }

        return thumbnails;
    }

    public static List<ProductInfoImage> createSaveProductInfoImage(String productName, int count) {
        List<ProductInfoImage> infoImages = new ArrayList<>();

        for(int i = count - 3; i < count; i++) {
            infoImages.add(
                    ProductInfoImage.builder()
                            .imageName(productName + "infoImage" + i + ".jpg")
                            .build()
            );
        }

        return infoImages;
    }

    public static List<Product> createProductByClassificationName (int startIdx, int endIdx, String classificationId) {
        List<Product> result = new ArrayList<>();
        for(int i = startIdx; i <= startIdx + endIdx; i++) {
            Product product = createProduct(i, Classification.builder().id(classificationId).build());
            createOptionAndThumbnailAndInfoImage(product);

            result.add(product);
        }

        return result;
    }

    private static void createOptionAndThumbnailAndInfoImage(Product product) {
        List<ProductOption> options = createProductOption();
        List<ProductThumbnail> thumbnails = createProductThumbnail(product.getProductName());
        List<ProductInfoImage> infoImages = createInfoImages(product.getProductName());

        options.forEach(product::addProductOption);
        thumbnails.forEach(product::addProductThumbnail);
        infoImages.forEach(product::addProductInfoImage);
    }

    private static List<ProductInfoImage> createInfoImages(String productName) {
        List<ProductInfoImage> infoImages = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            infoImages.add(
                    ProductInfoImage.builder()
                            .imageName(productName + "infoImage" + i + ".jpg")
                            .build()
            );
        }

        return infoImages;
    }

    private static List<ProductThumbnail> createProductThumbnail(String productName) {
        List<ProductThumbnail> thumbnails = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            thumbnails.add(
                    ProductThumbnail.builder()
                            .imageName(productName + "thumbnail" + i + ".jpg")
                            .build()
            );
        }

        return thumbnails;
    }

    private static List<ProductOption> createProductOption() {
        List<ProductOption> options = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            options.add(
                    ProductOption.builder()
                            .size("size" + i)
                            .color("color" + i)
                            .stock(i * 10)
                            .isOpen(i % 2 != 0)
                            .build()
            );
        }

        return options;
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
}

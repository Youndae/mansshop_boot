package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductLike;

import java.util.ArrayList;
import java.util.List;

public class ProductLikeFixture {

    public static List<ProductLike> createDefaultProductLike(List<Member> members, List<Product> products) {
        List<ProductLike> result = new ArrayList<>();

        for(Member m : members) {
            for(Product p : products) {
                result.add(
                        ProductLike.builder()
                                .member(m)
                                .product(p)
                                .build()
                );
            }
        }

        return result;
    }
}

package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductQnA;
import com.example.mansshop_boot.domain.entity.ProductQnAReply;

import java.util.ArrayList;
import java.util.List;

public class ProductQnAUnitFixture {

    public static List<ProductQnA> createProductQnAList(List<Member> memberList, List<Product> productList) {
        List<ProductQnA> productQnAList = new ArrayList<>();
        for(int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            Product product = productList.get(i);
            productQnAList.add(createProductQnA(i, member, product));
        }

        return productQnAList;
    }

    public static ProductQnA createProductQnA(int id, Member member, Product product) {
        return ProductQnA.builder()
                .id((long) id)
                .member(member)
                .product(product)
                .qnaContent(product.getProductName() + "'s qna content")
                .build();
    }

    public static ProductQnAReply createProductQnAReply(ProductQnA productQnA) {
        return ProductQnAReply.builder()
                .member(Member.builder().userId("admin").build())
                .productQnA(productQnA)
                .replyContent("admin's reply")
                .build();
    }
}

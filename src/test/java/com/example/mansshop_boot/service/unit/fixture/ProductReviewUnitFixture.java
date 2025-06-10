package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductReview;
import com.example.mansshop_boot.domain.entity.ProductReviewReply;
import com.example.mansshop_boot.service.unit.domain.ReviewAndReplyDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductReviewUnitFixture {

    public static List<ReviewAndReplyDTO> createProductReviewList(List<Member> members, Product product) {
        List<ReviewAndReplyDTO> productReviewList = new ArrayList<>();

        for(int i = 0; i < members.size(); i++) {
            ProductReview productReview = createProductReview(i, product, members.get(i));
            ProductReviewReply productReviewReply = createProductReviewReply(productReview);

            productReviewList.add(new ReviewAndReplyDTO(productReview, productReviewReply));
        }


        return productReviewList;
    }

    public static ProductReview createProductReview(int id, Product product, Member member) {
        return ProductReview.builder()
                            .id((long) id)
                            .member(member)
                            .product(product)
                            .reviewContent(member.getUserId() + "'s review")
                            .productOption(product.getProductOptions().get(0))
                            .createdAt(LocalDateTime.now())
                            .status(true)
                            .build();
    }

    public static ProductReviewReply createProductReviewReply(ProductReview productReview) {
        return ProductReviewReply.builder()
                                .member(Member.builder().userId("admin").build())
                                .productReview(productReview)
                                .replyContent(productReview.getReviewContent() + " replyContent")
                                .createdAt(LocalDateTime.now())
                                .build();
    }
}

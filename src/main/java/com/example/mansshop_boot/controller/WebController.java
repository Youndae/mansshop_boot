package com.example.mansshop_boot.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController implements ErrorController{

    /*@GetMapping({
            "/", "/{classification}"
            , "/login", "/join", "/search-id", "/search-password", "/oAuth"
            , "/product/{productId}"
            , "/cart", "/order", "/order/non-member"
            , "/my-page", "/my-page/like", "/my-page/product-qna", "/my-page/product-qna/{qnaId}"
            , "/my-page/qna", "/my-page/qna/{qnaId}", "/my-page/review", "/my-page/review/{reviewId}"
            , "/my-page/review/insert", "/my-page/profile", "/my-page/profile/info"
            , "/payment", "/payment/complete"
            , "/admin", "/admin/product", "/admin/product/{productId}", "/admin/product/stock"
            , "/admin/product/category", "/admin/order/new", "/admin/order"
            , "/admin/qna/product", "/admin/qna/product/{productQnAId}"
            , "/admin/qna/member", "/admin/qna/member/{memberQnAId}", "/admin/qna/category"
            , "/admin/member", "/admin/member/{userId}"
            , "/admin/sales/term", "/admin/sales/term/detail"
            , "/admin/sales/product", "/admin/sales/product/{productId}"
            , "/error"
    })
    public String index() {
        System.out.println("errorController index");
        return "forward:/index.html";
    }*/

    @GetMapping({
            "", "/", "/product/**"
            , "/login", "/join", "/search-id", "/search-password", "/oAuth", "/reset-pw"
            , "/new", "/search", "/category/**"
            , "/cart", "/productOrder", "/order/**"
            , "/my-page/**"
            , "/admin/**"
            , "/error"
    })
    public String index() {
        return "forward:index.html";
    }


}

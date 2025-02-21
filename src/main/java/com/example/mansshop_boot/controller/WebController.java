package com.example.mansshop_boot.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     *
     * 페이지 매핑
     */
    @Operation(hidden = true)
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

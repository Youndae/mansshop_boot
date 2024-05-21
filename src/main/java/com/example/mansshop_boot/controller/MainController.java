package com.example.mansshop_boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MainController {

    @GetMapping("/api/main")
    public String test() {
        System.out.println("main controller");

        return "Hello, Man's Shop!";
    }
}

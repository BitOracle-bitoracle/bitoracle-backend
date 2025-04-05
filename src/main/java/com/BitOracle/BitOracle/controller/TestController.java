package com.BitOracle.BitOracle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class TestController {

    @GetMapping("/auth/test")
    public String testLogin() {
        return "✅ 로그인된 사용자입니다.";
    }
}

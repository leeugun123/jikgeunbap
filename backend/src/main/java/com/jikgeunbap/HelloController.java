package com.jikgeunbap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "스프링 부트 프로젝트가 성공적으로 fewfwfwfe다!";
    }
}
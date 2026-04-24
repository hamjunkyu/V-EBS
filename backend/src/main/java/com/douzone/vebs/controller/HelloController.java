package com.douzone.vebs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "V-EBS Backend Running (Spring Boot 3.5 + Java 21)";
    }
}
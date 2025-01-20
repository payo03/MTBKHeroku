package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class InboundInterface {

    @GetMapping("/receive")
    public String receive() {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }

    // 0120 test junho
}

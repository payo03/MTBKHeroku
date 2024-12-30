package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class InboundInterface {

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }
    
}

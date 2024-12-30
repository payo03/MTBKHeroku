package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Healthcheck {

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }
    
}

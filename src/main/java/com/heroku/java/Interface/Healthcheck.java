package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class Healthcheck {

    @GetMapping("/healthcheck")
    public String getHealthcheck() {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }

    @PostMapping("/healthcheck")
    public String postHealthcheck(@RequestBody String body) {

        return "Hello from Spring Boot! / " + body;
    }
    
}

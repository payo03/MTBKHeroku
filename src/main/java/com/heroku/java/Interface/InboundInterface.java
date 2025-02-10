package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api")
public class InboundInterface {

    @GetMapping("/receive")
    public String receive(@RequestHeader(value="X-API-KEY", required = true) String apiKey) {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }
    
}

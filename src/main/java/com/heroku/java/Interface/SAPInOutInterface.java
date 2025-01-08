package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.heroku.java.Config.SFDCTokenManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/sap")
public class SAPInOutInterface {
    private static final Logger logger = LogManager.getLogger(SAPInOutInterface.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;
    
    @GetMapping("/healthcheck")
    public String callSAPHealthCheck() {
        String text = null;
        String SFDCHealthCheckURL = "http://3.36.162.185:80/MAN/GetTest";

        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                SFDCHealthCheckURL, 
                HttpMethod.GET, 
                requestEntity, 
                String.class
            );
            text = response.getBody();

            logger.info("#############################################");
            logger.info("SUCCESS. SAP HealthCheck, {}", text);
            logger.info("#############################################");
        } catch (Exception e) {
            logger.info("#############################################");
            logger.info("Fail. SAP HealthCheck, {}", e.getMessage());
            logger.info("#############################################");
        }

        return text;
    }
}

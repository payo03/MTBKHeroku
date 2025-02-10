package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.SFDCTokenManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class Healthcheck {
    private static final Logger logger = LogManager.getLogger(Healthcheck.class);
    private static final String APEX_REST = "apexrest";
    private static final String API = "api";

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    /*
    @Autowired
    @Qualifier("vpnRestClient")
    private RestClient restClient;
    */

    @Autowired
    private SFDCTokenManager tokenManager;

    @GetMapping("/healthcheck")
    public String getHealthcheck() {
        return "Hello from Spring Boot! / " + LocalDateTime.now();
    }

    @PostMapping("/healthcheck")
    public String postHealthcheck(@RequestBody String body) {

        return "Hello from Spring Boot! / " + body;
    }

    @GetMapping("/sfdc/healthcheck")
    public Map<String, String> callSFDCHealthCheck() {
        Map<String, String> responseMap = new HashMap<String, String>();
        
        // URL
        String SDFCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SDFCURL)
            .pathSegment(APEX_REST)
            .pathSegment(API)
            .pathSegment("check");

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenManager.getApiToken());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        System.out.println(URIBuilder.toUriString());
        try {
            ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<HashMap<String, String>>() {}
            );

            /*
            ResponseEntity<HashMap<String, String>> response = restClient.get()
                .uri(URIBuilder.toUriString())
                .headers(header -> {
                    header.addAll(headers);
                })
                .retrieve()
                .toEntity(new ParameterizedTypeReference<HashMap<String, String>>() {});
            */

            responseMap = response.getBody();

            logger.info("#############################################");
            logger.info("SUCCESS. SFDC HealthCheck, {}", response.getBody());
            logger.info("#############################################");
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            responseMap.put("result", "Health Check Fail");
            responseMap.put("responseDate", formattedDateTime);

            logger.error("#############################################");
            logger.error("Fail. SFDC HealthCheck, {}", e.getMessage());
            logger.error("#############################################");
        }

        return responseMap;
    }
    
}

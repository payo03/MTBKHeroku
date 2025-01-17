package com.heroku.java.Config;

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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SFDCTokenManager {
    private static final Logger logger = LogManager.getLogger(SFDCTokenManager.class);

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    private static final Long EXPIRED_PERIOD = 7140000L;    // 1시간 59분
    private static final String URL_OAUTH2 = "oauth2";
    private static final String URL_TOKEN = "token";

    private String apiToken;
    private Long expiredTime = System.currentTimeMillis() - 1;

    // 현재 토큰 반환
    public String getApiToken() {
        if (isTokenExpired()) {
            logger.info("#############################################");
            logger.info("currentTime : {}, expiredTime : {}", System.currentTimeMillis(), this.expiredTime);
            logger.info("#############################################");

            fetchNewToken(); 
        }
        return this.apiToken;
    }

    // 토큰갱신
    @SuppressWarnings("null")
    public void fetchNewToken() {
        // URL
        String SDFCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SDFCURL)
            .pathSegment(URL_OAUTH2)
            .pathSegment(URL_TOKEN);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HeaderTypeList.FORM_URLENCODE);

        // Body
        StringBuilder bodyBuilder = new StringBuilder();
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(
            "grant_type", Optional.ofNullable(System.getenv("SFDC_TOKEN_TYPE")).orElse("client_credentials")
        );
        requestBody.put(
            "client_id", Optional.ofNullable(System.getenv("SFDC_TOKEN_ID")).orElse("3MVG9UVgd21nzHD.r8gDYqZQv_LOpDI8YgKIFCmQ7fGN.pbl4U6jHKb1EHflhlcw_QB.7TwIrmvgLoVCg.Zjk")
        );
        requestBody.put(
            "client_secret", Optional.ofNullable(System.getenv("SFDC_TOKEN_SECRET")).orElse("EF3754D70177E00A1F9E3F3E397223BBECAB8DDC92082BDF10907D1890EA0E10")
        );

        requestBody.forEach((key, value) -> bodyBuilder.append(key).append("=").append(value).append("&"));
        String body = bodyBuilder.toString();
        body = body.substring(0, body.length() - 1);

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.POST, 
                requestEntity, 
                new ParameterizedTypeReference<HashMap<String, String>>() {}
            );

            Map<String, String> responseMap = response.getBody();
            this.apiToken = responseMap.get("access_token");
            this.expiredTime = Long.valueOf(responseMap.get("issued_at")) + EXPIRED_PERIOD;

            logger.info("#############################################");
            logger.info("SUCCESS. Token Fetch, {}", response.getBody());
            logger.info("#############################################");
        } catch (Exception e) {
            logger.error("#############################################");
            logger.error("Fail. Token Fetch, {}", e.getMessage());
            logger.error("#############################################");
        }
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired() {
        return System.currentTimeMillis() > this.expiredTime;
    }
}

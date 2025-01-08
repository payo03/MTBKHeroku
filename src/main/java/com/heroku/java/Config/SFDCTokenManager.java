package com.heroku.java.Config;

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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SFDCTokenManager {
    private static final Logger logger = LogManager.getLogger(SFDCTokenManager.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final Long EXPIRED_PERIOD = 7140000L;
    private String apiToken;
    private Long expiredTime = System.currentTimeMillis() - 1;

    // 현재 토큰 반환
    public String getApiToken() {
        if (isTokenExpired()) {
            fetchNewToken(); 
        }
        return this.apiToken;
    }

    // 토큰갱신
    @SuppressWarnings("null")
    private void fetchNewToken() {
        String SFDCTokenURL = System.getenv("SFDC_TOKEN_URL");
        // String SFDCTokenURL = "https://app-force-1035--partial.sandbox.my.salesforce.com/services/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HeaderTypeList.FORM_URLENCODE);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", System.getenv("SFDC_TOKEN_TYPE"));
        requestBody.put("client_id", System.getenv("SFDC_TOKEN_ID"));
        requestBody.put("client_secret", System.getenv("SFDC_TOKEN_SECRET"));
/*
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_id", "3MVG9UVgd21nzHD.r8gDYqZQv_LOpDI8YgKIFCmQ7fGN.pbl4U6jHKb1EHflhlcw_QB.7TwIrmvgLoVCg.Zjk");
        requestBody.put("client_secret", "EF3754D70177E00A1F9E3F3E397223BBECAB8DDC92082BDF10907D1890EA0E10");
*/

        StringBuilder bodyBuilder = new StringBuilder();

        requestBody.forEach((key, value) -> bodyBuilder.append(key).append("=").append(value).append("&"));
        String body = bodyBuilder.toString();
        body = body.substring(0, body.length() - 1);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
                SFDCTokenURL, 
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
            logger.info("#############################################");
            logger.info("Fail. Token Fetch, {}", e.getMessage());
            logger.info("#############################################");
        }
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired() {
        logger.info("#############################################");
        logger.info("currentTime : {}, expiredTime : {}", System.currentTimeMillis(), this.expiredTime);
        logger.info("#############################################");
        return System.currentTimeMillis() > this.expiredTime;
    }
}

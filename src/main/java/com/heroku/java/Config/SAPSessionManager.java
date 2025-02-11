package com.heroku.java.Config;

import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SAPSessionManager {
    private static final Logger logger = LogManager.getLogger(SAPSessionManager.class);

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    private static final Long EXPIRED_PERIOD = 1740000L;    // 29분
    private static final String URL_LOGIN = "Login";

    private Map<String, String> cookieMap;
    private Long expiredTime = System.currentTimeMillis() - 1;

    // 현재 세션반환
    public Map<String, String> getSessionMap() {
        if (isTokenExpired()) {
            logger.info("#############################################");
            logger.info("currentTime : {}, expiredTime : {}", System.currentTimeMillis(), this.expiredTime);
            logger.info("#############################################");

            fetchCookieMap(); 
        }
        return this.cookieMap;
    }

    // 세션갱신
    public void fetchCookieMap() {
        // URL
        String SAPWebURL = System.getenv("SAP_WEB_URL");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SAPWebURL)
            .pathSegment(URL_LOGIN);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HeaderTypeList.APPLICATION_JSON);

        // Body        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("CompanyDB", System.getenv("COMPANY_DB"));
        requestBody.put("UserName", System.getenv("USER_NAME"));
        requestBody.put("Password", System.getenv("PASSWORD"));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(body);

            ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.POST, 
                requestEntity, 
                new ParameterizedTypeReference<HashMap<String, String>>() {}
            );

            Map<String, String> responseMap = response.getBody();
            this.cookieMap = new HashMap<String, String>();

            this.cookieMap.put("B1SESSION", responseMap.get("SessionId"));
            this.cookieMap.put("ROUTEID", responseMap.get(".node4"));
            this.expiredTime = System.currentTimeMillis() + EXPIRED_PERIOD;

            logger.info("#############################################");
            logger.info("SUCCESS. Session Fetch, {}", response.getBody());
            logger.info("#############################################");
        } catch (Exception e) {
            logger.error("#############################################");
            logger.error("Fail. Session Fetch, {}", e.getMessage());
            logger.error("#############################################");
        }
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired() {
        return System.currentTimeMillis() > this.expiredTime;
    }
}

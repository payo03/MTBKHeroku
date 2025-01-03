package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.heroku.java.Config.HeaderTypeList;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class InOutInterface {
    private static final Logger logger = LogManager.getLogger(InOutInterface.class);

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/kakao/alim")
    public Map<String, Object> kakaoAlim(@RequestBody String jsonString) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");

        String infobipURL = System.getenv("INFOBIP_URL");
        String infobipAPIKey = System.getenv("INFOBIP_KEY");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", HeaderTypeList.APPLICATION_JSON);
        headers.set("Content-Type", HeaderTypeList.APPLICATION_JSON);
        headers.set("Authorization", infobipAPIKey);
        
        resultMap.put("requestBody", jsonString);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                infobipURL, 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            
            resultMap.put("Status Code", response.getStatusCode());
            resultMap.put("message", response.getBody());
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());
        }
        
        logger.info("#############################################");
        logger.info("SUCCESS. API Call");
        logger.info("#############################################");

        return resultMap;
    }
}

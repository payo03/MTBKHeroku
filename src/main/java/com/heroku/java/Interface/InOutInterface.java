package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
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

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", System.getenv("INFOBIP_KEY"));

        logger.info("#############################################");
        logger.info("### Raw Json : " + jsonString + " ###");
        logger.info("#############################################");

        String unescapedJson = StringEscapeUtils.unescapeJson(jsonString);
        logger.info("#############################################");
        logger.info("### Escaped Json : " + unescapedJson + " ###");
        logger.info("#############################################");

        resultMap.put("infoMap", unescapedJson);
        HttpEntity<String> requestEntity = new HttpEntity<>(unescapedJson, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                System.getenv("INFOBIP_URL"), 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            
            resultMap.put("Status Code", response.getStatusCode());
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());
        }
        headers.forEach((key, value) -> logger.info(key + ": " + value));
        logger.info(resultMap);

        return resultMap;
    }
}

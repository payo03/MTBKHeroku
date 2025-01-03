package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.HeaderTypeList;
import com.heroku.java.DTO.FetchTemplateRequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        logger.info("SUCCESS. KAKAO API Call");
        logger.info("#############################################");

        return resultMap;
    }

    
    @GetMapping("/wsmoka/template")
    public Map<String, Object> fetchTemplate(@ModelAttribute FetchTemplateRequest request) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");
        logger.info(request);

        String WSMokaURL = System.getenv("WS_MOKA_URL");
        // String WSMokaURL = "https://wt-api.carrym.com:8445/api/v1/mantruck/template/last_modified";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HeaderTypeList.FORM_URLENCODE);

        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(WSMokaURL)
            .queryParam("senderKey", request.getSenderKey())
            .queryParam("since", request.getSince());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        logger.info(URIBuilder.toUriString());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.GET, 
                requestEntity, 
                String.class
            );
            
            resultMap.put("Status Code", response.getStatusCode());
            resultMap.put("message", response.getBody());
        
            logger.info("#############################################");
            logger.info(response.getBody());
            logger.info("#############################################");
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());
        }
        
        logger.info("#############################################");
        logger.info("SUCCESS. TEMPLATE API Call");
        logger.info("#############################################");

        return resultMap;
    }
}

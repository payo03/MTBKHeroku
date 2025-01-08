package com.heroku.java.Interface;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.HeaderTypeList;
import com.heroku.java.Config.SFDCTokenManager;

@RestController
@RequestMapping("/api/sap")
public class SAPInOutInterface {
    private static final Logger logger = LogManager.getLogger(SAPInOutInterface.class);
    
    private static final String SAP_HEALTHCHECK = "GetTest";

    private static final String SFDC_PATH_ES001 = "apexrest/api/sap/sms001";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;
    
    @GetMapping("/healthcheck")
    public String healthCheck() {
        String text = null;
        String SAPURL = System.getenv("SAP_URL");
        // String SAPURL = "http://3.36.162.185:80/MAN";
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SAPURL)
            .pathSegment(SAP_HEALTHCHECK);

        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
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

    @PostMapping("/sms001")
    public Map<String, Object> sms001(@RequestBody String jsonString) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");
        logger.info(jsonString);

        // URL
        String SFDCURL = System.getenv("SFDC_URL");
        // String SFDCURL = "https://app-force-1035--partial.sandbox.my.salesforce.com/services";
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SFDCURL)
            .pathSegment(SFDC_PATH_ES001);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenManager.getApiToken());
        headers.set("Content-Type", HeaderTypeList.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            String responseBody = response.getBody();
            
            resultMap.put("Status Code", response.getStatusCode());
            resultMap.put("message", response.getBody());

            logger.info("#############################################");
            logger.info("SUCCESS. Request {}. SFDC: {}", jsonString, responseBody);
            logger.info("#############################################");
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());

            logger.info("#############################################");
            logger.info("Fail. Request {}. SFDC: {}", jsonString, e.getMessage());
            logger.info("#############################################");
        }

        return resultMap;
    }
}

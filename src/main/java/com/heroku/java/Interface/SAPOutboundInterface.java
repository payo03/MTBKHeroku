package com.heroku.java.Interface;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.HeaderTypeList;
import com.heroku.java.Config.SFDCTokenManager;

@RestController
@RequestMapping("/api/sap")
public class SAPOutboundInterface {
    private static final Logger logger = LogManager.getLogger(SAPOutboundInterface.class);

    private static final String SAP_HEALTHCHECK = "GetTest";
    private static final String PATH_ES004 = "SMS004";
    private static final String PATH_ES007 = "SMS007";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;
    
    @GetMapping("/healthcheck")
    public String healthCheck() {
        String text = null;
        String SAPURL = Optional.ofNullable(System.getenv("SAP_URL"))
            .orElse("http://3.36.162.185:80/MAN");
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
        } catch (HttpClientErrorException e) {        
            logger.error("#############################################");
            logger.error("Error. SAP HealthCheck, {}", e.getMessage());
            logger.error("#############################################");
        } catch (Exception e) {
            logger.error("#############################################");
            logger.error("Fail. SAP HealthCheck, {}", e.getMessage());
            logger.error("#############################################");
        }

        return text;
    }

    @PostMapping("/sms004")
    public Map<String, Object> sms004(@RequestBody String jsonString) {
        logger.info("\n{}", jsonString);

        // URL
        String SAP_URL = Optional.ofNullable(System.getenv("SAP_URL"))
            .orElse("http://3.36.162.185:80/MAN");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SAP_URL)
            .pathSegment(PATH_ES004);
            
        // Header
        HttpHeaders headers = makeHeadersSAP();
        // Request Info
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        return doCallOutSAP(String.class, URIBuilder, requestEntity);
    }

    @PostMapping("/sms007")
    public Map<String, Object> sms007(@RequestBody String jsonString) {
        logger.info("\n{}", jsonString);

        // URL
        String SAP_URL = Optional.ofNullable(System.getenv("SAP_URL"))
            .orElse("http://3.36.162.185:80/MAN");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SAP_URL)
            .pathSegment(PATH_ES007);
            
        // Header
        HttpHeaders headers = makeHeadersSAP();
        // Request Info
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        return doCallOutSAP(String.class, URIBuilder, requestEntity);
    }

    private <T> Map<String, Object> doCallOutSAP(Object responseType, UriComponentsBuilder URIBuilder, HttpEntity<String> requestEntity) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                URIBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                getResponseType(responseType)
            );

            resultMap.put("Status Code", response.getStatusCode().value());
            resultMap.put("message", response.getBody());

            logger.info("#############################################");
            logger.info("SUCCESS. Request {}. SAP: {}", requestEntity.getBody(), response.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException.Unauthorized e) {
            // SAP Token 갱신필요

        } catch (HttpClientErrorException e) {
            // HTTP 에러 처리
            resultMap.put("code", false);
            resultMap.put("Status Code", e.getStatusCode().value());
            resultMap.put("message", e.getResponseBodyAsString());
        
            logger.error("#############################################");
            logger.error("Error. Request {}. SAP: {}", requestEntity.getBody(), e.getResponseBodyAsString());
            logger.error("#############################################");
        } catch (Exception e) {
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());

            logger.error("#############################################");
            logger.error("Fail. Request {}. Heroku: {}", requestEntity.getBody(), e.getMessage());
            logger.error("#############################################");
        }

        return resultMap;
    }

    // Header Setting
    private HttpHeaders makeHeadersSAP() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + tokenManager.getApiToken());
        header.set("Content-Type", HeaderTypeList.APPLICATION_JSON);

        return header;
    }

    // Generic Type을통한 Response Type 동적설정
    @SuppressWarnings("unchecked")
    private <T> ParameterizedTypeReference<T> getResponseType(Object responseType) {
        if (responseType instanceof Class) {
            return ParameterizedTypeReference.forType((Class<T>) responseType);
        } else if (responseType instanceof ParameterizedTypeReference) {
            return (ParameterizedTypeReference<T>) responseType;
        } else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType);
        }
    }
}

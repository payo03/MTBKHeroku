package com.heroku.java.Interface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.SFDCTokenManager;

@Service
public class AsyncService {
    private static final Logger logger = LogManager.getLogger(AsyncService.class);

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;

    @Async
    public <T> void AsyncDoCallOutSAP(Object responseType, UriComponentsBuilder URIBuilderSAP, UriComponentsBuilder URIBuilderSFDC, HttpEntity<T> requestEntity, Object returnObj) {
        logger.info("#############################################");
        logger.info("Endpoint URL. {}", URIBuilderSAP.toUriString());
        logger.info("#############################################");

        ResponseEntity<T> responseEntity = null;
        T responseBody = null;
        try {
            // 1. SAP API CALL
            responseEntity = restTemplate.exchange(
                URIBuilderSAP.toUriString(),
                HttpMethod.POST,
                requestEntity,
                InterfaceCommon.getResponseType(responseType)
            );
            responseBody = responseEntity.getBody();
            if(returnObj != null) responseBody = InterfaceCommon.parseJSONNode(responseBody, returnObj);

            logger.info("#############################################");
            logger.info("SUCCESS. Request {}. SAP: {}", requestEntity.getBody(), responseBody);
            logger.info("#############################################");
            
            // 2. SFDC API CALL
            // Header
            String token = tokenManager.getApiToken();
            HttpHeaders headers = InterfaceCommon.makeHeadersSFDC(token);
            // Request Info
            HttpEntity<T> requestEntitySFDC = new HttpEntity<>(responseBody, headers);
            ResponseEntity<T> responseEntitySFDC = restTemplate.exchange(
                URIBuilderSFDC.toUriString(),
                HttpMethod.POST,
                requestEntitySFDC,
                InterfaceCommon.getResponseType(responseType)
            );

            logger.info("#############################################");
            logger.info("SUCCESS. Request {}. SFDC: {}", requestEntitySFDC.getBody(), responseEntitySFDC.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException.Unauthorized e) {
            // SAP는 성공. SFDC가 에러날 경우
            // Unauthorized 예외 처리: 토큰 갱신 후 재시도
            tokenManager.fetchNewToken();

            String token = tokenManager.getApiToken();
            HttpHeaders newHeaders = InterfaceCommon.makeHeadersSFDC(token);
            HttpEntity<T> newRequestEntitySFDC = new HttpEntity<>(responseBody, newHeaders);
    
            ResponseEntity<T> newResponseEntitySFDC = restTemplate.exchange(
                URIBuilderSFDC.toUriString(),
                HttpMethod.POST,
                newRequestEntitySFDC,
                InterfaceCommon.getResponseType(responseType)
            );

            logger.info("#############################################");
            logger.info("SUCCESS(Refresh Token). Request {}. SFDC: {}", newRequestEntitySFDC.getBody(), newResponseEntitySFDC.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException e) {
            // HTTP 에러 처리
        
            logger.error("#############################################");
            logger.error("Error. Request {}. SAP: {}", requestEntity.getBody(), e.getResponseBodyAsString());
            logger.error("#############################################");
        } catch (Exception e) {

            logger.error("#############################################");
            logger.error("Fail. Request {}. Heroku: {}", requestEntity.getBody(), e.getMessage());
            logger.error("#############################################");
        }
    }
}

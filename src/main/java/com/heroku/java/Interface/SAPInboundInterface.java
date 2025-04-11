package com.heroku.java.Interface;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.SFDCTokenManager;
import com.heroku.java.DTO.OffsetProcess;
import com.heroku.java.DTO.PaymentInfo;
// import com.heroku.java.DTO.Stock;

@RestController
@RequestMapping("/api/sap")
public class SAPInboundInterface {
    private static final Logger logger = LogManager.getLogger(SAPInboundInterface.class);
    
    private static final String URL_APEXREST = "apexrest";
    private static final String URL_API = "api";
    private static final String URL_SAP = "sap";

    // Test
    // private static final String PATH_ES005 = "sms005";
    // private static final String PATH_ES014 = "sms014";
    private static final String PATH_ES019 = "sms019";
    private static final String PATH_ES021 = "sms021";

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;

    @PostMapping("/sms019")
    public Map<String, Object> sms019(@RequestHeader(value="X-API-KEY", required = true) String apiKey, @RequestBody PaymentInfo request) {
        logger.info("#############################################");
        logger.info("SUCCESS. Request {}", request);
        logger.info("#############################################");

        // URL
        String SFDCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SFDCURL)
            .pathSegment(URL_APEXREST)
            .pathSegment(URL_API)
            .pathSegment(URL_SAP)
            .pathSegment(PATH_ES019);

        // Header
        String token = tokenManager.getApiToken();
        HttpHeaders headers = InterfaceCommon.makeHeadersSFDC(token);
        // Request Info
        HttpEntity<PaymentInfo> requestEntity = new HttpEntity<>(request, headers);

        return doCallOutSFDC(new ParameterizedTypeReference<Map<String, Object>>() {}, URIBuilder, requestEntity);
    }

    @PostMapping("/sms021")
    public Map<String, Object> sms021(@RequestHeader(value="X-API-KEY", required = true) String apiKey, @RequestBody OffsetProcess request) {
        logger.info("#############################################");
        logger.info("SUCCESS. Request {}", request);
        logger.info("#############################################");

        // URL
        String SFDCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SFDCURL)
            .pathSegment(URL_APEXREST)
            .pathSegment(URL_API)
            .pathSegment(URL_SAP)
            .pathSegment(PATH_ES021);

        // Header
        String token = tokenManager.getApiToken();
        HttpHeaders headers = InterfaceCommon.makeHeadersSFDC(token);
        // Request Info
        HttpEntity<OffsetProcess> requestEntity = new HttpEntity<>(request, headers);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");
        resultMap.put("status_code", 200);
        resultMap.put("request_body", request);

        return resultMap;
        // return doCallOutSFDC(new ParameterizedTypeReference<Map<String, Object>>() {}, URIBuilder, requestEntity);
    }

    /*
    @PostMapping("/sms005")
    public Map<String, Object> sms004(@RequestHeader(value="X-API-KEY", required = true) String apiKey, @RequestBody Stock request) {
        logger.info("#############################################");
        logger.info("SUCCESS. Request {}", request);
        logger.info("#############################################");

        // URL
        String SFDCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SFDCURL)
            .pathSegment(URL_APEXREST)
            .pathSegment(URL_API)
            .pathSegment(URL_SAP)
            .pathSegment(PATH_ES005);

        // Header
        String token = tokenManager.getApiToken();
        HttpHeaders headers = InterfaceCommon.makeHeadersSFDC(token);
        // Request Info
        HttpEntity<Stock> requestEntity = new HttpEntity<>(request, headers);

        return doCallOutSFDC(new ParameterizedTypeReference<Map<String, Object>>() {}, URIBuilder, requestEntity);
    }

    @PostMapping("/sms014")
    public Map<String, Object> sms014(@RequestHeader(value="X-API-KEY", required = true) String apiKey, @RequestBody String jsonString) {
        logger.info("\n{}", jsonString);

        // URL
        String SFDCURL = Optional.ofNullable(System.getenv("SFDC_URL"))
            .orElse("https://app-force-1035--partial.sandbox.my.salesforce.com/services");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SFDCURL)
            .pathSegment(URL_APEXREST)
            .pathSegment(URL_API)
            .pathSegment(URL_SAP)
            .pathSegment(PATH_ES014);

        // Header
        String token = tokenManager.getApiToken();
        HttpHeaders headers = InterfaceCommon.makeHeadersSFDC(token);
        // Request Info
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        return doCallOutSFDC(String.class, URIBuilder, requestEntity);
    }
    */

    /*
    ============================================================================================================ 
    ============================================================================================================
    ============================================================================================================
    */

    /*
        doCallOutSFDC(new ParameterizedTypeReference<Map<String, Object>>() {}, URIBuilder, requestEntity);
        doCallOutSFDC(String.class, URIBuilder, requestEntity);
        doCallOutSFDC(DTO.class, URIBuilder, requestEntity);
    */
    // SFDC 토큰 만료 에러시 재호출 필요. 공통메소드 생성
    private <T> Map<String, Object> doCallOutSFDC(Object responseType, UriComponentsBuilder URIBuilder, HttpEntity<T> requestEntity) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                URIBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                InterfaceCommon.getResponseType(responseType)
            );
            
            resultMap.put("status_code", response.getStatusCode().value());
            resultMap.put("request_body", response.getBody());

            logger.info("#############################################");
            logger.info("SUCCESS. Request {}. SFDC: {}", requestEntity.getBody(), response.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException.Unauthorized e) {
            // Unauthorized 예외 처리: 토큰 갱신 후 재시도
            tokenManager.fetchNewToken();

            String token = tokenManager.getApiToken();
            HttpHeaders newHeaders = InterfaceCommon.makeHeadersSFDC(token);
            HttpEntity<T> newRequestEntity = new HttpEntity<>(requestEntity.getBody(), newHeaders);
    
            response = restTemplate.exchange(
                URIBuilder.toUriString(),
                HttpMethod.POST,
                newRequestEntity,
                InterfaceCommon.getResponseType(responseType)
            );
            
            resultMap.put("status_code", response.getStatusCode().value());
            resultMap.put("request_body", response.getBody());

            logger.info("#############################################");
            logger.info("SUCCESS(Refresh Token). Request {}. SFDC: {}", requestEntity.getBody(), response.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException e) {
            // HTTP 에러 처리
            resultMap.put("code", false);
            resultMap.put("status_code", e.getStatusCode().value());
            resultMap.put("message", e.getResponseBodyAsString());
        
            logger.error("#############################################");
            logger.error("Error. Request {}. SFDC: {}", requestEntity.getBody(), e.getResponseBodyAsString());
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
}

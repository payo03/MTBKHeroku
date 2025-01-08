package com.heroku.java.Interface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.heroku.java.Config.SFDCTokenManager;


@RestController
@RequestMapping("/api/sap")
public class SAPInOutInterface {
    private static final Logger logger = LogManager.getLogger(SAPInOutInterface.class);
    
    private static final String IF_HEALTHCHECK = "GetTest";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SFDCTokenManager tokenManager;
    
    @GetMapping("/healthcheck")
    public String callSAPHealthCheck() {
        String text = null;
        String SAPURL = System.getenv("SAP_URL");
        // String SAPURL = "http://3.36.162.185:80/MAN";

        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(SAPURL)
            .pathSegment(IF_HEALTHCHECK);
            
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
}

package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class InOutInterface {
    private static final Logger logger = LogManager.getLogger(InOutInterface.class);

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/kakao/alim")
    public Map<String, Object> kakaoAlim(@RequestParam Map<String, Object> infoMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");
        resultMap.put("infoMap : ", infoMap);

        /*
            Kakaotalk API 호출
        */

        String url = "https://pe86m3.api-id.infobip.com/kakao-alim/1/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "App ca9697740134af524df3d4a4cf702337-e938db19-144d-4a33-90d0-fb349dae8c2d");

        Map<String, Object> body = new HashMap<>();
        body.put("messages", Arrays.asList(
            Map.of(
                "sender", "85d506c54e71a52daa4598efb194ac5ed21732d8",
                "destinations", Arrays.asList(
                    Map.of("to", "821012345666")
                ),
                "options", Map.of(
                    "validityPeriod", Map.of("amount", "15"),
                    "smsFailover", Map.of(
                        "sender", "031-8014-5700",
                        "text", "Sample Template Test\nUserName : payo03@man.eu.trial.partial"
                    )
                ),
                "webhooks", Map.of(
                    "contentType", "application/json",
                    "delivery", Map.of(
                        "url", "https://app-force-1035--partial.sandbox.my.salesforce-sites.com/guest/services/apexrest/api/webhook/kakao",
                        "intermediateReport", "true"
                    )
                ),
                "content", Map.of(
                    "text", "Sample Template Test\nUserName : payo03@man.eu.trial.partial",
                    "templateCode", "KAKAO000",
                    "type", "TEMPLATE"
                )
            )
        ));
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
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

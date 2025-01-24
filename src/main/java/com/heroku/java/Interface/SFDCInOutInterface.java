package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.java.Config.HeaderTypeList;
import com.heroku.java.DTO.FetchTemplateRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SFDCInOutInterface {
    private static final Logger logger = LogManager.getLogger(SFDCInOutInterface.class);

    private static final List<String> KAKAO_WHITE_LIST = Arrays.asList(
        Optional.ofNullable(System.getenv("KAKAO_WHITE_LIST")).orElse("0").split(",")
    );

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    @PostMapping("/kakao/alim")
    public Map<String, Object> kakaoAlim(@RequestBody String jsonString) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode destinations = jsonNode.path("messages").get(0).path("destinations");
        /* 
        // WHITE LIST ON_OFF
        for (JsonNode destinationNode : destinations) {
            String destination = destinationNode.path("to").asText();
            if (!KAKAO_WHITE_LIST.contains(destination)) {
                resultMap.put("code", false);
                resultMap.put("message", "The destination number " + destination + " is not in the white list.");
                return resultMap;
            }
        }
        */

        String infobipURL = System.getenv("INFOBIP_URL");
        String infobipAPIKey = System.getenv("INFOBIP_KEY");

        // Header
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
            
            resultMap.put("status_code", response.getStatusCode().value());
            resultMap.put("message", response.getBody());
        
            logger.info("#############################################");
            logger.info("SUCCESS. KAKAO API Call, {}", response.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException e) {
            // 일반적인 HTTP 에러 처리
            resultMap.put("code", false);
            resultMap.put("status_code", e.getStatusCode().value());
            resultMap.put("message", e.getResponseBodyAsString());
        
            logger.error("#############################################");
            logger.error("Error. Request {}. SFDC: {}", jsonString, e.getResponseBodyAsString());
            logger.error("#############################################");
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());
        
            logger.error("#############################################");
            logger.error("Fail. KAKAO API Call, {}", e.getMessage());
            logger.error("#############################################");
        }

        return resultMap;
    }

    @GetMapping("/wsmoka/template")
    public Map<String, Object> fetchTemplate(@ModelAttribute FetchTemplateRequest request) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + ((int) (Math.random() * 100)) + " points");
        logger.info(request);

        // URL
        String WSMokaURL = System.getenv("WS_MOKA_URL");
        UriComponentsBuilder URIBuilder = UriComponentsBuilder.fromHttpUrl(WSMokaURL)
            .queryParam("senderKey", request.getSenderKey())
            .queryParam("since", request.getSince());

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HeaderTypeList.FORM_URLENCODE);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        logger.info(URIBuilder.toUriString());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                URIBuilder.toUriString(), 
                HttpMethod.GET, 
                requestEntity, 
                String.class
            );
            
            resultMap.put("status_code", response.getStatusCode().value());
            resultMap.put("message", response.getBody());
        
            logger.info("#############################################");
            logger.info("SUCCESS. WSMOKA API Call, {}", response.getBody());
            logger.info("#############################################");
        } catch (HttpClientErrorException e) {
            // 일반적인 HTTP 에러 처리
            resultMap.put("code", false);
            resultMap.put("status_code", e.getStatusCode().value());
            resultMap.put("message", e.getResponseBodyAsString());
        
            logger.error("#############################################");
            logger.error("Error. Request {}. SFDC: {}", request, e.getResponseBodyAsString());
            logger.error("#############################################");
        } catch (Exception e) {
            // 예외 처리
            resultMap.put("code", false);
            resultMap.put("message", e.getMessage());
        
            logger.error("#############################################");
            logger.error("Fail. WSMOKA API Call, {}", e.getMessage());
            logger.error("#############################################");
        }

        return resultMap;
    }

    @PostMapping("/getpagenumber")
    public Integer getPageNumber(@RequestBody String file) throws UnsupportedEncodingException {
        file = file.replaceFirst("file=", "");
        byte[] pdfData = Base64.getDecoder().decode(file);
        
        // PDF 데이터를 메모리로 읽기
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfData))) {

            // 페이지 수 계산
            return document.getNumberOfPages();
        } catch (IOException e) {
            e.printStackTrace();

            return 0;
        }
    }

    @PostMapping("/png")
    public ResponseEntity<String> convertURLToPNG(@RequestBody String url) {
        // 헤드리스 크롬 설정
        ChromeOptions options = new ChromeOptions();
        String chromePath = findChromePath();
        options.setBinary(chromePath); // Google Chrome 실행 경로
        options.addArguments("--headless"); // 헤드리스 모드
        options.addArguments("--no-sandbox"); // Sandbox 비활성화
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 메모리 제한 해제
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1280,1024");

        WebDriver driver = new ChromeDriver(options);

        logger.info("#############################################");
        logger.info("Before Try, {}", driver);
        logger.info("#############################################");
        try {
            // URL 열기
            driver.get(url);

            logger.info("#############################################");
            logger.info("After Try, {}", driver);
            logger.info("#############################################");

            // 스크린샷 찍기
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            logger.info("#############################################");
            logger.info("Screenshot, {}", screenshot);
            logger.info("#############################################");

            // PNG 데이터를 Base64로 인코딩
            String base64Image = Base64.getEncoder().encodeToString(screenshot);

            logger.info("#############################################");
            logger.info("Image, {}", base64Image);
            logger.info("#############################################");

            // Base64 데이터 반환
            return ResponseEntity.ok(base64Image);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        } finally {
            // WebDriver 종료
            driver.quit();
        }
    }

    private String findChromePath() {
        try {

            Process process = Runtime.getRuntime().exec("find /tmp -name chrome");

            logger.info("#############################################");
            logger.info("Process, {}", process);
            logger.info("#############################################");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            logger.info("#############################################");
            logger.info("Reader, {}", reader);
            logger.info("#############################################");

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("chrome")) {
                    return line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

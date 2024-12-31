package com.heroku.java.Config;

import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final Logger logger = LogManager.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        try {

            // 1. Heroku Config. Quotaguard 프록시 URL 가져오기
            String staticURL = System.getenv("QUOTAGUARDSTATIC_URL");
            if(staticURL != null) {
                URL proxyUrl = new URL(System.getenv("QUOTAGUARDSTATIC_URL"));
                logger.info("#############################################");
                logger.info("### Proxy URL: " + proxyUrl + " ###");
                logger.info("#############################################");

                // 2. 프록시 사용자 정보 추출
                String userInfo = proxyUrl.getUserInfo();
                String user = userInfo.substring(0, userInfo.indexOf(':'));
                String password = userInfo.substring(userInfo.indexOf(':') + 1);

                // 3. 인증 설정
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                });

                // 4. 프록시 설정
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setProxy(proxy);

                // 5. RestTemplate. Froxy 설정
                RestTemplate template = new RestTemplate(factory);

                // 6. Static IP Setting(getForObject)
                logger.info("#############################################");
                logger.info(template.getForObject("http://ip.quotaguard.com", String.class));
                logger.info("#############################################");
                
                return template;
            } else {
                return new RestTemplate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure RestTemplate with Quotaguard proxy.", e);
        }
    }
}
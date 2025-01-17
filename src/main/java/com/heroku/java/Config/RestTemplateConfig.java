package com.heroku.java.Config;

import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final Logger logger = LogManager.getLogger(RestTemplateConfig.class);

    @Bean("defaultRestTemplate")
    RestTemplate defaultRestTemplate() {
        // RestClient가 있다고함... 써본적이 없어서 RestTemplate으로 사용했음

        try {
            // 1. Heroku Config. Quotaguard 프록시 URL 가져오기
            String staticURL = System.getenv("QUOTAGUARDSTATIC_URL");
            if(staticURL != null) {
                URL proxyURL = new URL(staticURL);
                logger.info("#############################################");
                logger.info("### Proxy URL: " + proxyURL + " ###");
                logger.info("#############################################");

                // 2. 프록시 사용자 정보 추출
                String userInfo = proxyURL.getUserInfo();
                String user = userInfo.substring(0, userInfo.indexOf(':'));
                String password = userInfo.substring(userInfo.indexOf(':') + 1);

                // 3. 인증 설정
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                });

                // 4. 프록시 설정
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyURL.getHost(), proxyURL.getPort()));
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

    @Bean("vpnRestClient")
    RestClient vpnRestClient(@Qualifier("defaultRestTemplate") RestTemplate defaultRestTemplate) {
        
        try {
            // 1. Heroku Config. VPN URL 가져오기
            String vpnURL = System.getenv("VPN_URL");
            if(vpnURL != null) {
                URL proxyURL = new URL(vpnURL);
                logger.info("#############################################");
                logger.info("### VPN URL: " + proxyURL + " ###");
                logger.info("#############################################");

                // 2. 프록시 사용자 정보 추출
                String userInfo = proxyURL.getUserInfo();
                String user = userInfo.substring(0, userInfo.indexOf(':'));
                String password = userInfo.substring(userInfo.indexOf(':') + 1);

                // 4. 프록시 설정
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyURL.getHost(), proxyURL.getPort()));
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setProxy(proxy);

                // 5. RestTemplate. Froxy 설정
                defaultRestTemplate.setRequestFactory(factory);

                defaultRestTemplate.getInterceptors().add((request, body, execution) -> {
                    String auth = user + ":" + password;
                    String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                    request.getHeaders().add("Proxy-Authorization", "Basic " + encodedAuth);
                    
                    logger.info("#############################################");
                    logger.info("#############################################");
                    logger.info("#############################################");
                    
                    return execution.execute(request, body);
                });
                RestClient client = RestClient.create(defaultRestTemplate);

                // 6. VPN Proxy Setting
                logger.info("#############################################");
                logger.info(client.get().uri("http://ip.quotaguard.com").retrieve().body(String.class));
                logger.info("#############################################");

                return client;
            } else {
                return RestClient.create(defaultRestTemplate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure RestTemplate with VPN proxy.", e);
        }
    }
}
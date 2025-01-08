package com.heroku.java.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HandlerConfig implements WebMvcConfigurer {

    @Autowired
    private WhiteListInterceptor whiteListInterceptor;

    @Autowired
    private APIRequestInterceptor apiRequestInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    @SuppressWarnings("null")
    public void addInterceptors(InterceptorRegistry registry) {

        // API 호출경로에 대해 적용
        registry.addInterceptor(whiteListInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(apiRequestInterceptor)
                .addPathPatterns("/api/**");

        // API요청은 1초에 2번(해제)
        registry.addInterceptor(rateLimitInterceptor);
    }
}

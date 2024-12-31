package com.heroku.java.Config;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class APIRequestInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(APIRequestInterceptor.class);

    private static final String API_PREFIX = "Bearer ";
    private static final String API_TOKEN = Optional.ofNullable(System.getenv("API_TOKEN"))
                                                    .orElse("test");

    @Override
    @SuppressWarnings("null")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String paramKey = request.getHeader("X-API-KEY");
        String apiKey = API_PREFIX + API_TOKEN;

        // API Key 검증
        if (paramKey == null || !apiKey.equals(paramKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid API Key");
            logger.warn("Invalid API Key: {}", paramKey);

            return false;
        }

        return true; // 요청 허용
    }
}

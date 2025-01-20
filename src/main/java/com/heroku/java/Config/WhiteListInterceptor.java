package com.heroku.java.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class WhiteListInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(WhiteListInterceptor.class);

    // 허용된 도메인 리스트
    private static final List<String> DOMAIN_WHITE_LIST = Arrays.asList(
        Optional.ofNullable(System.getenv("DOMAIN_WHITE_LIST")).orElse("localhost").split(",")
    );

    @Override
    @SuppressWarnings("null")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestDomain = request.getServerName();

        // 도메인으로 WhiteList Setting
        if (!DOMAIN_WHITE_LIST.contains(requestDomain)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Invalid domain");
            logger.info(requestDomain);
            logger.debug(requestDomain);

            return false;
        }

        return true; // 허용된 요청만 처리
    }
}

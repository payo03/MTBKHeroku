package com.heroku.java.Config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DomainWhiteListInterceptor implements HandlerInterceptor {

    // 허용된 도메인 리스트
    private static final List<String> DOMAIN_WHITE_LIST = List.of(
            "localhost",
            "https://app-force-1035--partial.sandbox.my.salesforce.com"
    );

    @Override
    @SuppressWarnings("null")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestDomain = request.getServerName();

        // 도메인으로 WhiteList Setting
        if (!DOMAIN_WHITE_LIST.contains(requestDomain)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Invalid domain");
            System.out.println(requestDomain);

            return false;
        }

        return true; // 허용된 요청만 처리
    }
}

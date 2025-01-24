package com.heroku.java.Config;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private static final long THRESHOLD_TIME = 500; // 0.5초
    private static final String[] STATIC_PATHS = {"/stylesheets/", "/images/", "/favicon.ico"};

    private ConcurrentHashMap<String, Long> requestMap = new ConcurrentHashMap<>();
    private Long lastRequestTime = null;

    public boolean isRequestAllowed(HttpServletRequest request) {
        String serverName = request.getServerName();
        long currentTime = System.currentTimeMillis();
        lastRequestTime = requestMap.get(serverName);

        // IF. 호출한적 없음 or 호출시간 > 기준시간
        if (lastRequestTime == null || currentTime - lastRequestTime > THRESHOLD_TIME) {
            requestMap.put(serverName, currentTime);

            return true; // 요청 허용
        }
        
        String requestURI = request.getRequestURI();
        for (String path : STATIC_PATHS) {
            if (requestURI.startsWith(path)) return true;
        }

        return false; // 요청 차단
    }
}

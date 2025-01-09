package com.heroku.java.Config;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private static final long THRESHOLD_TIME = 500; // 0.5초

    private ConcurrentHashMap<String, Long> requestMap = new ConcurrentHashMap<>();
    private Long lastRequestTime = null;

    public boolean isRequestAllowed(HttpServletRequest request) {
        String serverName = request.getServerName();
        long currentTime = System.currentTimeMillis();
        lastRequestTime = requestMap.get(serverName);
        if (
            lastRequestTime == null || 
            currentTime - lastRequestTime > THRESHOLD_TIME
        ) {
            requestMap.put(serverName, currentTime);

            return true; // 요청 허용
        }

        return false; // 요청 차단
    }
}

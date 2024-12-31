package com.heroku.java.Config;

import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private final ConcurrentHashMap<String, Long> requestMap = new ConcurrentHashMap<>();
    private static final long THRESHOLD_TIME = 500; // 0.5초

    public boolean isRequestAllowed(HttpServletRequest request) {
        String serverName = request.getServerName();
        long currentTime = System.currentTimeMillis();

        Long lastRequestTime = requestMap.get(serverName);
        if (lastRequestTime == null || (currentTime - lastRequestTime > THRESHOLD_TIME)) {
            requestMap.put(serverName, currentTime);
            return true; // 요청 허용
        }
        return false; // 요청 차단
    }
}

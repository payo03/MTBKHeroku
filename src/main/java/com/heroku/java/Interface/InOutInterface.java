package com.heroku.java.Interface;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class InOutInterface {
    private static final Logger logger = LogManager.getLogger(InOutInterface.class);

    @PostMapping("/kakao/alim")
    public Map<String, Object> kakaoAlim(@RequestParam Map<String, Object> infoMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", true);
        resultMap.put("message", "Great. you\'ve got " + Integer.valueOf(String.valueOf(Math.random()*10)) + " points");
        resultMap.put("infoMap : ", infoMap);

        /*
            Kakaotalk API 호출
        */

        logger.info(infoMap);

        return resultMap;
    }
}

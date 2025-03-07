package com.heroku.java.Interface;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heroku.java.Config.HeaderTypeList;

public class InterfaceCommon {

    // Generic Type을통한 Response Type 동적설정
    @SuppressWarnings("unchecked")
    public static <T> ParameterizedTypeReference<T> getResponseType(Object responseType) {
        if (responseType instanceof Class) {
            return ParameterizedTypeReference.forType((Class<T>) responseType);
        } else if (responseType instanceof ParameterizedTypeReference) {
            return (ParameterizedTypeReference<T>) responseType;
        } else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType);
        }
    }

    // Header Setting
    public static HttpHeaders makeHeadersSAP() {
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", HeaderTypeList.APPLICATION_JSON);

        return header;
    }

    // SFDC Call을 위한 Header는 고정
    public static HttpHeaders makeHeadersSFDC(String token) {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + token);
        header.set("Content-Type", HeaderTypeList.APPLICATION_JSON);

        return header;
    }

    public static Map<String, Object> extractJSON(String paramString) {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(paramString);
            JsonNode logIdNode = jsonNode.get("LOG_ID");

            // LOG_ID가 String 타입인 경우
            String logId = logIdNode != null && logIdNode.isTextual() ? logIdNode.asText() : "";

            if (jsonNode.isObject()) {
                ((ObjectNode) jsonNode).remove("LOG_ID");
            }
            String parseString = mapper.writeValueAsString(jsonNode);

            returnMap.put("logId", logId);
            returnMap.put("parseString", parseString);
        } catch (Exception e) {
            returnMap.put("logId", "");
            returnMap.put("parseString", paramString);
        }

        return returnMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseJSONNode(T data, Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;

        // data가 String이면 JSON 문자열을 파싱, 아니면 객체를 JsonNode로 변환
        if (data instanceof String) {
            rootNode = mapper.readTree((String) data);
        } else {
            rootNode = mapper.valueToTree(data);
        }

        String logId = (String) obj;

        // logId가 빈 문자열이 아닌 경우에만 LOG_ID 필드를 추가
        if (logId != null && !logId.isEmpty()) {
            if (rootNode.isObject()) {
                ((ObjectNode) rootNode).put("LOG_ID", logId);
            } else {
                ObjectNode newNode = mapper.createObjectNode();

                newNode.set("data", rootNode);
                newNode.put("LOG_ID", logId);
                rootNode = newNode;
            }
        }

        // 다시 T 타입으로 변환: 원본이 String이면 JSON 문자열로, 아니면 객체로 변환
        if (data instanceof String) {
            return (T) mapper.writeValueAsString(rootNode);
        } else {
            return mapper.convertValue(rootNode, (Class<T>) data.getClass());
        }
    }
}

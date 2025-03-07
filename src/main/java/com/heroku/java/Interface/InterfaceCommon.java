package com.heroku.java.Interface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(paramString);
            JsonNode logIdNode = jsonNode.get("LOG_ID");
            String logId = logIdNode != null ? logIdNode.asText() : null;

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

        // ID_LIST에 들어갈 ArrayNode 생성
        ArrayNode logIdNode = mapper.createArrayNode();
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                if (item instanceof Map) {
                    JsonNode mapNode = mapper.valueToTree(item);
                    logIdNode.add(mapNode);
                } else if (item instanceof Boolean) {
                    logIdNode.add((Boolean) item);
                } else if (item instanceof String) {
                    logIdNode.add((String) item);
                } else {
                    logIdNode.add(item != null ? item.toString() : "null");
                }
            }
        } else if (obj instanceof Map) {
            JsonNode mapNode = mapper.valueToTree(obj);
            logIdNode.add(mapNode);
        } else if (obj instanceof Boolean) {
            logIdNode.add((Boolean) obj);
        } else if (obj instanceof String) {
            logIdNode.add((String) obj);
        } else {
            logIdNode.add(obj != null ? obj.toString() : "null");
        }

        // rootNode가 ObjectNode이면 직접 필드를 추가, 그렇지 않으면 새 ObjectNode에 감싸서 추가
        if (rootNode.isObject()) {
            ((ObjectNode) rootNode).set("LOG_ID", logIdNode);
        } else {
            ObjectNode newNode = mapper.createObjectNode();
            
            newNode.set("data", rootNode);
            newNode.set("LOG_ID", logIdNode);
            rootNode = newNode;
        }

        // 다시 T 타입으로 변환: 원본이 String이면 JSON 문자열로, 아니면 객체로 변환
        if (data instanceof String) {
            return (T) mapper.writeValueAsString(rootNode);
        } else {
            return mapper.convertValue(rootNode, (Class<T>) data.getClass());
        }
    }
}

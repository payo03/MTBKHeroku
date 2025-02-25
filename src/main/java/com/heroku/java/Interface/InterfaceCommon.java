package com.heroku.java.Interface;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

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
}

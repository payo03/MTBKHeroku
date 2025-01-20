package com.heroku.java.DTO;

import lombok.Data;

@Data
public class FetchTemplateRequest {
    private String senderKey;
    private String since;
    
}
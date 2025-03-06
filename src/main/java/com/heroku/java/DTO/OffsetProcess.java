package com.heroku.java.DTO;

import java.util.List;

import lombok.Data;

@Data
public class OffsetProcess {
    private ResultDTO success_result;
    private ResultDTO fail_result;
}

@Data
class ResultDTO {
    private String message;
    private String oinv_doc_no;
    private List<String> orct_doc_no;
}
package com.heroku.java.Config;

import org.springframework.http.MediaType;

public class HeaderTypeList {

    public static final String TEXT_PLAIN = MediaType.APPLICATION_JSON_VALUE;
    public static final String TEXT_HTML = MediaType.TEXT_XML_VALUE;
    public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    public static final String APPLICATION_XML = MediaType.APPLICATION_XML_VALUE;
    public static final String FORM_DATA = MediaType.MULTIPART_FORM_DATA_VALUE;
    public static final String FORM_URLENCODE = MediaType.APPLICATION_FORM_URLENCODED_VALUE;

    public static final String TEXT_PLAIN_UTF8 = "text/plain; charset=UTF-8";
    public static final String ORACLE_JSON = "application/vnd.oracle.adf.resourceitem+json";
}

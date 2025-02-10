package com.heroku.java.DTO;

import lombok.Data;

@Data
public class LoginInfo {

    private String CompanyDB;
    private String UserName;
    private String Password;
}
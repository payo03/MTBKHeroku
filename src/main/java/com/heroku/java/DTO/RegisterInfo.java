package com.heroku.java.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegisterInfo {

    @JsonProperty("MAN_VACCOUNTLCollection")
    private List<AccountInfo> manVAccountCollection;

    @Data
    public static class AccountInfo {

        @JsonProperty("U_CardCD")
        private String uCardCd;

        @JsonProperty("U_ACCNO")
        private String uAccNo;
    }
}
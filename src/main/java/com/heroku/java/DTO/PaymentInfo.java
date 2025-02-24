package com.heroku.java.DTO;

import lombok.Data;

@Data
public class PaymentInfo {

    private String card_cd;             // BP코드
    private String acc_no;              // 가상계좌 번호
    private String doc_num;             // 문서번호
    private String doc_entry;           // 문서번호(내부)
    private String crncy;               // 통화코드
    private int doc_total;              // 입금금액
    private int doc_total_fc;           // 입금금액(?)
}
package com.heroku.java.DTO;

import lombok.Data;

@Data
public class StockDetail {

    private String shp_no;      // "SHPNO"  : 배송 번호.
    private String shp_date;    // "SHPDT"  : 배송된 날짜 및 시간.
    private String seq;         // "SEQ"    : 순서
    private String item_code;   // "ITEMCD" : 상세 품목 코드
    private int ship_qty;       // "SHPQTY" : 배송된 수량.
}

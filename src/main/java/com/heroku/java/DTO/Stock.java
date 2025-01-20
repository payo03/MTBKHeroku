package com.heroku.java.DTO;

import java.util.List;

import lombok.Data;

@Data
public class Stock {

    private String doc_no;              // "DOCNO"  : 문서 번호를 나타내는 고유 식별자.
    private String item_code;           // "ITEMCD" : 품목(제품)의 고유 코드.
    private String dis_num;             // "DISNUM" : 차대번호
    private String req_date;            // "REQDT"  : 요청 발생 날짜 및 시간.
    private int b_stock_qty;            // "STQTY"  : 이동 전 재고 수량.
    private int a_stock_qty;            // "RSTQTY" : 이동 후 재고 수량.
    private String fwh_code;            // "FWHSCD" : 출고 창고 코드.
    private String twh_code;            // "TWHSCD" : 입고 창고 코드.
    private String part_move_type;      // "PTMOVTP": 재고 이동 유형
    private List<StockDetail> details;  // "STK"    : 재고 상세 정보 배열
}
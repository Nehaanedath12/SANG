package com.sangsolutions.sang.Adapter.RequestHistoryAdapter;

import java.util.HashMap;

public class RequestClass {

    String sDocNo,sDate,sNarration;
    int iTransId;
    String productName,unit,remarks;
    int qty;
    int iProduct;
    public HashMap<Integer, Integer> hashMapBody;

    public String getsDocNo() {
        return sDocNo;
    }

    public void setsDocNo(String sDocNo) {
        this.sDocNo = sDocNo;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public int getiTransId() {
        return iTransId;
    }

    public void setiTransId(int iTransId) {
        this.iTransId = iTransId;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }

    public HashMap<Integer, Integer> getHashMapBody() {
        return hashMapBody;
    }

    public void setHashMapBody(HashMap<Integer, Integer> hashMapBody) {
        this.hashMapBody = hashMapBody;
    }

    public  static  String I_TRANS_ID="iTransId";
    public  static  String S_DOC_N="sDocNo";
    public  static  String S_DATE="sDate";
    public  static  String S_NARRATION="sNarration";

}

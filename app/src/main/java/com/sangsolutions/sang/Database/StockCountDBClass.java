package com.sangsolutions.sang.Database;

public class StockCountDBClass {
    String sDocNo,sDate,stockDate,sNarration,processTime;
    int iTransId,iDocType,iUser,status;

    int iTag1,iTag2,iTag3,iTag4,iTag5,iTag6,iTag7,iTag8,iProduct,fqty;
    String sRemarks,unit;

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

    public String getStockDate() {
        return stockDate;
    }

    public void setStockDate(String stockDate) {
        this.stockDate = stockDate;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public int getiTransId() {
        return iTransId;
    }

    public void setiTransId(int iTransId) {
        this.iTransId = iTransId;
    }

    public int getiDocType() {
        return iDocType;
    }

    public void setiDocType(int iDocType) {
        this.iDocType = iDocType;
    }

    public int getiUser() {
        return iUser;
    }

    public void setiUser(int iUser) {
        this.iUser = iUser;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getiTag1() {
        return iTag1;
    }

    public void setiTag1(int iTag1) {
        this.iTag1 = iTag1;
    }

    public int getiTag2() {
        return iTag2;
    }

    public void setiTag2(int iTag2) {
        this.iTag2 = iTag2;
    }

    public int getiTag3() {
        return iTag3;
    }

    public void setiTag3(int iTag3) {
        this.iTag3 = iTag3;
    }

    public int getiTag4() {
        return iTag4;
    }

    public void setiTag4(int iTag4) {
        this.iTag4 = iTag4;
    }

    public int getiTag5() {
        return iTag5;
    }

    public void setiTag5(int iTag5) {
        this.iTag5 = iTag5;
    }

    public int getiTag6() {
        return iTag6;
    }

    public void setiTag6(int iTag6) {
        this.iTag6 = iTag6;
    }

    public int getiTag7() {
        return iTag7;
    }

    public void setiTag7(int iTag7) {
        this.iTag7 = iTag7;
    }

    public int getiTag8() {
        return iTag8;
    }

    public void setiTag8(int iTag8) {
        this.iTag8 = iTag8;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }

    public int getFqty() {
        return fqty;
    }

    public void setFqty(int fqty) {
        this.fqty = fqty;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    //HEADER
    public static  String I_TRANS_ID="iTransId";
    public static  String S_DOC_NO="sDocNo";
    public static  String S_DATE="sDate";
    public static  String STOCK_DATE="sStockDate";
    public static  String I_DOC_TYPE="iDocType";
    public static  String S_NARRATION="sNarration";
    public static  String PROCESS_TIME="processTime";
    public static  String STATUS="status";


    public static  String I_TAG_1="iTag1";
    public static  String I_TAG_2="iTag2";
    public static  String I_TAG_3="iTag3";
    public static  String I_TAG_4="iTag4";

    public static  String I_TAG_5="iTag5";
    public static  String I_TAG_6="iTag6";
    public static  String I_TAG_7="iTag7";
    public static  String I_TAG_8="iTag8";

    public static  String I_PRODUCT="iProduct";
    public static  String F_QTY="fQty";
    public static  String S_REMARKS="sRemarks";
    public static  String S_UNITS="sUnits";
}

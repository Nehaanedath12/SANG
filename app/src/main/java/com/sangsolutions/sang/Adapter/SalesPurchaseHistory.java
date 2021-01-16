package com.sangsolutions.sang.Adapter;

public class SalesPurchaseHistory {

    public int iTransId,iAccount1,nAmount;
    public  String sDocNo,sDate,sAccount1,sAccount2;

    public SalesPurchaseHistory() {
    }

    public SalesPurchaseHistory(int iTransId, int iAccount1, int nAmount, String sDocNo, String sDate, String sAccount1, String sAccount2) {
        this.iTransId = iTransId;
        this.iAccount1 = iAccount1;
        this.nAmount = nAmount;
        this.sDocNo = sDocNo;
        this.sDate = sDate;
        this.sAccount1 = sAccount1;
        this.sAccount2 = sAccount2;
    }

    public int getiTransId() {
        return iTransId;
    }

    public void setiTransId(int iTransId) {
        this.iTransId = iTransId;
    }

    public int getiAccount1() {
        return iAccount1;
    }

    public void setiAccount1(int iAccount1) {
        this.iAccount1 = iAccount1;
    }

    public int getnAmount() {
        return nAmount;
    }

    public void setnAmount(int nAmount) {
        this.nAmount = nAmount;
    }

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

    public String getsAccount1() {
        return sAccount1;
    }

    public void setsAccount1(String sAccount1) {
        this.sAccount1 = sAccount1;
    }

    public String getsAccount2() {
        return sAccount2;
    }

    public void setsAccount2(String sAccount2) {
        this.sAccount2 = sAccount2;
    }


    public  static  String I_TRANS_ID="iTransId";
    public  static  String I_ACCOUNT1="iAccount1";
    public  static  String N_AMOUNT="nAmount";
    public  static  String S_DOC_NO="sDocNo";
    public  static  String S_DATE="sDate";
    public  static  String S_ACCOUNT1="sAccount1";
    public  static  String S_ACCOUNT2="sAccount2";

}

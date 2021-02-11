package com.sangsolutions.sang.Adapter.PaymentReceiptHistoryAdapter;

public class PaymentReceiptHistory {

    public int iTransId,iAccount1,iAccount2,fAmount,iPaymentMethod,iBank,iChequeNo;
    public  String sDocNo,sDate,sAccount1,sAccount2,sChequeDate;

    public PaymentReceiptHistory(int iTransId, int iAccount1, int iAccount2, int fAmount,
                                 int iPaymentMethod, int iBank, int iChequeNo, String sDocNo,
                                 String sDate, String sAccount1, String sAccount2, String sChequeDate) {
        this.iTransId = iTransId;
        this.iAccount1 = iAccount1;
        this.iAccount2 = iAccount2;
        this.fAmount = fAmount;
        this.iPaymentMethod = iPaymentMethod;
        this.iBank = iBank;
        this.iChequeNo = iChequeNo;
        this.sDocNo = sDocNo;
        this.sDate = sDate;
        this.sAccount1 = sAccount1;
        this.sAccount2 = sAccount2;
        this.sChequeDate = sChequeDate;
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

    public int getiAccount2() {
        return iAccount2;
    }

    public void setiAccount2(int iAccount2) {
        this.iAccount2 = iAccount2;
    }

    public int getfAmount() {
        return fAmount;
    }

    public void setfAmount(int fAmount) {
        this.fAmount = fAmount;
    }

    public int getiPaymentMethod() {
        return iPaymentMethod;
    }

    public void setiPaymentMethod(int iPaymentMethod) {
        this.iPaymentMethod = iPaymentMethod;
    }

    public int getiBank() {
        return iBank;
    }

    public void setiBank(int iBank) {
        this.iBank = iBank;
    }

    public int getiChequeNo() {
        return iChequeNo;
    }

    public void setiChequeNo(int iChequeNo) {
        this.iChequeNo = iChequeNo;
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

    public String getsChequeDate() {
        return sChequeDate;
    }

    public void setsChequeDate(String sChequeDate) {
        this.sChequeDate = sChequeDate;
    }

    public  static  String I_TRANS_ID="iTransId";
    public  static  String I_ACCOUNT1="iAccount1";
    public  static  String S_ACCOUNT1="sAccount1";
    public  static  String I_ACCOUNT2="iAccount2";
    public  static  String S_ACCOUNT2="sAccount2";
    public  static  String F_AMOUNT="fAmount";
    public  static  String S_DOC_NO="sDocNo";
    public  static  String S_DATE="sDate";

    public  static  String I_PAYMENT_METHOD="iPaymentMethod";
    public  static  String I_BANK ="iBank";
    public  static  String I_CHEQUE_NO="iChequeNo";
    public  static  String S_CHEQUE_DATE="sChequeDate";


}

package com.sangsolutions.sang.Adapter.BankAdapter;

public class Bank {
    String sName,sCode;
    int iId;

    public Bank() {
    }

    public Bank(String sName, String sCode, int iId) {
        this.sName = sName;
        this.sCode = sCode;
        this.iId = iId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public int getiId() {
        return iId;
    }

    public void setiId(int iId) {
        this.iId = iId;
    }

    public  static  String I_ID="iId";
    public  static  String S_NAME="sName";
    public  static  String S_CODE="sCode";
}

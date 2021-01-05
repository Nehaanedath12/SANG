package com.sangsolutions.sang.Adapter;

public class TagDetails {
    String sCode,sName,sAltName,iType;
    int iId;

    public TagDetails(String sCode, String sName, String sAltName, int iId, String iType) {
        this.sCode = sCode;
        this.sName = sName;
        this.sAltName = sAltName;
        this.iId = iId;
        this.iType=iType;
    }

    public String getsCode() {
        return sCode;
    }

    public String getsName() {
        return sName;
    }

    public String getsAltName() {
        return sAltName;
    }

    public String getiType() {
        return iType;
    }

    public int getiId() {
        return iId;
    }
    public  static  String I_ID="iId";
    public  static  String S_NAME="sName";
    public  static  String S_CODE="sCode";
    public  static  String S_ALT_NAME="sAltName";
    public  static  String I_TYPE="iType";
}

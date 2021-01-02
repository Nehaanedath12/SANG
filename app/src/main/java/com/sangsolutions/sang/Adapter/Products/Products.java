package com.sangsolutions.sang.Adapter.Products;

public class Products {

    String sCode,sName,sAltName;
    int iId;

    public Products(String sCode, String sName, String sAltName, int iId) {
        this.sCode = sCode;
        this.sName = sName;
        this.sAltName = sAltName;
        this.iId = iId;
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

    public int getiId() {
        return iId;
    }

    public static  String I_ID="iId";
    public  static  String S_NAME="sName";
    public  static  String S_CODE="sCode";
    public  static  String S_ALT_NAME="sAltName";
}

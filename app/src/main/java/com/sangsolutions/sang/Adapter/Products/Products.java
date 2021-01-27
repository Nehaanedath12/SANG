package com.sangsolutions.sang.Adapter.Products;

public class Products {

    String sCode,sName,sAltName,sUnit,sBarcode;
    int iId;

    public Products() {
    }

    public Products(String sCode, String sName, String sAltName, int iId,String sUnit,String sBarcode) {
        this.sCode = sCode;
        this.sName = sName;
        this.sAltName = sAltName;
        this.iId = iId;
        this.sUnit=sUnit;
        this.sBarcode=sBarcode;
    }

    public String getsBarcode() {
        return sBarcode;
    }

    public void setsBarcode(String sBarcode) {
        this.sBarcode = sBarcode;
    }

    public String getsUnit() {
        return sUnit;
    }

    public void setsUnit(String sUnit) {
        this.sUnit = sUnit;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setsAltName(String sAltName) {
        this.sAltName = sAltName;
    }

    public void setiId(int iId) {
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
    public  static  String S_UNIT="sUnit";
    public  static  String S_BARCODE="sBarcode";
}

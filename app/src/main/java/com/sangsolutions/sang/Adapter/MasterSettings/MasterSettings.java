package com.sangsolutions.sang.Adapter.MasterSettings;

public class MasterSettings {
    String sName,sAltName;
    int iId;

    public MasterSettings(String sName, String sAltName, int iId) {
        this.sName = sName;
        this.sAltName = sAltName;
        this.iId = iId;
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
    public  static  String S_ALT_NAME="sAltName";
}

package com.sangsolutions.sang.Adapter;

public class User {
    int iId;
    String sLoginName,sPassword;

    public User() {
    }

    public User(int iId, String sLoginName, String sPassword) {
        this.iId = iId;
        this.sLoginName = sLoginName;
        this.sPassword = sPassword;
    }

    public void setsLoginName(String sLoginName) {
        this.sLoginName = sLoginName;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public int getiId() {
        return iId;
    }

    public String getsLoginName() {
        return sLoginName;
    }

    public String getsPassword() {
        return sPassword;
    }

    public static  String I_ID="iId";
    public  static  String S_LOGIN_NAME="sLoginName";
    public  static  String S_PASSWORD="sPassword";
}

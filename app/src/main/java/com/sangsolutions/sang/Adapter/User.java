package com.sangsolutions.sang.Adapter;

public class User {
    int iId,iStatus;
    String sLoginName,sPassword,sUserName,bWeb,bMob;

    public User() {
    }

    public User(int iId, String sLoginName, String sPassword, int iStatus, String sUserName, String bWeb, String bMob) {
        this.iId = iId;
        this.sLoginName = sLoginName;
        this.sPassword = sPassword;
        this.iStatus=iStatus;
        this.sUserName=sUserName;
        this.bMob=bMob;
        this.bWeb=bWeb;
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

    public void setiId(int iId) {
        this.iId = iId;
    }

    public int getiStatus() {
        return iStatus;
    }

    public void setiStatus(int iStatus) {
        this.iStatus = iStatus;
    }

    public String getsUserName() {
        return sUserName;
    }

    public void setsUserName(String sUserName) {
        this.sUserName = sUserName;
    }

    public String getbWeb() {
        return bWeb;
    }

    public void setbWeb(String bWeb) {
        this.bWeb = bWeb;
    }

    public String getbMob() {
        return bMob;
    }

    public void setbMob(String bMob) {
        this.bMob = bMob;
    }

    public static  String I_ID="iId";
    public  static  String S_LOGIN_NAME="sLoginName";
    public  static  String S_PASSWORD="sPassword";
    public static  String I_STATUS="iStatus";
    public  static  String S_USERNAME="sUserName";
    public  static  String B_WEB="bWeb";
    public  static  String B_MOB="bMob";
}

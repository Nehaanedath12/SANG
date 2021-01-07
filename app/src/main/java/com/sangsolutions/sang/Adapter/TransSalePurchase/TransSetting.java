package com.sangsolutions.sang.Adapter.TransSalePurchase;

public class TransSetting {

    int iDocType,iTagId,iTagPosition;
    String  bVisible,bMandatory;

    public TransSetting(int iDocType, int iTagId, String bVisible, String bMandatory,int iTagPosition) {
        this.iDocType = iDocType;
        this.iTagId = iTagId;
        this.bVisible = bVisible;
        this.bMandatory = bMandatory;
        this.iTagPosition=iTagPosition;
    }

    public int getiDocType() {
        return iDocType;
    }

    public int getiTagId() {
        return iTagId;
    }

    public String getbVisible() {
        return bVisible;
    }

    public String getbMandatory() {
        return bMandatory;
    }

    public int getiTagPosition() {
        return iTagPosition;
    }

    public static  String I_DOC_TYPE="iDocType";
    public static  String I_TAG_ID="iTagId";
    public static  String B_VISIBLE="bVisible";
    public static  String B_MANDATORY="bMandatory";
    public static  String I_TAG_POSITION="iTagPosition";

}

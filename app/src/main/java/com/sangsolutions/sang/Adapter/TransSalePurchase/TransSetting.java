package com.sangsolutions.sang.Adapter.TransSalePurchase;

public class TransSetting {

    int iDocType,iTagId;
    boolean bVisible,bMandatory;

    public TransSetting(int iDocType, int iTagId, boolean bVisible, boolean bMandatory) {
        this.iDocType = iDocType;
        this.iTagId = iTagId;
        this.bVisible = bVisible;
        this.bMandatory = bMandatory;
    }

    public int getiDocType() {
        return iDocType;
    }

    public int getiTagId() {
        return iTagId;
    }

    public boolean isbVisible() {
        return bVisible;
    }

    public boolean isbMandatory() {
        return bMandatory;
    }


    public static  String I_DOC_TYPE="iDocType";
    public static  String I_TAG_ID="iTagId";
    public static  String B_VISIBLE="bVisible";
    public static  String B_MANDATORY="bMandatory";

}

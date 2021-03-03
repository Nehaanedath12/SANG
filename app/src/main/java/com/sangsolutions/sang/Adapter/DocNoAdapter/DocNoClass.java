package com.sangsolutions.sang.Adapter.DocNoAdapter;

public class DocNoClass {
    String docNo;
    int iTransId;

    public DocNoClass(String docNo, int iTransId) {
        this.docNo = docNo;
        this.iTransId = iTransId;
    }

    public String getDocNo() {
        return docNo;
    }

    public int getiTransId() {
        return iTransId;
    }
    public  static  String I_TRANS_ID="iTransId";
    public  static  String S_DOC_NO="sDocNo";
}

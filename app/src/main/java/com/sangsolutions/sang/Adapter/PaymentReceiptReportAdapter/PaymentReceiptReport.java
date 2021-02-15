package com.sangsolutions.sang.Adapter.PaymentReceiptReportAdapter;

public class PaymentReceiptReport {

    String sDocNo,Date,Account1,Account2,iCreatedBy,Tag1,Tag2,Tag3,Tag4,Tag5,Tag6,Tag7,Tag8;
    int iTransId,iRefDocId,iAuth;

    public PaymentReceiptReport(String sDocNo, String date, String account1, String account2,
                                String tag1, String tag2, String tag3, String tag4,
                                String tag5, String tag6, String tag7, String tag8) {
        this.sDocNo = sDocNo;
        Date = date;
        Account1 = account1;
        Account2 = account2;
        Tag1 = tag1;
        Tag2 = tag2;
        Tag3 = tag3;
        Tag4 = tag4;
        Tag5 = tag5;
        Tag6 = tag6;
        Tag7 = tag7;
        Tag8 = tag8;
        this.iTransId = iTransId;
        this.iRefDocId = iRefDocId;
        this.iAuth = iAuth;
    }

}

package com.sangsolutions.sang.Adapter.SalesPurchaseReportAdapter;

public class SalesPurchaseReport {
    String sDocNo,Date,Account1,Account2,qty,rate,disc,vat,sNarration,Product,Remarks,Tag1,Tag2,Tag3,Tag4,Tag5,Tag6,Tag7,Tag8;

    public SalesPurchaseReport(String sDocNo, String date, String account1, String account2, String product, String qty, String rate, String vat, String discount, String tag1, String tag2, String tag3, String tag4, String tag5, String tag6, String tag7, String tag8) {

        this.sDocNo=sDocNo;
        this.Date=date;
        this.Account1=account1;
        this.Account2=account2;
        this.Product=product;
        this.qty=qty;
        this.rate=rate;
        this.disc=discount;
        this.vat=vat;
        this.Tag1=tag1;
        this.Tag2=tag2;
        this.Tag3=tag3;
        this.Tag4=tag4;
        this.Tag5=tag5;
        this.Tag6=tag6;
        this.Tag7=tag7;
        this.Tag8=tag8;



    }


    public String getsDocNo() {
        return sDocNo;
    }

    public String getDate() {
        return Date;
    }

    public String getAccount1() {
        return Account1;
    }

    public String getAccount2() {
        return Account2;
    }

    public String getProduct() {
        return Product;
    }

    public String getTag1() {
        return Tag1;
    }

    public String getTag2() {
        return Tag2;
    }

    public String getTag3() {
        return Tag3;
    }

    public String getTag4() {
        return Tag4;
    }

    public String getTag5() {
        return Tag5;
    }

    public String getTag6() {
        return Tag6;
    }

    public String getTag7() {
        return Tag7;
    }

    public String getTag8() {
        return Tag8;
    }
}

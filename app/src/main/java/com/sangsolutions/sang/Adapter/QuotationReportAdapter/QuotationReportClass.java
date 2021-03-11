package com.sangsolutions.sang.Adapter.QuotationReportAdapter;

public class QuotationReportClass {
    String sDocNo,Date,rate,Account1,Account2,qty,Product,
            Tag1,Tag2,Tag3,Tag4,Tag5,Tag6,Tag7,Tag8;

    public QuotationReportClass(String sDocNo, String date, String rate,
                                String account1, String account2, String qty, String product,
                                String tag1, String tag2, String tag3, String tag4,
                                String tag5, String tag6, String tag7, String tag8) {
        this.sDocNo = sDocNo;
        Date = date;
        this.rate = rate;
        Account1 = account1;
        Account2 = account2;
        this.qty = qty;
        Product = product;
        Tag1 = tag1;
        Tag2 = tag2;
        Tag3 = tag3;
        Tag4 = tag4;
        Tag5 = tag5;
        Tag6 = tag6;
        Tag7 = tag7;
        Tag8 = tag8;
    }

    public String getsDocNo() {
        return sDocNo;
    }

    public void setsDocNo(String sDocNo) {
        this.sDocNo = sDocNo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAccount1() {
        return Account1;
    }

    public void setAccount1(String account1) {
        Account1 = account1;
    }

    public String getAccount2() {
        return Account2;
    }

    public void setAccount2(String account2) {
        Account2 = account2;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }



    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }


    public String getTag1() {
        return Tag1;
    }

    public void setTag1(String tag1) {
        Tag1 = tag1;
    }

    public String getTag2() {
        return Tag2;
    }

    public void setTag2(String tag2) {
        Tag2 = tag2;
    }

    public String getTag3() {
        return Tag3;
    }

    public void setTag3(String tag3) {
        Tag3 = tag3;
    }

    public String getTag4() {
        return Tag4;
    }

    public void setTag4(String tag4) {
        Tag4 = tag4;
    }

    public String getTag5() {
        return Tag5;
    }

    public void setTag5(String tag5) {
        Tag5 = tag5;
    }

    public String getTag6() {
        return Tag6;
    }

    public void setTag6(String tag6) {
        Tag6 = tag6;
    }

    public String getTag7() {
        return Tag7;
    }

    public void setTag7(String tag7) {
        Tag7 = tag7;
    }

    public String getTag8() {
        return Tag8;
    }

    public void setTag8(String tag8) {
        Tag8 = tag8;
    }
}

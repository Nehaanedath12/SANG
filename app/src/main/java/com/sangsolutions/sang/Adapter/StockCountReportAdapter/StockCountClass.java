package com.sangsolutions.sang.Adapter.StockCountReportAdapter;

public class StockCountClass {
    String sDocNo,Date,stockDate,qty,sNarration,Product,Remarks,Tag1,Tag2,Tag3,Tag4,Tag5,Tag6,Tag7,Tag8;

    public StockCountClass(String sDocNo, String date, String stockDate, String qty,
                           String product,String tag1, String tag2, String tag3, String tag4,
                           String tag5, String tag6, String tag7, String tag8) {
        this.sDocNo = sDocNo;
        Date = date;
        this.stockDate = stockDate;
        this.qty = qty;
        this.sNarration = sNarration;
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

    public String getStockDate() {
        return stockDate;
    }

    public void setStockDate(String stockDate) {
        this.stockDate = stockDate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
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

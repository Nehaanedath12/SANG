package com.sangsolutions.sang.Adapter.BatchSalesAdapter;

public class BatchSales {
    public String batch,mfDate,expDate;
    public int qty,enterQty,iId,iProduct;

    public BatchSales() {
    }

    public BatchSales(String batch, String mfDate, String expDate, int qty, int enterQty, int iId, int iProduct) {
        this.batch = batch;
        this.mfDate = mfDate;
        this.expDate = expDate;
        this.qty = qty;
        this.enterQty=enterQty;
        this.iProduct=iProduct;
        this.iId=iId;
    }
    public BatchSales(String batch, String mfDate, String expDate, int enterQty,int iId,int iProduct) {
        this.batch = batch;
        this.mfDate = mfDate;
        this.expDate = expDate;
        this.enterQty = enterQty;
        this.iProduct=iProduct;
        this.iId=iId;
    }

    public int getiId() {
        return iId;
    }

    public void setiId(int iId) {
        this.iId = iId;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }

    public int getEnterQty() {
        return enterQty;
    }

    public void setEnterQty(int enterQty) {
        this.enterQty = enterQty;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMfDate() {
        return mfDate;
    }

    public void setMfDate(String mfDate) {
        this.mfDate = mfDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public  static  String S_BATCH="sBatch";
    public  static  String MF_DATE="iMFDate";
    public  static  String EXP_DATE="iExpDate";
    public  static  String F_QTY="fQty";
    public  static  String I_ID="iId";
    public  static  String I_PRODUCT="iProduct";




}

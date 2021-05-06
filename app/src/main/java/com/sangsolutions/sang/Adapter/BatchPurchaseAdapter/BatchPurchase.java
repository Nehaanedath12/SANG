package com.sangsolutions.sang.Adapter.BatchPurchaseAdapter;

public class BatchPurchase {
    String batch,mfDate,expDate;
    int qty;

    public BatchPurchase(String batch, String mfDate, String expDate, int qty) {
        this.batch = batch;
        this.mfDate = mfDate;
        this.expDate = expDate;
        this.qty = qty;
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
}

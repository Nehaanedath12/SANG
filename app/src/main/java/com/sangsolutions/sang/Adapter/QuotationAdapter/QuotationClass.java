package com.sangsolutions.sang.Adapter.QuotationAdapter;

import java.util.HashMap;

public class QuotationClass {

    String productName,unit,remarks;
    Float rate;
    int qty;
    int iProduct;
    public HashMap<Integer, Integer> hashMapBody;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }

    public HashMap<Integer, Integer> getHashMapBody() {
        return hashMapBody;
    }

    public void setHashMapBody(HashMap<Integer, Integer> hashMapBody) {
        this.hashMapBody = hashMapBody;
    }
}

package com.sangsolutions.sang.Adapter.BatchPurchaseBodyAdapter;

import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchase;

import java.util.HashMap;
import java.util.List;

public class BatchPurchaseBody {

    public String productName,unit,remarks;
    public Float gross,net,rate,vat,vatPer,discount,addCharges;
    public int totalQty;
    public int iProduct;
    public HashMap<Integer, Integer> hashMapBody;
    public List<BatchPurchase>batchList;

    public BatchPurchaseBody() {
    }

    public BatchPurchaseBody(String productName, String unit, String remarks,
                             Float gross, Float net, Float rate, Float vat,
                             Float vatPer, Float discount, Float addCharges,
                             int qty, int iProduct, HashMap<Integer, Integer> hashMapBody,
                             List<BatchPurchase> batchList
                             ) {
        this.productName = productName;
        this.unit = unit;
        this.remarks = remarks;
        this.gross = gross;
        this.net = net;
        this.rate = rate;
        this.vat = vat;
        this.vatPer = vatPer;
        this.discount = discount;
        this.addCharges = addCharges;
        this.totalQty = qty;
        this.iProduct = iProduct;
        this.hashMapBody = hashMapBody;
        this.batchList = batchList;
//        this.batch=batch;
//        this.mfDate=mfDate;
//        this.expDate=expDate;
//        this.batchQty=batchQty;
    }

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

    public Float getGross() {
        return gross;
    }

    public void setGross(Float gross) {
        this.gross = gross;
    }

    public Float getNet() {
        return net;
    }

    public void setNet(Float net) {
        this.net = net;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Float getVat() {
        return vat;
    }

    public void setVat(Float vat) {
        this.vat = vat;
    }

    public Float getVatPer() {
        return vatPer;
    }

    public void setVatPer(Float vatPer) {
        this.vatPer = vatPer;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getAddCharges() {
        return addCharges;
    }

    public void setAddCharges(Float addCharges) {
        this.addCharges = addCharges;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

//    public int getBatchQty() {
//        return batchQty;
//    }
//
//    public void setBatchQty(int batchQty) {
//        this.batchQty = batchQty;
//    }

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

    public List<BatchPurchase> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<BatchPurchase> batchList) {
        this.batchList = batchList;
    }

//    public String getBatch() {
//        return batch;
//    }
//
//    public void setBatch(String batch) {
//        this.batch = batch;
//    }
//
//    public String getMfDate() {
//        return mfDate;
//    }
//
//    public void setMfDate(String mfDate) {
//        this.mfDate = mfDate;
//    }
//
//    public String getExpDate() {
//        return expDate;
//    }
//
//    public void setExpDate(String expDate) {
//        this.expDate = expDate;
//    }
}

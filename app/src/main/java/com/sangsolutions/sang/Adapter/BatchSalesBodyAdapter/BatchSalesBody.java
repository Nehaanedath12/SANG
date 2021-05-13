package com.sangsolutions.sang.Adapter.BatchSalesBodyAdapter;

import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchase;
import com.sangsolutions.sang.Adapter.BatchSalesAdapter.BatchSales;

import java.util.HashMap;
import java.util.List;

public class BatchSalesBody {
    public String productName,unit,remarks;
    public Float gross,net,rate,vat,vatPer,discount,addCharges;
    public int totalQty;
    public int iProduct;
    public HashMap<Integer, Integer> hashMapBody;
    public List<BatchSales> batchList;

    public BatchSalesBody() {
    }

    public BatchSalesBody(String productName, String unit, String remarks, Float gross,
                          Float net, Float rate, Float vat, Float vatPer, Float discount,
                          Float addCharges, int totalQty, int iProduct, HashMap<Integer,
            Integer> hashMapBody, List<BatchSales> batchList) {
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
        this.totalQty = totalQty;
        this.iProduct = iProduct;
        this.hashMapBody = hashMapBody;
        this.batchList = batchList;
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

    public List<BatchSales> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<BatchSales> batchList) {
        this.batchList = batchList;
    }
}


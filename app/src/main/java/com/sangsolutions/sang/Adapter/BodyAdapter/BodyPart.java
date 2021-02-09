package com.sangsolutions.sang.Adapter.BodyAdapter;


import java.util.HashMap;

public class BodyPart {
    String productName,unit,remarks;
    Float gross,net,rate,vat,vatPer,discount,addCharges;
    int qty;
    int iProduct;
    public HashMap<Integer, Integer> hashMapBody;

    public BodyPart() {
    }



    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setVat(Float vat) {
        this.vat = vat;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public HashMap<Integer, Integer> getHashMapBody() {
        return hashMapBody;
    }

    public void setHashMapBody(HashMap<Integer, Integer> hashMapBody) {
        this.hashMapBody = hashMapBody;
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

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
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


    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }
}

package com.sangsolutions.sang.Adapter.BodyAdapter;


public class BodyPart {
    String productName,unit;
    Float gross,net,rate,vat,vatPer,discount,addCharges;
    int iProduct,qty;

    public BodyPart() {
    }

    public BodyPart(String productName, String unit, Float gross, Float net, float rate, int qty, float vat, int iProduct) {
        this.productName = productName;
        this.unit = unit;
        this.gross = gross;
        this.net = net;
        this.rate = rate;
        this.qty = qty;
        this.vat = vat;
        this.iProduct = iProduct;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

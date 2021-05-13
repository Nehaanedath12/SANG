package com.sangsolutions.sang.Database;

public class BatchPurchaseClass {

    String sDocNo,sDate,sNarration,processTime;
    int iTransId,iDocType,iAccount1,iAccount2,iUser,status;

    int rawId;

    //body
    Float fRate,fVat,fvatPer,fDiscount,fAddCharges,net;

    //batch
    String batchName,mfDate,expDate;

    int iTag1,iTag2,iTag3,iTag4,iTag5,iTag6,iTag7,iTag8,iProduct,totalQty,batchQty,slNo;
    String sRemarks,unit;

    public BatchPurchaseClass() {
    }

    public int getRawId() {
        return rawId;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getsDocNo() {
        return sDocNo;
    }


    public void setsDocNo(String sDocNo) {
        this.sDocNo = sDocNo;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public int getiTransId() {
        return iTransId;
    }

    public void setiTransId(int iTransId) {
        this.iTransId = iTransId;
    }

    public int getiDocType() {
        return iDocType;
    }

    public void setiDocType(int iDocType) {
        this.iDocType = iDocType;
    }

    public int getiAccount1() {
        return iAccount1;
    }

    public void setiAccount1(int iAccount1) {
        this.iAccount1 = iAccount1;
    }

    public int getiAccount2() {
        return iAccount2;
    }

    public void setiAccount2(int iAccount2) {
        this.iAccount2 = iAccount2;
    }

    public int getiUser() {
        return iUser;
    }

    public void setiUser(int iUser) {
        this.iUser = iUser;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Float getfRate() {
        return fRate;
    }

    public void setfRate(Float fRate) {
        this.fRate = fRate;
    }

    public Float getfVat() {
        return fVat;
    }

    public void setfVat(Float fVat) {
        this.fVat = fVat;
    }

    public Float getFvatPer() {
        return fvatPer;
    }

    public void setFvatPer(Float fvatPer) {
        this.fvatPer = fvatPer;
    }

    public Float getfDiscount() {
        return fDiscount;
    }

    public void setfDiscount(Float fDiscount) {
        this.fDiscount = fDiscount;
    }

    public Float getfAddCharges() {
        return fAddCharges;
    }

    public void setfAddCharges(Float fAddCharges) {
        this.fAddCharges = fAddCharges;
    }

    public Float getNet() {
        return net;
    }

    public void setNet(Float net) {
        this.net = net;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
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

    public int getiTag1() {
        return iTag1;
    }

    public void setiTag1(int iTag1) {
        this.iTag1 = iTag1;
    }

    public int getiTag2() {
        return iTag2;
    }

    public void setiTag2(int iTag2) {
        this.iTag2 = iTag2;
    }

    public int getiTag3() {
        return iTag3;
    }

    public void setiTag3(int iTag3) {
        this.iTag3 = iTag3;
    }

    public int getiTag4() {
        return iTag4;
    }

    public void setiTag4(int iTag4) {
        this.iTag4 = iTag4;
    }

    public int getiTag5() {
        return iTag5;
    }

    public void setiTag5(int iTag5) {
        this.iTag5 = iTag5;
    }

    public int getiTag6() {
        return iTag6;
    }

    public void setiTag6(int iTag6) {
        this.iTag6 = iTag6;
    }

    public int getiTag7() {
        return iTag7;
    }

    public void setiTag7(int iTag7) {
        this.iTag7 = iTag7;
    }

    public int getiTag8() {
        return iTag8;
    }

    public void setiTag8(int iTag8) {
        this.iTag8 = iTag8;
    }

    public int getiProduct() {
        return iProduct;
    }

    public void setiProduct(int iProduct) {
        this.iProduct = iProduct;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getBatchQty() {
        return batchQty;
    }

    public void setBatchQty(int batchQty) {
        this.batchQty = batchQty;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    //HEADER
    public static  String I_TRANS_ID="iTransId";
    public static  String S_DOC_NO="sDocNo";
    public static  String S_DATE="sDate";
    public static  String I_DOC_TYPE="iDocType";
    public static  String I_ACCOUNT_1="iAccount1";
    public static  String I_ACCOUNT_2="iAccount2";
    public static  String S_NARRATION="sNarration";
    public static  String PROCESS_TIME="processTime";
    public static  String STATUS="status";



    //BODY

    public static  String I_TAG_1="iTag1";
    public static  String I_TAG_2="iTag2";
    public static  String I_TAG_3="iTag3";
    public static  String I_TAG_4="iTag4";

    public static  String I_TAG_5="iTag5";
    public static  String I_TAG_6="iTag6";
    public static  String I_TAG_7="iTag7";
    public static  String I_TAG_8="iTag8";

    public static  String I_PRODUCT="iProduct";
    public static  String F_QTY="fQty";
    public static  String F_RATE="fRate";
    public static  String F_DISCOUNT="fDiscount";
    public static  String F_ADD_CHARGES="fAddCharges";
    public static  String F_VAT_PER="fVatPer";
    public static  String F_VAT="fVAT";
    public static  String S_REMARKS="sRemarks";
    public static  String S_UNITS="sUnits";
    public static  String F_NET="fNet";

    //batch
    public static  String BATCH_NAME="batchName";
    public static  String BATCH_QTY="batchQty";
    public static  String MF_DATE="mfDate";
    public static  String EXP_DATE="expDate";

    public static  String SL_NO="slNo";
    public static  String I_ROW_ID="iRowId";
}

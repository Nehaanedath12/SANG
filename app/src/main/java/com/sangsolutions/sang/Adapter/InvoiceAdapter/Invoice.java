package com.sangsolutions.sang.Adapter.InvoiceAdapter;

public class Invoice {

    int iTransId;
    String DocDate,DocNo,Amount,Customer,CustomerCode;

    public Invoice(int iTransId, String docDate, String docNo, String amount, String customer, String customerCode) {
        this.iTransId = iTransId;
        DocDate = docDate;
        DocNo = docNo;
        Amount = amount;
        Customer = customer;
        CustomerCode = customerCode;
    }

    public int getiTransId() {
        return iTransId;
    }

    public void setiTransId(int iTransId) {
        this.iTransId = iTransId;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public  static  String DOC_DATE="DocDate";
    public  static  String DOC_NO="DocNo";
    public  static  String AMOUNT="Amount";
    public  static  String CUSTOMER="Customer";
    public  static  String CUSTOMER_CODE="CustomerCode";
    public  static  String I_TRANS_ID="iTransId";
}

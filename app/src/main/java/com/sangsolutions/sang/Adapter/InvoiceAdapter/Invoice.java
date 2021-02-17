package com.sangsolutions.sang.Adapter.InvoiceAdapter;

public class Invoice {

    int iTransId;
    String InvDate, InvNo,Amount,Customer,CustomerCode;

    public Invoice() {
    }

    public Invoice(int iTransId, String invDate, String invNo, String amount, String customer, String customerCode) {
        this.iTransId = iTransId;
        InvDate = invDate;
        InvNo = invNo;
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

    public String getInvDate() {
        return InvDate;
    }

    public void setInvDate(String invDate) {
        InvDate = invDate;
    }

    public String getInvNo() {
        return InvNo;
    }

    public void setInvNo(String invNo) {
        InvNo = invNo;
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

    public  static  String INV_DATE ="InvDate";
    public  static  String INV_NO ="InvNo";
    public  static  String AMOUNT="Amount";
    public  static  String CUSTOMER="Customer";
    public  static  String CUSTOMER_CODE="CustomerCode";
    public  static  String I_TRANS_ID="iTransId";
}

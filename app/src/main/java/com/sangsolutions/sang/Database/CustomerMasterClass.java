package com.sangsolutions.sang.Database;

public class CustomerMasterClass {
    public String name,code,altName,address,city,country,fax,website,pinNo,mobile,phone,
            contactPerson,creditAmount,creditDays,email,processingTime;
    public int iType,iId,iStatus;
    public boolean local;



    public CustomerMasterClass() {
    }

    public CustomerMasterClass(String name, String code, String altName,
                               String address, String city, String country,
                               String fax, String website, int iType, String creditDays,
                               String creditAmount, String pinNo, String mobile, String phone,
                               String contactPerson, String email,int iId, String processingTime,
                               int iStatus,boolean local) {
        this.name = name;
        this.code = code;
        this.altName = altName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.fax = fax;
        this.website = website;
        this.iType = iType;
        this.creditDays = creditDays;
        this.creditAmount = creditAmount;
        this.pinNo = pinNo;
        this.mobile = mobile;
        this.phone = phone;
        this.contactPerson = contactPerson;
        this.email=email;
        this.iId=iId;
        this.iStatus=iStatus;
        this.processingTime=processingTime;
        this.local=local;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public int getiId() {
        return iId;
    }

    public void setiId(int iId) {
        this.iId = iId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPinNo() {
        return pinNo;
    }

    public void setPinNo(String pinNo) {
        this.pinNo = pinNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getiType() {
        return iType;
    }

    public void setiType(int iType) {
        this.iType = iType;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(String creditDays) {
        this.creditDays = creditDays;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public int getiStatus() {
        return iStatus;
    }

    public void setiStatus(int iStatus) {
        this.iStatus = iStatus;
    }

    public static  String LOCAL="local";
    public static  String ID="iId";
    public static  String NAME="name";
    public static  String CODE="code";
    public static  String ALT_NAME="altName";
    public static  String I_TYPE="iType";

    public static  String CREDIT_DAYS="creditDays";
    public static  String CREDIT_AMOUNT="creditAmount";
    public static  String ADDRESS="address";
    public static  String CITY="city";

    public static  String COUNTRY="country";
    public static  String PIN_NO="pinNo";
    public static  String MOBILE_NO="mobNo";
    public static  String PHONE_NO="phoneNo";

    public static  String FAX="fax";
    public static  String EMAIL="email";
    public static  String WEBSITE="website";
    public static  String CONTACT_PERSON_NO="contact_person";

    public static  String PROCESS_TIME="processTime";
    public static  String STATUS="status";
}

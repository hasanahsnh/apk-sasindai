package com.example.sasindai.model;

public class ChannelPaymentData {
    private String code;
    private String name;
    private String type;
    private FeeCustomerData fee_customer;
    private boolean active;

    public ChannelPaymentData() {

    }

    public ChannelPaymentData(String code, String name, String type, FeeCustomerData fee_customer, boolean active) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.fee_customer = fee_customer;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FeeCustomerData getFee_customer() {
        return fee_customer;
    }

    public void setFee_customer(FeeCustomerData fee_customer) {
        this.fee_customer = fee_customer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

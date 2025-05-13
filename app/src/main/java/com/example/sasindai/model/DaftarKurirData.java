package com.example.sasindai.model;

public class DaftarKurirData {
    private String name;
    private String service;
    private int price;
    private String etd;

    public DaftarKurirData(String name, String service, int price, String etd) {
        this.name = name;
        this.service = service;
        this.price = price;
        this.etd = etd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }
}

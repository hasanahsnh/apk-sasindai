package com.example.sasindai.model;

public class FeeCustomerData {
    private int flat;
    private int percent;

    public FeeCustomerData() {

    }

    public FeeCustomerData(int flat, int percent) {
        this.flat = flat;
        this.percent = percent;
    }

    public int getFlat() {
        return flat;
    }

    public void setFlat(int flat) {
        this.flat = flat;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}

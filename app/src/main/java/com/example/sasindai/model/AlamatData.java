package com.example.sasindai.model;

public class AlamatData {
    private String jalan, kota, provinsi, kodePos, negara;

    public AlamatData() {
        // Diperlukan untuk firebase
    }

    public AlamatData(String jalan, String kota, String provinsi, String kodePos, String negara) {
        this.jalan = jalan;
        this.kota = kota;
        this.provinsi = provinsi;
        this.kodePos = kodePos;
        this.negara = negara;
    }

    public String getJalan() {
        return jalan;
    }

    public void setJalan(String jalan) {
        this.jalan = jalan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }

    public String getNegara() {
        return negara;
    }

    public void setNegara(String negara) {
        this.negara = negara;
    }
}

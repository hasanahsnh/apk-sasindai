package com.example.sasindai.model;

public class NotifikasiData {
    String judulNotifikasi, pesanNotifikasi, waktu;

    public NotifikasiData(String judulNotifikasi, String pesanNotifikasi, String waktu) {
        this.judulNotifikasi = judulNotifikasi;
        this.pesanNotifikasi = pesanNotifikasi;
        this.waktu = waktu;
    }

    public NotifikasiData() {
        // Required for database
    }

    public String getJudulNotifikasi() {
        return judulNotifikasi;
    }

    public void setJudulNotifikasi(String judulNotifikasi) {
        this.judulNotifikasi = judulNotifikasi;
    }

    public String getPesanNotifikasi() {
        return pesanNotifikasi;
    }

    public void setPesanNotifikasi(String pesanNotifikasi) {
        this.pesanNotifikasi = pesanNotifikasi;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}

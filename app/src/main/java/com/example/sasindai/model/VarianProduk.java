package com.example.sasindai.model;

import java.util.Map;

public class VarianProduk {
    private String
            idVarian,
            gambar,
            nama,
            size;
    private int
            harga,
            stok;

    private float berat;
    private Map<String, UlasanData> ulasan;

    public VarianProduk() {
        // Diperlukan untuk firebase
    }

    // Gen constructor
    public VarianProduk(Map<String, UlasanData> ulasan, String idVarian, String gambar, String nama, String size, int harga, int stok, float berat) {
        this.gambar = gambar;
        this.nama = nama;
        this.size = size;
        this.harga = harga;
        this.stok = stok;
        this.berat = berat;
        this.idVarian = idVarian;
        this.ulasan = ulasan;
    }

    // Gen getter, setter


    public Map<String, UlasanData> getUlasan() {
        return ulasan;
    }

    public void setUlasan(Map<String, UlasanData> ulasan) {
        this.ulasan = ulasan;
    }

    public String getIdVarian() {
        return idVarian;
    }

    public void setIdVarian(String idVarian) {
        this.idVarian = idVarian;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public float getBerat() {
        return berat;
    }

    public void setBerat(float berat) {
        this.berat = berat;
    }
}

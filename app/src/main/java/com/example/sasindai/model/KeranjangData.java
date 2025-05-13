package com.example.sasindai.model;

import java.util.List;

public class KeranjangData {
    private String idProduk;
    private String namaProduk;
    private String namaVarian;
    private String createAt;
    private String gambarVarian;
    private int harga;
    private int qty;
    private String size;
    private List<VarianProduk> varian;
    private float berat;

    public KeranjangData() {
        //
    }

    public KeranjangData(String idProduk, String namaProduk, String namaVarian, String createAt, String gambarVarian, int harga, int qty, String size, List<VarianProduk> varian, float berat) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.namaVarian = namaVarian;
        this.createAt = createAt;
        this.gambarVarian = gambarVarian;
        this.harga = harga;
        this.qty = qty;
        this.size = size;
        this.varian = varian;
        this.berat = berat;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getNamaVarian() {
        return namaVarian;
    }

    public void setNamaVarian(String namaVarian) {
        this.namaVarian = namaVarian;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getGambarVarian() {
        return gambarVarian;
    }

    public void setGambarVarian(String gambarVarian) {
        this.gambarVarian = gambarVarian;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<VarianProduk> getVarian() {
        return varian;
    }

    public void setVarian(List<VarianProduk> varian) {
        this.varian = varian;
    }

    public float getBerat() {
        return berat;
    }

    public void setBerat(float berat) {
        this.berat = berat;
    }
}

package com.example.sasindai.model;

public class ItemProdukOrderData {
    private String idProduk;
    private String namaProduk;
    private String namaVarian;
    private int harga;
    private int qty;

    public ItemProdukOrderData() {
    }

    public ItemProdukOrderData(String idProduk, String namaProduk, String namaVarian, int harga, int qty) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.namaVarian = namaVarian;
        this.harga = harga;
        this.qty = qty;
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
}

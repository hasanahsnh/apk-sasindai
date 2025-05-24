package com.example.sasindai.model;

public class ItemProdukOrderData {
    private String id_produk;
    private String nama_produk;
    private String nama_varian;
    private int harga;
    private int qty;

    public ItemProdukOrderData() {

    }

    public ItemProdukOrderData(String id_produk, String nama_produk, String nama_varian, int harga, int qty) {
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        this.nama_varian = nama_varian;
        this.harga = harga;
        this.qty = qty;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getNama_varian() {
        return nama_varian;
    }

    public void setNama_varian(String nama_varian) {
        this.nama_varian = nama_varian;
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

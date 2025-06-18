package com.example.sasindai.model;

import java.util.List;

public class OrdersData {
    private String alamat;
    private String created_at;
    private String kurir;
    private String layanan;
    private String metode_pembayaran;
    private String namaLengkap;
    private String no_telp;
    private String order_id;
    private String payment_url;
    private List<ItemProdukOrderData> produk;
    private String status;
    private int total;
    private String uid;
    private String updated_at;
    private String uidPenjual;
    private String statusPesanan;
    private int biayaOngkir;
    private String tipeCheckout;

    public OrdersData() {
    }

    public OrdersData(String alamat, String created_at, String kurir, String layanan, String metode_pembayaran, String namaLengkap, String no_telp, String order_id, String payment_url, List<ItemProdukOrderData> produk, String status, int total, String uid, String updated_at, String uidPenjual, String statusPesanan, int biayaOngkir, String tipeCheckout) {
        this.alamat = alamat;
        this.created_at = created_at;
        this.kurir = kurir;
        this.layanan = layanan;
        this.metode_pembayaran = metode_pembayaran;
        this.namaLengkap = namaLengkap;
        this.no_telp = no_telp;
        this.order_id = order_id;
        this.payment_url = payment_url;
        this.produk = produk;
        this.status = status;
        this.total = total;
        this.uid = uid;
        this.updated_at = updated_at;
        this.uidPenjual = uidPenjual;
        this.statusPesanan = statusPesanan;
        this.biayaOngkir = biayaOngkir;
        this.tipeCheckout = tipeCheckout;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getKurir() {
        return kurir;
    }

    public void setKurir(String kurir) {
        this.kurir = kurir;
    }

    public String getLayanan() {
        return layanan;
    }

    public void setLayanan(String layanan) {
        this.layanan = layanan;
    }

    public String getMetode_pembayaran() {
        return metode_pembayaran;
    }

    public void setMetode_pembayaran(String metode_pembayaran) {
        this.metode_pembayaran = metode_pembayaran;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_url() {
        return payment_url;
    }

    public void setPayment_url(String payment_url) {
        this.payment_url = payment_url;
    }

    public List<ItemProdukOrderData> getProduk() {
        return produk;
    }

    public void setProduk(List<ItemProdukOrderData> produk) {
        this.produk = produk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUidPenjual() {
        return uidPenjual;
    }

    public void setUidPenjual(String uidPenjual) {
        this.uidPenjual = uidPenjual;
    }

    public String getStatusPesanan() {
        return statusPesanan;
    }

    public void setStatusPesanan(String statusPesanan) {
        this.statusPesanan = statusPesanan;
    }

    public int getBiayaOngkir() {
        return biayaOngkir;
    }

    public void setBiayaOngkir(int biayaOngkir) {
        this.biayaOngkir = biayaOngkir;
    }

    public String getTipeCheckout() {
        return tipeCheckout;
    }

    public void setTipeCheckout(String tipeCheckout) {
        this.tipeCheckout = tipeCheckout;
    }
}
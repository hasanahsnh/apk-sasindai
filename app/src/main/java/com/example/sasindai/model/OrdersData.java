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
    private List<ItemProdukOrderData> produk;
    private String snap_token;
    private String status;
    private long total;
    private String uid;
    private String updated_at;

    public OrdersData() {

    }

    public OrdersData(String alamat, String created_at, String kurir, String layanan, String metode_pembayaran, String namaLengkap, String no_telp, String order_id, List<ItemProdukOrderData> produk, String snap_token, String status, long total, String uid, String updated_at) {
        this.alamat = alamat;
        this.created_at = created_at;
        this.kurir = kurir;
        this.layanan = layanan;
        this.metode_pembayaran = metode_pembayaran;
        this.namaLengkap = namaLengkap;
        this.no_telp = no_telp;
        this.order_id = order_id;
        this.produk = produk;
        this.snap_token = snap_token;
        this.status = status;
        this.total = total;
        this.uid = uid;
        this.updated_at = updated_at;
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

    public List<ItemProdukOrderData> getProduk() {
        return produk;
    }

    public void setProduk(List<ItemProdukOrderData> produk) {
        this.produk = produk;
    }

    public String getSnap_token() {
        return snap_token;
    }

    public void setSnap_token(String snap_token) {
        this.snap_token = snap_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
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
}

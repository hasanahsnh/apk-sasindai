package com.example.sasindai.model;

import java.util.List;

public class ProdukData {
    private String
        createAt,
        deskripsiProduk,
        idProduk,
        namaProduk,
        uid,
        updateAt;
    private int
        sisaStok,
        terjual;
    private List<String> urlFotoProduk;
    private List<VarianProduk> varian;

    public ProdukData() {
        // Diperlukan untuk firebase
    }

    // Gen constructor
    public ProdukData(String createAt, String deskripsiProduk, String idProduk, String namaProduk, String uid, String updateAt, int sisaStok, int terjual, List<String> urlFotoProduk, List<VarianProduk> varian) {
        this.createAt = createAt;
        this.deskripsiProduk = deskripsiProduk;
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.uid = uid;
        this.updateAt = updateAt;
        this.sisaStok = sisaStok;
        this.terjual = terjual;
        this.urlFotoProduk = urlFotoProduk;
        this.varian = varian;
    }

    // Gen getter, setter

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public void setDeskripsiProduk(String deskripsiProduk) {
        this.deskripsiProduk = deskripsiProduk;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public int getSisaStok() {
        return sisaStok;
    }

    public void setSisaStok(int sisaStok) {
        this.sisaStok = sisaStok;
    }

    public int getTerjual() {
        return terjual;
    }

    public void setTerjual(int terjual) {
        this.terjual = terjual;
    }

    public List<String> getUrlFotoProduk() {
        return urlFotoProduk;
    }

    public void setUrlFotoProduk(List<String> urlFotoProduk) {
        this.urlFotoProduk = urlFotoProduk;
    }

    public List<VarianProduk> getVarian() {
        return varian;
    }

    public void setVarian(List<VarianProduk> varian) {
        this.varian = varian;
    }
}

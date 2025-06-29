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
    private String uidPenjual;
    private String idVarian;
    private boolean isDataBerubah = false, isTidakTersedia = false;

    public KeranjangData() {
        //
    }

    public KeranjangData(boolean isDataBerubah, boolean isTidakTersedia, String idVarian, String idProduk, String namaProduk, String namaVarian, String createAt, String gambarVarian, int harga, int qty, String size, List<VarianProduk> varian, float berat, String uidPenjual) {
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
        this.uidPenjual = uidPenjual;
        this.idVarian = idVarian;
        this.isDataBerubah = isDataBerubah;
        this.isTidakTersedia = isTidakTersedia;
    }

    public boolean isTidakTersedia() {
        return isTidakTersedia;
    }

    public void setTidakTersedia(boolean tidakTersedia) {
        isTidakTersedia = tidakTersedia;
    }

    public boolean isDataBerubah() {
        return isDataBerubah;
    }

    public void setDataBerubah(boolean dataBerubah) {
        isDataBerubah = dataBerubah;
    }

    public String getIdVarian() {
        return idVarian;
    }

    public void setIdVarian(String idVarian) {
        this.idVarian = idVarian;
    }

    public String getUidPenjual() {
        return uidPenjual;
    }

    public void setUidPenjual(String uidPenjual) {
        this.uidPenjual = uidPenjual;
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

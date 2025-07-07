package com.example.sasindai.model;

public class UlasanData {
    private ItemProdukOrderData itemProduk;
    private String uidPembeli;
    private String komentar;
    private int rating;
    private long waktu;

    public UlasanData(ItemProdukOrderData itemProduk, String uidPembeli, String komentar, int rating, long waktu) {
        this.itemProduk = itemProduk;
        this.uidPembeli = uidPembeli;
        this.komentar = komentar;
        this.rating = rating;
        this.waktu = waktu;
    }

    public UlasanData() {}

    public ItemProdukOrderData getItemProduk() {
        return itemProduk;
    }

    public void setItemProduk(ItemProdukOrderData itemProduk) {
        this.itemProduk = itemProduk;
    }

    public String getUidPembeli() {
        return uidPembeli;
    }

    public void setUidPembeli(String uidPembeli) {
        this.uidPembeli = uidPembeli;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }
}

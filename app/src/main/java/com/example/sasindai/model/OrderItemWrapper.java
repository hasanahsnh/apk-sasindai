package com.example.sasindai.model;

public class OrderItemWrapper {
    private final ItemProdukOrderData produk;
    private final OrdersData order;
    private float rating;
    private String komentar;
    private String mediaUrl;

    public OrderItemWrapper(ItemProdukOrderData produk, OrdersData order, float rating, String komentar, String mediaUrl) {
        this.produk = produk;
        this.order = order;
        this.rating = rating;
        this.komentar = komentar;
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public ItemProdukOrderData getProduk() {
        return produk;
    }

    public OrdersData getOrder() {
        return order;
    }
}


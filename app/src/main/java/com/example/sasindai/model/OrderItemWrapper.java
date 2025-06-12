package com.example.sasindai.model;

public class OrderItemWrapper {
    private final ItemProdukOrderData produk;
    private final OrdersData order;

    public OrderItemWrapper(ItemProdukOrderData produk, OrdersData order) {
        this.produk = produk;
        this.order = order;
    }

    public ItemProdukOrderData getProduk() {
        return produk;
    }

    public OrdersData getOrder() {
        return order;
    }
}


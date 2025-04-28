package com.example.sasindai.model;

public class KatalogMotifData {
    private String motif;
    private String filosofi;
    private String sumberFilosofi;
    private String sumberGambar;
    private String gambarUrl;

    public KatalogMotifData() {
        // Dibutuhkan untuk firebase
    }

    public KatalogMotifData(String motif, String filosofi, String sumberFilosofi, String sumberGambar, String gambarUrl) {
        this.motif = motif;
        this.filosofi = filosofi;
        this.sumberFilosofi = sumberFilosofi;
        this.sumberGambar = sumberGambar;
        this.gambarUrl = gambarUrl;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getFilosofi() {
        return filosofi;
    }

    public void setFilosofi(String filosofi) {
        this.filosofi = filosofi;
    }

    public String getSumberFilosofi() {
        return sumberFilosofi;
    }

    public void setSumberFilosofi(String sumberFilosofi) {
        this.sumberFilosofi = sumberFilosofi;
    }

    public String getSumberGambar() {
        return sumberGambar;
    }

    public void setSumberGambar(String sumberGambar) {
        this.sumberGambar = sumberGambar;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }
}

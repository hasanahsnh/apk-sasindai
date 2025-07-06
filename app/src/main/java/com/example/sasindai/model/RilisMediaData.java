package com.example.sasindai.model;

public class RilisMediaData {

    int BeritaId;
    String judulArtikel, jurnalis, kontenBerita, tanggalTerbit, fotoBeritaUrl;

    public RilisMediaData(int beritaId, String judulArtikel, String jurnalis, String kontenBerita, String tanggalTerbit, String fotoBeritaUrl) {
        BeritaId = beritaId;
        this.judulArtikel = judulArtikel;
        this.jurnalis = jurnalis;
        this.kontenBerita = kontenBerita;
        this.tanggalTerbit = tanggalTerbit;
        this.fotoBeritaUrl = fotoBeritaUrl;
    }

    public RilisMediaData() {
        // Required...
    }

    public int getBeritaId() {
        return BeritaId;
    }

    public void setBeritaId(int beritaId) {
        BeritaId = beritaId;
    }

    public String getJudulArtikel() {
        return judulArtikel;
    }

    public void setJudulArtikel(String judulArtikel) {
        this.judulArtikel = judulArtikel;
    }

    public String getJurnalis() {
        return jurnalis;
    }

    public void setJurnalis(String jurnalis) {
        this.jurnalis = jurnalis;
    }

    public String getKontenBerita() {
        return kontenBerita;
    }

    public void setKontenBerita(String kontenBerita) {
        this.kontenBerita = kontenBerita;
    }

    public String getTanggalTerbit() {
        return tanggalTerbit;
    }

    public void setTanggalTerbit(String tanggalTerbit) {
        this.tanggalTerbit = tanggalTerbit;
    }

    public String getFotoBeritaUrl() {
        return fotoBeritaUrl;
    }

    public void setFotoBeritaUrl(String fotoBeritaUrl) {
        this.fotoBeritaUrl = fotoBeritaUrl;
    }
}

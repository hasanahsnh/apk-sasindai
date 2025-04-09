package com.example.sasindai.model;

public class RilisMediaData {

    private int BeritaId;
    private String judulArtikel, jurnalis, kontenBerita, tanggalTerbit, fotoBeritaUrl;

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

    public String getJudulArtikel() {
        return judulArtikel;
    }

    public String getJurnalis() {
        return jurnalis;
    }

    public String getKontenBerita() {
        return kontenBerita;
    }

    public String getTanggalTerbit() {
        return tanggalTerbit;
    }

    public String getFotoBeritaUrl() {
        return fotoBeritaUrl;
    }
}

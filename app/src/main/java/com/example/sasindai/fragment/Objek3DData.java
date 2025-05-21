package com.example.sasindai.fragment;

public class Objek3DData {
    public String idObjek;
    public String namaObjek;
    public String deskripsiObjek;
    public String glbUrl;
    public String previewObjek;

    public Objek3DData() {}

    public Objek3DData(String idObjek, String namaObjek, String deskripsiObjek, String glbUrl, String previewObjek) {
        this.idObjek = idObjek;
        this.namaObjek = namaObjek;
        this.deskripsiObjek = deskripsiObjek;
        this.glbUrl = glbUrl;
        this.previewObjek = previewObjek;
    }

    public String getIdObjek() {
        return idObjek;
    }

    public void setIdObjek(String idObjek) {
        this.idObjek = idObjek;
    }

    public String getNamaObjek() {
        return namaObjek;
    }

    public void setNamaObjek(String namaObjek) {
        this.namaObjek = namaObjek;
    }

    public String getDeskripsiObjek() {
        return deskripsiObjek;
    }

    public void setDeskripsiObjek(String deskripsiObjek) {
        this.deskripsiObjek = deskripsiObjek;
    }

    public String getGlbUrl() {
        return glbUrl;
    }

    public void setGlbUrl(String glbUrl) {
        this.glbUrl = glbUrl;
    }

    public String getPreviewObjek() {
        return previewObjek;
    }

    public void setPreviewObjek(String previewObjek) {
        this.previewObjek = previewObjek;
    }
}

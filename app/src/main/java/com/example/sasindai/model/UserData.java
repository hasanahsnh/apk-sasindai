package com.example.sasindai.model;

public class UserData {
    private final Boolean emailIsVerified;
    private String uid, email, namaLengkap, noTelp, role, authMethod;
    private AlamatData alamat;

    public UserData(String uid, String email, String namaLengkap, String noTelp, String role, String authMethod, AlamatData alamat, Boolean emailIsVerified) {
        this.uid = uid;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.noTelp = noTelp;
        this.role = role;
        this.authMethod = authMethod;
        this.alamat = alamat;
        this.emailIsVerified = emailIsVerified;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public AlamatData getAlamat() {
        return alamat;
    }

    public void setAlamat(AlamatData alamat) {
        this.alamat = alamat;
    }

    public Boolean getEmailIsVerified() {
        return emailIsVerified;
    }
}

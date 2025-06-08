package com.example.sasindai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Type;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    WebView webView;
    String uid;
    String tipeCheckout, jsonSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        webView = findViewById(R.id.webviewPayment);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        }

        SharedPreferences prefs = getSharedPreferences("checkout_data", MODE_PRIVATE);

        tipeCheckout = getIntent().getStringExtra("tipe_checkout");
        if (tipeCheckout == null) {
            tipeCheckout = prefs.getString("tipe_checkout", null);
        }

        jsonSelectedItems = getIntent().getStringExtra("selectedItems");
        if (jsonSelectedItems == null) {
            jsonSelectedItems = prefs.getString("selectedItems", null);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void kurangiStokProduk(String tipeCheckout, String jsonSelectedItems) {
        Log.d("STOK", "Kurangi stok: tipeCheckout=" + tipeCheckout);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Type type = new TypeToken<List<KeranjangData>>(){}.getType();
        List<KeranjangData> selectedItems = new Gson().fromJson(jsonSelectedItems, type);

        if (selectedItems == null || selectedItems.isEmpty()) {
            Log.e("STOK", "Data selectedItems kosong atau null");
            return;
        }

        for (KeranjangData item : selectedItems) {
            String idProduk = item.getIdProduk();
            String namaVarian = item.getNamaVarian();
            int qty = item.getQty();

            DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference()
                    .child("produk")
                    .child(idProduk);

            produkRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    ProdukData produkData = currentData.getValue(ProdukData.class);
                    if (produkData == null || produkData.getVarian() == null) return Transaction.success(currentData);

                    boolean varianDitemukan = false;
                    for (VarianProduk varian : produkData.getVarian()) {
                        if (varian.getNama().equalsIgnoreCase(namaVarian)) {
                            if (varian.getStok() < qty) {
                                Log.e("STOK", "Stok tidak cukup! Varian: " + namaVarian + ", Stok tersedia: " + varian.getStok() + ", Qty diminta: " + qty);
                                return Transaction.abort();
                            }

                            varian.setStok(varian.getStok() - qty);
                            produkData.setSisaStok(Math.max(0, produkData.getSisaStok() - qty));
                            produkData.setTerjual(produkData.getTerjual() + qty);
                            varianDitemukan = true;
                            break;
                        }
                    }

                    if (!varianDitemukan) {
                        Log.e("STOK", "Varian tidak ditemukan saat pengurangan: " + namaVarian);
                        return Transaction.abort();
                    }

                    currentData.setValue(produkData);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    if (committed) {
                        Log.d("STOK", "Stok berhasil dikurangi: " + idProduk);

                        DatabaseReference keranjangRef = FirebaseDatabase.getInstance().getReference()
                                .child("keranjang")
                                .child(uid)
                                .child(idProduk)
                                .child(namaVarian);

                        keranjangRef.removeValue().addOnSuccessListener(aVoid -> {
                            Log.d("KERANJANG", "Item dihapus dari keranjang: " + namaVarian);
                        });

                    } else {
                        Log.e("STOK", "Gagal kurangi stok: " + (error != null ? error.getMessage() : "Transaksi dibatalkan"));
                    }
                }
            });
        }
    }

    private void kembalikanStokProduk(String tipeCheckout, String jsonSelectedItems) {
        Log.d("STOK", "Kembalikan stok: tipeCheckout=" + tipeCheckout);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Type type = new TypeToken<List<KeranjangData>>(){}.getType();
        List<KeranjangData> selectedItems = new Gson().fromJson(jsonSelectedItems, type);

        for (KeranjangData item : selectedItems) {
            String idProduk = item.getIdProduk();
            String namaVarian = item.getNamaVarian();
            int qty = item.getQty();

            DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference()
                    .child("produk")
                    .child(idProduk);
            produkRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.@NonNull Result doTransaction(@NonNull MutableData currentData) {
                    ProdukData produkData = currentData.getValue(ProdukData.class);
                    if (produkData == null || produkData.getVarian() == null) return Transaction.success(currentData);

                    boolean varianDitemukan = false;
                    for (VarianProduk varian : produkData.getVarian()) {
                        if (varian.getNama().equalsIgnoreCase(namaVarian)) {
                            varian.setStok(varian.getStok() + qty);
                            produkData.setSisaStok(produkData.getSisaStok() + qty);
                            produkData.setTerjual(Math.max(0, produkData.getTerjual() - qty)); // Hindari nilai negatif
                            varianDitemukan = true;
                            break;
                        }
                    }

                    if (!varianDitemukan) {
                        Log.e("STOK", "Varian tidak ditemukan saat pengembalian: " + namaVarian);
                    }

                    currentData.setValue(produkData);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    if (committed) {
                        Log.d("STOK", "Stok berhasil dikembalikan: " + idProduk);
                    } else {
                        Log.e("STOK", "Gagal kembalikan stok: " + error.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        String orderId = getIntent().getStringExtra("order_id");
        tipeCheckout = getIntent().getStringExtra("tipe_checkout");
        jsonSelectedItems = getIntent().getStringExtra("selectedItems");

        if (tipeCheckout == null || jsonSelectedItems == null) {
            SharedPreferences prefs = getSharedPreferences("checkout_data", MODE_PRIVATE);
            if (tipeCheckout == null)
                tipeCheckout = prefs.getString("tipe_checkout", null);
            if (jsonSelectedItems == null)
                jsonSelectedItems = prefs.getString("selectedItems", null);
        }

        Log.d("STOK", "onBackPressed: tipeCheckout=" + tipeCheckout);

        if (orderId != null) {
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("orders")
                    .child(orderId)
                    .child("status");

            statusRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    Log.d("Payment", "Status pesanan: " + status);

                    if ("success".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                        kurangiStokProduk(tipeCheckout, jsonSelectedItems);
                    } else if ("canceled".equalsIgnoreCase(status) || "expired".equalsIgnoreCase(status)) {
                        kembalikanStokProduk(tipeCheckout, jsonSelectedItems);
                    }
                }

                Intent intent = new Intent(this, KeranjangActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }).addOnFailureListener(e -> {
                Log.e("CHECKOUT", "Gagal mengambil status pesanan", e);
                super.onBackPressed();
            });
        } else {
            super.onBackPressed();
        }
    }
}
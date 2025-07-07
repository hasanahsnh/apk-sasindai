package com.example.sasindai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.sasindai.adapter.KeranjangListAdapter;
import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KeranjangActivity extends AppCompatActivity {
    RecyclerView recyclerViewKeranjang;
    SharedPreferences sharedPreferences;
    String userUid;
    List<KeranjangData> keranjangData = new ArrayList<>();
    List<KeranjangData> selectedItems = new ArrayList<>();
    FirebaseUser firebaseUser;
    CheckBox checkBoxSelectAll;
    TextView tvTotalHarga, btnCheckout;
    KeranjangListAdapter adapter;
    LinearLayout progressBarKeranjang, layoutProgressBarKeranjangDataNotFound;
    FrameLayout frameAllProduk;
    LottieAnimationView progressBarKeranjangDataNotFound;
    DatabaseReference produkRefs, produkUtamaRef;
    ValueEventListener produkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_keranjang);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        checkBoxSelectAll = findViewById(R.id.checkBoxSelectAll);
        tvTotalHarga = findViewById(R.id.tvTotalHargaKeranjang);
        btnCheckout = findViewById(R.id.btnCheckout);
        progressBarKeranjang = findViewById(R.id.progressBarKeranjang);
        frameAllProduk = findViewById(R.id.frameAllProduk);
        progressBarKeranjangDataNotFound = findViewById(R.id.progressBarKeranjangDataNotFound);
        layoutProgressBarKeranjangDataNotFound = findViewById(R.id.layoutProgressBarKeranjangDataNotFound);

        // SharedPrefs
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Ambil uid user sekarang
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userUid = firebaseUser.getUid();
            sharedPreferences.edit().putString("userUID", firebaseUser.getUid()).apply();
        } else {
            Log.e("KeranjangActivity", "User belum login, FirebaseUser null");
        }
        Log.d("Karanjang Activity", "User uid get:" + userUid);

        // Inisialisasi Widget
        recyclerViewKeranjang = findViewById(R.id.recyclerViewKeranjang);
        // End inisialisasi widget

        recyclerViewKeranjang.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KeranjangListAdapter(this, keranjangData);
        recyclerViewKeranjang.setAdapter(adapter);
        adapter.clearSelections();

        adapter.setSelectionChangeListener(() -> {
            int total = adapter.getTotalHargaSelected();
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            String formattedTotal = formatRupiah.format(total).replace("Rp", "Rp ");
            tvTotalHarga.setText(adapter.getSelectedItems().isEmpty() ? "Rp 0,00" : formattedTotal);
        });

        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        btnCheckout.setOnClickListener(v -> {
            Log.i("Keranjang actv", "btn checkout berhasil ditekan, task clear");
            selectedItems.clear();
            selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Tidak ada produk yang dipilih!", Toast.LENGTH_SHORT).show();
            } else {
                int totalHarga = adapter.getTotalHargaSelected();
                float totalBerat = adapter.getBeratItemSelected();

                Log.d("CHECK_BERAT", "Berat disimpan: " + totalBerat);

                // dapatkan pref dari keranjang yang di ceklis
                SharedPreferences prefs = getSharedPreferences("ProdukKeranjang", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("total_harga", totalHarga);
                editor.putFloat("total_berat", totalBerat);
                editor.clear();
                editor.apply();
                // end

                // clear pref alamat/rincian kodepos dari pref tersedia
                SharedPreferences alamat = getSharedPreferences("AlamatPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorAlamatDanKurir = alamat.edit();
                Map<String, ?> allEntries = alamat.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    if (!entry.getKey().equals("alamat")) {
                        editorAlamatDanKurir.remove(entry.getKey());
                    }
                }
                editorAlamatDanKurir.apply();

                // clear ekspedisi dipilih sebelumnya
                SharedPreferences kurirPrefs = getSharedPreferences("KurirPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorKurir = kurirPrefs.edit();
                editorKurir.clear();
                editorKurir.apply();

                Gson gson = new Gson();
                String selectedItemsJson = gson.toJson(selectedItems);

                Log.d("KeranjangActivity", "Selected Items: " + selectedItems);

                Intent intent = new Intent(KeranjangActivity.this, DetailPemesananActivity.class);

                intent.putExtra("selectedItems", selectedItemsJson);
                intent.putExtra("tipe_checkout", "item_keranjang");

                startActivity(intent);
            }
        });

        AmbilProdukUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void AmbilProdukUser() {
        progressBarKeranjang.setVisibility(View.VISIBLE); // keep
        layoutProgressBarKeranjangDataNotFound.setVisibility(View.GONE);

        produkUtamaRef = FirebaseDatabase.getInstance().getReference("produk");
        produkRefs = FirebaseDatabase.getInstance().getReference("keranjang").child(userUid);

        produkUtamaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotProdukPusat) {
                Map<String, ProdukData> produkPusatMap = new HashMap<>();

                for (DataSnapshot produkSnapshot : snapshotProdukPusat.getChildren()) {
                    ProdukData produk = produkSnapshot.getValue(ProdukData.class);
                    if (produk != null) {
                        produkPusatMap.put(produkSnapshot.getKey(), produk);
                    }
                }

                produkRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotKeranjang) {
                        keranjangData.clear();
                        adapter.clearSelections();

                        if (!snapshotKeranjang.exists()) {
                            tampilkanKosong();
                            return;
                        }

                        for (DataSnapshot produkSnapshot : snapshotKeranjang.getChildren()) {
                            String idProduk = produkSnapshot.getKey();

                            for (DataSnapshot varianSnapshot : produkSnapshot.getChildren()) {
                                try {
                                    KeranjangData data = varianSnapshot.getValue(KeranjangData.class);
                                    if (data != null) {
                                        String idVarian = varianSnapshot.getKey();
                                        data.setIdProduk(idProduk);
                                        data.setIdVarian(idVarian);

                                        ProdukData produkPusat = produkPusatMap.get(idProduk);

                                        if (produkPusat != null) {
                                            Map<String, VarianProduk> mapVarian = produkPusat.getVarian();
                                            if (mapVarian != null) {
                                                VarianProduk varianPusat = mapVarian.get(idVarian);
                                                if (varianPusat != null) {
                                                    Log.d("CekPerubahan", "Bandingkan produk: " + data.getNamaProduk() + " vs pusat: " + produkPusat.getNamaProduk());
                                                    Log.d("CekPerubahan", "Bandingkan harga: " + data.getHarga() + " vs pusat: " + varianPusat.getHarga());

                                                    if (!produkPusat.getNamaProduk().equals(data.getNamaProduk()) || varianPusat.getHarga() != data.getHarga()) {
                                                        data.setHarga(varianPusat.getHarga());
                                                        data.setDataBerubah(true);
                                                    }
                                                } else {
                                                    // Varian sudah tidak ada di produk pusat
                                                    data.setTidakTersedia(true);
                                                }
                                            } else {
                                                // Produk tidak punya varian apapun
                                                data.setTidakTersedia(true);
                                            }
                                        } else {
                                            // Produk tidak ditemukan
                                            data.setTidakTersedia(true);
                                        }

                                        keranjangData.add(data);
                                    } else {
                                        Log.w("Keranjang", "Data keranjang null, dilewati");
                                    }
                                } catch (Exception e) {
                                    Log.e("KeranjangParse", "Gagal parsing varian keranjang: " + e.getMessage());
                                }
                            }

                            if (!keranjangData.isEmpty()) {
                                tampilkanProduk();
                            } else {
                                tampilkanKosong();
                            }

                            if (recyclerViewKeranjang.getAdapter() != null) {
                                recyclerViewKeranjang.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tampilkanKosong();
                        Log.e("Memuat Keranjang", "Gagal memuat data keranjang, Error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tampilkanKosong();
                Log.e("Memuat Produk Utama", "Gagal memuat data produk, Error: " + error.getMessage());
            }
        });
    }

    private void tampilkanProduk() {
        frameAllProduk.setVisibility(View.VISIBLE);
        progressBarKeranjang.setVisibility(View.GONE);
        layoutProgressBarKeranjangDataNotFound.setVisibility(View.GONE);
        progressBarKeranjangDataNotFound.setVisibility(View.GONE);
        progressBarKeranjangDataNotFound.pauseAnimation();
    }

    private void tampilkanKosong() {
        frameAllProduk.setVisibility(View.GONE);
        progressBarKeranjang.setVisibility(View.GONE);
        layoutProgressBarKeranjangDataNotFound.setVisibility(View.VISIBLE);
        progressBarKeranjangDataNotFound.setVisibility(View.VISIBLE);
        progressBarKeranjangDataNotFound.setRepeatCount(LottieDrawable.INFINITE);
        progressBarKeranjangDataNotFound.playAnimation();
        progressBarKeranjangDataNotFound.setAlpha(0f);
        progressBarKeranjangDataNotFound.animate().alpha(1f).setDuration(400).start(); // animasi fade-in
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (produkRefs != null && produkListener != null) {
            produkRefs.removeEventListener(produkListener);
        }
    }
}
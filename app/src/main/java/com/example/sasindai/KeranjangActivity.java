package com.example.sasindai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.sasindai.adapter.KeranjangListAdapter;
import com.example.sasindai.model.KeranjangData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_keranjang);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

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
                editorAlamatDanKurir.clear();
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("keranjang").child(userUid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keranjangData.clear();
                adapter.clearSelections();

                for (DataSnapshot produkSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot varianSnapshot : produkSnapshot.getChildren()) {
                        KeranjangData data = varianSnapshot.getValue(KeranjangData.class);
                        if (data != null) {
                            keranjangData.add(data);
                        }
                    }
                }

                progressBarKeranjang.setVisibility(View.GONE);

                if (!keranjangData.isEmpty()) {
                    frameAllProduk.setVisibility(View.VISIBLE);
                    progressBarKeranjangDataNotFound.setVisibility(View.GONE);
                } else {
                    frameAllProduk.setVisibility(View.GONE);
                    layoutProgressBarKeranjangDataNotFound.setVisibility(View.VISIBLE);
                    progressBarKeranjangDataNotFound.setVisibility(View.VISIBLE);
                    progressBarKeranjangDataNotFound.setRepeatCount(LottieDrawable.INFINITE);
                    progressBarKeranjangDataNotFound.playAnimation();
                }

                if (recyclerViewKeranjang.getAdapter() != null)
                    recyclerViewKeranjang.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarKeranjang.setVisibility(View.GONE);
                frameAllProduk.setVisibility(View.GONE);
                progressBarKeranjangDataNotFound.setVisibility(View.VISIBLE);
                Log.e("Firebase", "Gagal mengambil data keranjang: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBarKeranjang.setVisibility(View.VISIBLE);
        frameAllProduk.setVisibility(View.GONE);
        progressBarKeranjangDataNotFound.setVisibility(View.GONE);

        checkBoxSelectAll.setChecked(false);

        new Handler().postDelayed(this::AmbilProdukUser, 5000);
    }
}
package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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

import com.example.sasindai.adapter.ProdukFotoSliderAdapter;
import com.example.sasindai.adapter.UkuranListAdapter;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailProdukActivity extends AppCompatActivity {
    private RecyclerView sliderGambarProduk, recyclerViewUkuranVarian;
    private List<String> gambarProduk = new ArrayList<>();
    private List<VarianProduk> ukuranData = new ArrayList<>();
    private ProdukFotoSliderAdapter adapter;
    private UkuranListAdapter adapterUkuran;
    private ShimmerFrameLayout shimmerSliderGambarProduk, shimmerNamaProduk, shimmerDeskripsiProduk, shimmerRentangharga, shimmerUkuran;
    private TextView namaProduk, deskripsiProduk, tvAverageHargaProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_produk);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        // Inisialisasi widget
        sliderGambarProduk = findViewById(R.id.recyclerViewSliderGambarProduk);
        shimmerSliderGambarProduk = findViewById(R.id.shimmerSliderGambarProduk);
        namaProduk = findViewById(R.id.namaProduk);
        shimmerNamaProduk = findViewById(R.id.shimmerNamaProduk);
        deskripsiProduk = findViewById(R.id.deskripsiProduk);
        shimmerDeskripsiProduk = findViewById(R.id.shimmerDeskripsiProduk);
        tvAverageHargaProduk = findViewById(R.id.tvAverageHargaProduk);
        shimmerRentangharga = findViewById(R.id.shimmerRentangharga);
        recyclerViewUkuranVarian = findViewById(R.id.recyclerViewUkuranVarian);
        shimmerUkuran = findViewById(R.id.shimmerUkuran);
        // End inisial

        // Adapter foto produk
        adapter = new ProdukFotoSliderAdapter(this, gambarProduk);
        sliderGambarProduk.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sliderGambarProduk.setAdapter(adapter);
        // End set

        // Adapter ukuran
        adapterUkuran = new UkuranListAdapter(this, ukuranData);
        recyclerViewUkuranVarian.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUkuranVarian.setAdapter(adapterUkuran);
        // End ukuran

        // Intent data ke detail
        tampilkanDetail();
        // End intent

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void tampilkanDetail() {
        String produkJson = getIntent().getStringExtra("produk");
        ProdukData produk = new Gson().fromJson(produkJson, ProdukData.class);

        // Nampilkan data nama produk
        if (produk.getNamaProduk() != null && namaProduk != null) {
            namaProduk.setVisibility(View.VISIBLE);
            shimmerNamaProduk.stopShimmer();
            shimmerNamaProduk.setVisibility(View.GONE);
            namaProduk.setText(produk.getNamaProduk());
        } else {
            shimmerNamaProduk.startShimmer();
            shimmerNamaProduk.setVisibility(View.VISIBLE);
        }
        // End nama produk

        // Gambar produk
        if (produk.getUrlFotoProduk() != null) {
            gambarProduk.addAll(produk.getUrlFotoProduk());
        }
        if (produk.getVarian() != null) {
            for (VarianProduk varianProduk : produk.getVarian()) {
                if (varianProduk.getGambar() != null && !varianProduk.getGambar().isEmpty()) {
                    gambarProduk.add(varianProduk.getGambar());
                }
            }
        }
        if (sliderGambarProduk != null) {
            shimmerSliderGambarProduk.stopShimmer();
            shimmerSliderGambarProduk.setVisibility(View.GONE);
        } else {
            shimmerSliderGambarProduk.startShimmer();
            shimmerSliderGambarProduk.setVisibility(View.VISIBLE);
        }
        // End foto produk

        // Ukuran
        if (produk.getVarian() != null && recyclerViewUkuranVarian != null) {
            ukuranData.clear();
            for (VarianProduk ukuran : produk.getVarian()) {
                if (ukuran.getSize() != null && !ukuran.getSize().isEmpty()) {
                    ukuranData.add(ukuran);
                }
            }

            recyclerViewUkuranVarian.setVisibility(View.VISIBLE);
            shimmerUkuran.stopShimmer();
            shimmerUkuran.setVisibility(View.GONE);

            adapterUkuran.notifyDataSetChanged();
        } else {
            recyclerViewUkuranVarian.setVisibility(View.GONE);
            shimmerUkuran.startShimmer();
            shimmerUkuran.setVisibility(View.VISIBLE);
        }
        // End ukuran

        // Harga
        if (produk.getVarian() != null && !produk.getVarian().isEmpty()) {
            int hargaTermurah = Integer.MAX_VALUE;
            int hargaTermahal = Integer.MIN_VALUE;

            for (VarianProduk hargaProduk : produk.getVarian()) {
                int harga = hargaProduk.getHarga();

                if (harga < hargaTermurah) {
                    hargaTermurah = harga;
                }

                if (harga > hargaTermahal) {
                    hargaTermahal = harga;
                }
            }

            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            if (hargaTermurah == hargaTermahal) {
                tvAverageHargaProduk.setText(numberFormat.format(hargaTermurah).replace("Rp", "Rp "));
            } else {
                String hargaRentang = numberFormat.format(hargaTermurah).replace("Rp", "Rp ") + " - " + numberFormat.format(hargaTermahal).replace("Rp", "Rp ");
                tvAverageHargaProduk.setText(hargaRentang);
            }

            tvAverageHargaProduk.setVisibility(View.VISIBLE);
            shimmerRentangharga.stopShimmer();
            shimmerRentangharga.setVisibility(View.GONE);

        } else {
            tvAverageHargaProduk.setVisibility(View.GONE);
            shimmerRentangharga.startShimmer();
            shimmerRentangharga.setVisibility(View.VISIBLE);
        }
        // End harga

        // Deskripsi produk
        if (produk.getDeskripsiProduk() != null) {
            deskripsiProduk.setText(produk.getDeskripsiProduk());
            deskripsiProduk.setVisibility(View.VISIBLE);
            shimmerDeskripsiProduk.stopShimmer();
            shimmerDeskripsiProduk.setVisibility(View.GONE);
        } else {
            shimmerDeskripsiProduk.startShimmer();
            shimmerDeskripsiProduk.setVisibility(View.VISIBLE);
        }
        // End deskripsi

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
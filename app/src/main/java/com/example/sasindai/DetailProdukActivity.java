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
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DetailProdukActivity extends AppCompatActivity {
    private RecyclerView sliderGambarProduk;
    private List<String> gambarProduk = new ArrayList<>();
    private ProdukFotoSliderAdapter adapter;
    private ShimmerFrameLayout shimmerSliderGambarProduk, shimmerNamaProduk;
    private TextView namaProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_produk);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.gold)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        sliderGambarProduk = findViewById(R.id.recyclerViewSliderGambarProduk);
        shimmerSliderGambarProduk = findViewById(R.id.shimmerSliderGambarProduk);
        namaProduk = findViewById(R.id.namaProduk);
        shimmerNamaProduk = findViewById(R.id.shimmerNamaProduk);

        adapter = new ProdukFotoSliderAdapter(this, gambarProduk);
        sliderGambarProduk.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sliderGambarProduk.setAdapter(adapter);

        // Intent data ke detail
        tampilkanDetail();

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
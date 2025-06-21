package com.example.sasindai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.BottomSheetProdukAdapter;
import com.example.sasindai.adapter.KeranjangListAdapter;
import com.example.sasindai.adapter.ProdukFotoSliderAdapter;
import com.example.sasindai.adapter.UkuranListAdapter;
import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.example.sasindai.theme.ThemeActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailProdukActivity extends AppCompatActivity {
    private RecyclerView sliderGambarProduk, recyclerViewUkuranVarian;
    private List<String> gambarProduk = new ArrayList<>();
    private List<VarianProduk> ukuranData = new ArrayList<>();
    private ProdukData produkData;
    private ProdukFotoSliderAdapter adapter;
    private UkuranListAdapter adapterUkuran;
    private ShimmerFrameLayout shimmerSliderGambarProduk, shimmerNamaProduk, shimmerDeskripsiProduk, shimmerRentangharga, shimmerUkuran;
    private TextView namaProduk, deskripsiProduk, tvAverageHargaProduk, btnTambahProdukDetail, btnBeliSekarang;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_produk);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

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
        btnTambahProdukDetail = findViewById(R.id.btnTambahProdukDetail);
        btnBeliSekarang = findViewById(R.id.btnBeliSekarang);
        // End inisial

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Show bottom sheet
        btnTambahProdukDetail.setOnClickListener(v -> {
            if (currentUser != null) {
                if (currentUser.isEmailVerified()){
                    bottomSheetKeranjang();
                } else {
                    Toast.makeText(this, "Silakan verifikasi email Anda terlebih dahulu", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(DetailProdukActivity.this, AuthHostActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            }

        });
        btnBeliSekarang.setOnClickListener(v -> {
            if (currentUser != null) {
                if (currentUser.isEmailVerified()) {
                    bottomSheetBeliSekarang();
                } else {
                    Toast.makeText(this, "Silakan verifikasi email Anda terlebih dahulu", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(DetailProdukActivity.this, AuthHostActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
        // End show bottom sheet

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

    // Tampilkan bottom shhet beli sekarang
    private void bottomSheetBeliSekarang() {
        Dialog dialog = new Dialog(DetailProdukActivity.this);
        dialog.setContentView(R.layout.bottom_sheet_buy_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RecyclerView recyclerViewBeli = dialog.findViewById(R.id.recyclerViewItemProdukBuy);
        recyclerViewBeli.setLayoutManager(new GridLayoutManager(this, 2));

        BottomSheetProdukAdapter adapterDialog = new BottomSheetProdukAdapter(this, ukuranData);
        recyclerViewBeli.setAdapter(adapterDialog);
        adapterDialog.notifyDataSetChanged();

        TextView tvQuantity = dialog.findViewById(R.id.tvQuantityBuy);
        ImageView btnPlus = dialog.findViewById(R.id.btnPlusBuy);
        ImageView btnMinus = dialog.findViewById(R.id.btnMinusBuy);
        LinearLayout btnBeli = dialog.findViewById(R.id.btnBeliProduk);

        final int[] quantity = {1}; // default qty
        final VarianProduk[] selectedVarian = {null}; // simpan varian yang dipilih

        btnPlus.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        adapterDialog.setOnItemClickListener(varianProduk -> {
            selectedVarian[0] = varianProduk; // set varian yang dipilih
        });

        btnBeli.setOnClickListener(v -> {
            if (selectedVarian[0] == null) {
                Toast.makeText(this, "Pilih varian terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            int jumlahTambah;
            try {
                jumlahTambah = Integer.parseInt(tvQuantity.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Jumlah tidak valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            int stokTersedia = selectedVarian[0].getStok();

            if (jumlahTambah > stokTersedia) {
                Toast.makeText(this, "Jumlah melebihi stok tersedia: " + stokTersedia, Toast.LENGTH_SHORT).show();
            } else {
                KeranjangData beliLangsung = new KeranjangData();
                beliLangsung.setIdProduk(produkData.getIdProduk());
                beliLangsung.setNamaProduk(produkData.getNamaProduk());
                beliLangsung.setIdVarian(selectedVarian[0].getIdVarian());
                beliLangsung.setNamaVarian(selectedVarian[0].getNama());
                beliLangsung.setHarga(selectedVarian[0].getHarga());
                beliLangsung.setQty(jumlahTambah);
                beliLangsung.setSize(selectedVarian[0].getSize());
                beliLangsung.setGambarVarian(selectedVarian[0].getGambar());
                beliLangsung.setBerat(selectedVarian[0].getBerat());
                beliLangsung.setVarian(ukuranData);
                beliLangsung.setUidPenjual(produkData.getUid());

                // Convert ke JSON
                List<KeranjangData> listProduk = new ArrayList<>();
                listProduk.add(beliLangsung);
                String jsonProduk = new Gson().toJson(listProduk);

                int totalHarga = selectedVarian[0].getHarga() * jumlahTambah;
                float totalBerat = selectedVarian[0].getBerat() * jumlahTambah;

                // Simpan total harga ke SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("ProdukKeranjang", MODE_PRIVATE).edit();
                editor.putString("produk_beli_langsung", jsonProduk);
                editor.putInt("total_harga", totalHarga);
                editor.putFloat("total_berat", totalBerat);
                editor.apply();

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

                // Pindah actv
                Intent intent = new Intent(DetailProdukActivity.this, DetailPemesananActivity.class);
                intent.putExtra("tipe_checkout", "beli_sekarang");
                intent.putExtra("selectedItems", jsonProduk);
                startActivity(intent);
            }
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int halfHeight = displayMetrics.heightPixels / 2;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfHeight);
            window.setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }
    // End tampilkan bottom sheet

    // Tampilkan bottom shet keranjang
    private void bottomSheetKeranjang() {
        Dialog dialog = new Dialog(DetailProdukActivity.this);
        dialog.setContentView(R.layout.bottom_sheet_cart_layout);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RecyclerView recyclerViewKeranjang = dialog.findViewById(R.id.recyclerViewItemProdukCart);
        recyclerViewKeranjang.setLayoutManager(new GridLayoutManager(this, 2));

        BottomSheetProdukAdapter adapterDialog = new BottomSheetProdukAdapter(this, ukuranData);
        recyclerViewKeranjang.setAdapter(adapterDialog);
        adapterDialog.notifyDataSetChanged();

        TextView tvQuantity = dialog.findViewById(R.id.tvQuantityCart);
        ImageView btnPlus = dialog.findViewById(R.id.btnPlusCart);
        ImageView btnMinus = dialog.findViewById(R.id.btnMinusCart);
        LinearLayout btnTambah = dialog.findViewById(R.id.btnTambahProdukCart);

        final int[] quantity = {1}; // default qty
        final VarianProduk[] selectedVarian = {null}; // simpan varian yang dipilih

        tvQuantity.setText(String.valueOf(quantity[0]));

        btnPlus.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        adapterDialog.setOnItemClickListener(varianProduk -> {
            selectedVarian[0] = varianProduk; // set varian yang dipilih
        });

        btnTambah.setOnClickListener(v -> {
            if (selectedVarian[0] == null) {
                Toast.makeText(this, "Pilih varian terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            int jumlahTambah;
            try {
                jumlahTambah = Integer.parseInt(tvQuantity.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Jumlah tidak valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            int stokTersedia = selectedVarian[0].getStok();

            if (jumlahTambah > stokTersedia) {
                Toast.makeText(this, "Jumlah melebihi stok tersedia: " + stokTersedia, Toast.LENGTH_SHORT).show();
            } else {
                tambahProdukKeDatabase(selectedVarian[0], quantity[0]);
            }
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int halfHeight = displayMetrics.heightPixels / 2;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfHeight);
            window.setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }
    // End bottom sheet layout

    // Tambah produk ke keranjang
    private void tambahProdukKeDatabase(VarianProduk varianProduk, int i) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (produkData == null) {
            Toast.makeText(this, "Data produk tidak tersedia!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid(); // ambil uid pembeli
        String idProduk = produkData.getIdProduk();
        String idVarian = varianProduk.getIdVarian();
        String createAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        DatabaseReference keranjangRef = FirebaseDatabase.getInstance()
                .getReference("keranjang")
                .child(uid)
                .child(idProduk)
                .child(idVarian);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("idProduk", idProduk);
        cartItem.put("namaProduk", produkData.getNamaProduk());
        cartItem.put("idVarian", varianProduk.getIdVarian());
        cartItem.put("namaVarian", varianProduk.getNama());
        cartItem.put("harga", varianProduk.getHarga());
        cartItem.put("gambarVarian", varianProduk.getGambar());
        cartItem.put("size", varianProduk.getSize());
        cartItem.put("qty", i);
        cartItem.put("createAt", createAt);
        cartItem.put("berat", varianProduk.getBerat());
        cartItem.put("uidPenjual", produkData.getUid());

        keranjangRef.setValue(cartItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailProdukActivity.this, "Produk berhasil ditambah!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailProdukActivity.this, "Gagal menambah produk!", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    // End tambah produk ke db

    // Tampilkan detail produk
    private void tampilkanDetail() {
        String produkJson = getIntent().getStringExtra("produk");
        produkData = new Gson().fromJson(produkJson, ProdukData.class);

        // Nampilkan data nama produk
        if (produkData.getNamaProduk() != null && namaProduk != null) {
            namaProduk.setVisibility(View.VISIBLE);
            shimmerNamaProduk.stopShimmer();
            shimmerNamaProduk.setVisibility(View.GONE);
            namaProduk.setText(produkData.getNamaProduk());
        } else {
            shimmerNamaProduk.startShimmer();
            shimmerNamaProduk.setVisibility(View.VISIBLE);
        }
        // End nama produk

        // Gambar produk
        if (produkData.getUrlFotoProduk() != null) {
            gambarProduk.addAll(produkData.getUrlFotoProduk());
        }
        if (produkData.getVarian() != null) {
            for (VarianProduk varianProduk : produkData.getVarian().values()) {
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
        if (produkData.getVarian() != null && recyclerViewUkuranVarian != null) {
            ukuranData.clear();
            for (VarianProduk ukuran : produkData.getVarian().values()) {
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
        if (produkData.getVarian() != null && !produkData.getVarian().isEmpty()) {
            int hargaTermurah = Integer.MAX_VALUE;
            int hargaTermahal = Integer.MIN_VALUE;

            for (VarianProduk hargaProduk : produkData.getVarian().values()) {
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
        if (produkData.getDeskripsiProduk() != null) {
            deskripsiProduk.setText(produkData.getDeskripsiProduk());
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
    // End tampilkan produk

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
package com.example.sasindai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
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
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        btnCheckout = findViewById(R.id.btnCheckout);

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

        adapter.setSelectionChangeListener(() -> {
            int total = adapter.getTotalHargaSelected();
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            tvTotalHarga.setText(formatRupiah.format(total).replace("Rp", "Rp "));
        });

        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        btnCheckout.setOnClickListener(v -> {
            selectedItems.clear();
            selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Tidak ada produk yang dipilih!", Toast.LENGTH_SHORT).show();
            } else {
                Gson gson = new Gson();
                String selectedItemsJson = gson.toJson(selectedItems);
                Log.d("KeranjangActivity", "Selected Items: " + selectedItems);
                Intent intent = new Intent(KeranjangActivity.this, DetailPemesananActivity.class);
                intent.putExtra("selectedItems", selectedItemsJson);
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

    private void AmbilProdukUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("keranjang").child(userUid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keranjangData.clear();

                for (DataSnapshot produkSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot varianSnapshot : produkSnapshot.getChildren()) {
                        KeranjangData data = varianSnapshot.getValue(KeranjangData.class);
                        if (data != null) {
                            keranjangData.add(data);
                        }
                    }
                }

                if (recyclerViewKeranjang != null) {
                    recyclerViewKeranjang.getAdapter().notifyDataSetChanged();
                } else {
                    Log.e("Keranjang Activity", "Recycler view gagal dimuat atau bernilai null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Gagal mengambil data keranjang: " + error.getMessage());
            }
        });
    }
}
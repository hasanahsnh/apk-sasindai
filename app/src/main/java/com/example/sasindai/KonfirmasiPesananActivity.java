package com.example.sasindai;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.example.sasindai.adapter.KonfirmasiPesananPenilaianAdapter;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrderItemWrapper;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KonfirmasiPesananActivity extends AppCompatActivity {
    RecyclerView rvUlasanVarian;
    List<OrderItemWrapper> listItem = new ArrayList<>();
    KonfirmasiPesananPenilaianAdapter adapter;
    TextView btnSimpan, tvIdPesanan, tvUnggahFotoAtauVideo;
    DatabaseReference rootRef, ordersRef, pengirimanRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_konfirmasi_pesanan);

        rvUlasanVarian = findViewById(R.id.rvUlasanVarian);
        btnSimpan = findViewById(R.id.btnSimpanKonfirmasiPenilaian);
        tvIdPesanan = findViewById(R.id.tvIdPesanan);

        // import tema
        ThemeActivity.applyTheme(this);

        // Ambil data produk dari intent
        String jsonProduk = getIntent().getStringExtra("produk_dipesan");
        if (jsonProduk != null && !jsonProduk.isEmpty()) {
            Map<String, ItemProdukOrderData> produkMap = new Gson().fromJson(
                    jsonProduk,
                    new TypeToken<Map<String, ItemProdukOrderData>>() {}.getType()
            );

            // Pastikan kamu punya constructor yang menerima 1 argumen atau ganti sesuai kebutuhan
            for (ItemProdukOrderData item : produkMap.values()) {
                listItem.add(new OrderItemWrapper(item, null, 0f, "", ""));
            }
        }

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) {
            tvIdPesanan.setText(orderId);
        }

        rvUlasanVarian.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KonfirmasiPesananPenilaianAdapter(this, listItem);
        rvUlasanVarian.setAdapter(adapter);

        // SIMPAN ULASAN
        btnSimpan.setOnClickListener(v -> {
            boolean adaRatingKosong = false;

            for (OrderItemWrapper item : listItem) {
                if (item.getRating() <= 0) {
                    adaRatingKosong = true;
                    break;
                }
            }

            if (adaRatingKosong) {
                Toast.makeText(this, "Mohon beri rating untuk semua produk sebelum menyimpan", Toast.LENGTH_SHORT).show();
                return;
            }

            simpanUlasan();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void simpanUlasan() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("produk");
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        pengirimanRef = FirebaseDatabase.getInstance().getReference("pengiriman");

        String orderId = getIntent().getStringExtra("order_id");

        for (OrderItemWrapper item : listItem) {
            ItemProdukOrderData produk = item.getProduk();
            String idProduk = produk.getIdProduk();
            String idVarian = produk.getIdVarian();
            float rating = item.getRating();
            String komentar = item.getKomentar();

            Log.d("ULASAN", "idProduk: " + idProduk + ", idVarian: " + idVarian + ", uid: " + uid);

            if (rating > 0) {
                DatabaseReference ulasanRef = rootRef
                        .child(idProduk)
                        .child("varian")
                        .child(idVarian)
                        .child("ulasan");

                // Buat random id ulasan
                String idUlasan = ulasanRef.push().getKey();

                if (idUlasan != null) {
                    DatabaseReference currentUlasan = ulasanRef.child(idUlasan);
                    currentUlasan.child("idVarian").setValue(idVarian);
                    currentUlasan.child("idProduk").setValue(idProduk);
                    currentUlasan.child("uidPembeli").setValue(uid);
                    currentUlasan.child("rating").setValue(rating);
                    currentUlasan.child("komentar").setValue(komentar);
                    currentUlasan.child("waktu").setValue(ServerValue.TIMESTAMP);
                }
            }
        }

        if (orderId != null && !orderId.isEmpty()) {
            ordersRef.child(orderId).child("statusPesanan").setValue("pesanan diterima");
            pengirimanRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String idPesanan = child.child("idPesanan").getValue(String.class);

                        Log.d("CEK_PENGIRIMAN", "child key: " + child.getKey());
                        Log.d("CEK_PENGIRIMAN", "idPesanan dari child: " + child.child("idPesanan").getValue());

                        if (orderId.equals(idPesanan)) {
                            // Dapatkan idPengiriman (key node)
                            String idPengiriman = child.getKey();

                            assert idPengiriman != null;
                            pengirimanRef.child(idPengiriman).child("statusPengiriman").setValue("pengiriman selesai");
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Gagal update status pengiriman: " + error.getMessage());
                }
            });
        }

        Toast.makeText(this, "Penilaian berhasil disimpan", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void uploadMediaToFirebase(int position, Uri mediaUri, String fileNamePrefix) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String filename = "ulasan_media/" + fileNamePrefix + "_" + uid + "_" + System.currentTimeMillis();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(filename);
        storageRef.putFile(mediaUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            listItem.get(position).setMediaUrl(url);
                            Toast.makeText(this, "Media berhasil diunggah", Toast.LENGTH_SHORT).show();
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal mengunggah media: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}
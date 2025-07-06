package com.example.sasindai;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.example.sasindai.adapter.TransaksiAdapter;
import com.example.sasindai.model.OrdersData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {
    FrameLayout frameDataOrders;
    RecyclerView recyclerViewRiwayatTransaksi;
    TransaksiAdapter adapter;
    List<OrdersData> ordersData = new ArrayList<>();
    LinearLayout layoutProgressBarRiwayatPesananNotFound, progressBarRiwayatPesanan;
    LottieAnimationView progressBarRiwayatPesananNotFound;
    Query transaksiRefs;
    ValueEventListener transaksiListener;
    ImageView btnFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaksi);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        recyclerViewRiwayatTransaksi = findViewById(R.id.recyclerViewRiwayatTransaksi);
        layoutProgressBarRiwayatPesananNotFound = findViewById(R.id.layoutProgressBarRiwayatPesananNotFound);
        progressBarRiwayatPesanan = findViewById(R.id.progressBarRiwayatPesanan);
        frameDataOrders = findViewById(R.id.frameDataOrders);
        progressBarRiwayatPesananNotFound = findViewById(R.id.progressBarRiwayatPesananNotFound);

        recyclerViewRiwayatTransaksi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(this, ordersData);
        recyclerViewRiwayatTransaksi.setAdapter(adapter);

        ambilDaftarRiwayatTransasksiUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ambilDaftarRiwayatTransasksiUser() {
        transaksiRefs = FirebaseDatabase.getInstance()
                .getReference("orders")
                .orderByChild("uid")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        transaksiListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersData.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrdersData order = orderSnapshot.getValue(OrdersData.class);

                    if (order != null) {
                        ordersData.add(order);
                    }
                }

                if (ordersData != null) {
                    frameDataOrders.setVisibility(View.VISIBLE);
                    progressBarRiwayatPesanan.setVisibility(View.GONE);
                } else {
                    frameDataOrders.setVisibility(View.GONE);
                    progressBarRiwayatPesanan.setVisibility(View.GONE);
                    layoutProgressBarRiwayatPesananNotFound.setVisibility(View.VISIBLE);
                    progressBarRiwayatPesananNotFound.setVisibility(View.VISIBLE);
                    progressBarRiwayatPesananNotFound.playAnimation();
                    progressBarRiwayatPesananNotFound.setRepeatCount(LottieDrawable.INFINITE);
                }

                if (recyclerViewRiwayatTransaksi.getAdapter() != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Gagal mengambil data riwayat pesanan: " + error.getMessage());
                progressBarRiwayatPesanan.setVisibility(View.GONE);
                frameDataOrders.setVisibility(View.GONE);
                progressBarRiwayatPesananNotFound.setVisibility(View.VISIBLE);
            }
        };
        transaksiRefs.addValueEventListener(transaksiListener);
    }

    @Override
    protected void onResume() {
        super.onResume();


        //new Handler().postDelayed(this::ambilDaftarRiwayatTransasksiUser, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transaksiRefs != null && transaksiListener != null) {
            transaksiRefs.removeEventListener(transaksiListener);
        }
    }
}
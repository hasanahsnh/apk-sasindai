package com.example.sasindai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.example.sasindai.adapter.NotifikasiListAdapter;
import com.example.sasindai.model.NotifikasiData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarNotifikasiBroadcastActivity extends AppCompatActivity {
    RecyclerView rvDaftarNotifikasi;
    NotifikasiListAdapter adapter;
    List<NotifikasiData> data = new ArrayList<>();
    DatabaseReference notifikasiRefs;
    LottieAnimationView notifEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_notifikasi_broadcast);

        ThemeActivity.applyTheme(this);

        rvDaftarNotifikasi = findViewById(R.id.rvDaftarNotifikasi);
        notifEmpty = findViewById(R.id.notifEmpty);

        rvDaftarNotifikasi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotifikasiListAdapter(this, data);
        rvDaftarNotifikasi.setAdapter(adapter);

        ambilDaftarNotifikasi();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ambilDaftarNotifikasi() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        long installTime = prefs.getLong("install_time", 0);

        notifikasiRefs = FirebaseDatabase.getInstance().getReference("notifikasi");

        notifikasiRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();

                // Formatter untuk waktu seperti "2025-07-06 00:49:46"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (DataSnapshot notifSnapshot : snapshot.getChildren()) {
                    NotifikasiData notifikasi = notifSnapshot.getValue(NotifikasiData.class);
                    if (notifikasi != null) {
                        try {
                            // Parse waktu lokal (tanpa zona) lalu tetapkan zona Asia/Makassar
                            LocalDateTime localTime = LocalDateTime.parse(notifikasi.getWaktu(), formatter);
                            ZonedDateTime makassarTime = localTime.atZone(ZoneId.of("Asia/Makassar"));
                            long eventTimeMillis = makassarTime.toInstant().toEpochMilli();

                            // Filter berdasar waktu install
                            if (eventTimeMillis >= installTime) {
                                data.add(notifikasi);
                            }
                        } catch (Exception e) {
                            Log.e("ParseError", "Gagal parsing waktu notifikasi: " + e.getMessage());
                        }
                    }
                }

                if (data.isEmpty()) {
                    notifEmpty.setVisibility(View.VISIBLE);
                    notifEmpty.playAnimation();
                    notifEmpty.setRepeatCount(LottieDrawable.INFINITE);
                    notifEmpty.setAlpha(0f);
                    notifEmpty.animate().alpha(1f).setDuration(400).start(); // animasi fade-in
                } else {
                    notifEmpty.setVisibility(View.GONE);
                }

                // Urutkan dari yang terbaru (waktu paling besar ke kecil)
                data.sort(new Comparator<NotifikasiData>() {
                    @Override
                    public int compare(NotifikasiData n1, NotifikasiData n2) {
                        return n2.getWaktu().compareTo(n1.getWaktu()); // descending
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Error", "Error: " + error.getMessage());
            }
        });
    }
}
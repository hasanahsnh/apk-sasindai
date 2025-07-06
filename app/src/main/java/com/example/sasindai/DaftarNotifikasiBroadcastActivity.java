package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.NotifikasiListAdapter;
import com.example.sasindai.model.NotifikasiData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_notifikasi_broadcast);

        ThemeActivity.applyTheme(this);

        rvDaftarNotifikasi = findViewById(R.id.rvDaftarNotifikasi);

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
        notifikasiRefs = FirebaseDatabase.getInstance().getReference("notifikasi");

        notifikasiRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();

                for (DataSnapshot notifSnapshot : snapshot.getChildren()) {
                    NotifikasiData notifikasi = notifSnapshot.getValue(NotifikasiData.class);
                    if (notifikasi != null) {
                        data.add(notifikasi);
                    }
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
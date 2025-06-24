package com.example.sasindai.isLayanan;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sasindai.callback.CallbackLayanan.LayananStatusCallback;

public class IsLayanan {

    public static void kaPasaran(Context context, LayananStatusCallback callback) {
        DatabaseReference layananRef = FirebaseDatabase.getInstance().getReference("layanan");

        layananRef.orderByChild("fitur").equalTo("Ka Pasaran")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isAktif = false;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            String status = child.child("statusFitur").getValue(String.class);
                            if ("aktif".equalsIgnoreCase(status)) {
                                isAktif = true;
                                break;
                            }
                        }

                        callback.onResult(isAktif);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Gagal membaca data layanan: " + error.getMessage());
                        Toast.makeText(context, "Terjadi kesalahan mengambil status layanan", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                    }
                });
    }

}

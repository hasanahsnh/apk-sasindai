package com.example.sasindai.service;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMUtils {
    // perbarui token user != null
    public static void perbaruiTokenFirebase(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Gagal mengambil token", task.getException());
                    return;
                }

                String tokenBaru = task.getResult();
                Log.d("FCM", "Token saat ini: " + tokenBaru);

                // Ambil token lama dari SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String tokenLama = prefs.getString("device_token", null);

                // Bandingkan token lama dan baru
                if (tokenLama == null || !tokenLama.equals(tokenBaru)) {
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(user.getUid())
                            .child("device_token")
                            .setValue(tokenBaru)
                            .addOnSuccessListener(unused -> {
                                Log.d("FCM", "Token berhasil diperbarui");
                                prefs.edit().putString("device_token", tokenBaru).apply();
                            })
                            .addOnFailureListener(e -> Log.e("FCM", "Gagal update token", e));
                } else {
                    Log.d("FCM", "Token tidak berubah, tidak perlu update.");
                }
            });
        }
    }

    // ambil token secara global
    public static void simpanTokenGlobal(Context context) {
        // cek status user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Gagal mengambil token (guest)", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("FCM", "Token guest: " + token);

                FirebaseDatabase.getInstance()
                        .getReference("push_tokens")
                        .child(token)
                        .child("created_at")
                        .setValue(System.currentTimeMillis());
            });
        }
    }

}

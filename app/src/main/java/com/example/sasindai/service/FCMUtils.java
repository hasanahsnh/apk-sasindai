package com.example.sasindai.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMUtils {
    public static void perbaruiTokenFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Gagal mengambil token", task.getException());
                    return;
                }

                String token = task.getResult();
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(user.getUid())
                        .child("device_token")
                        .setValue(token)
                        .addOnSuccessListener(unused -> Log.d("FCM", "Token diperbarui"))
                        .addOnFailureListener(e -> Log.e("FCM", "Gagal update token", e));
            });
        }
    }
}

package com.example.sasindai.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sasindai.MainHostActivity;
import com.example.sasindai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "notif_channel";
    private static final String CHANNEL_NAME = "Notifikasi Umum";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Token baru: " + token);
        updateFirebaseToken(token);
    }

    private void updateFirebaseToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid());

            userRef.child("device_token").setValue(token)
                    .addOnSuccessListener(aVoid ->
                            Log.d("FCM_TOKEN", "Token berhasil diperbarui ke database.")
                    )
                    .addOnFailureListener(e ->
                            Log.e("FCM_TOKEN", "Gagal memperbarui token: " + e.getMessage())
                    );
        } else {
            Log.w("FCM_TOKEN", "Belum login, token tidak dikirim.");
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = null;
        String message = null;

        // Ambil notifikasi dari payload notification
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        // Jika kosong, coba dari payload data
        if ((title == null || message == null) && !remoteMessage.getData().isEmpty()) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        }

        if (title != null && message != null) {
            tampilkanNotifikasi(title, message);
        } else {
            Log.w("FCM", "Tidak ada title/body dalam payload, notifikasi tidak ditampilkan.");
        }
    }

    private void tampilkanNotifikasi(String title, String message) {
        // Buat channel jika belum ada (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(this, MainHostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("from_broadcast", true); // jika ingin tandai berasal dari notifikasi

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifikasi_29)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Periksa izin notifikasi di Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            int notifId = (int) System.currentTimeMillis(); // ID unik agar tidak ditimpa
            managerCompat.notify(notifId, builder.build());

            Log.d("FCM", "Notifikasi ditampilkan: " + title);
        } else {
            Log.w("FCM", "Izin notifikasi belum diberikan (POST_NOTIFICATIONS).");
        }
    }
}

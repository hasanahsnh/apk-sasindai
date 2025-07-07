package com.example.sasindai;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.example.sasindai.fragment.AkunFragment;
import com.example.sasindai.fragment.BerandaFragment;
import com.example.sasindai.fragment.KatalogMotifFragment;
import com.example.sasindai.fragment.RilisMediaFragment;
import com.example.sasindai.service.FCMUtils;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainHostActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    SharedPreferences sharedPreferences;
    boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_host);

        notifikasi();

        FCMUtils.simpanTokenGlobal(this);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        // Inisialisasi widget
        chipNavigationBar = findViewById(R.id.bottom_nav);

        // Cek status login (user)
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (!prefs.contains("install_time")) {
            long currentTime = System.currentTimeMillis();
            prefs.edit().putLong("install_time", currentTime).apply();
        }

        /* Jika widget bottom nav ditemukan, maka
        aktifkan indikator pertama di nav_beranda, dan
        posisikan sebagai BerandaFragment: */

        if (getIntent().getBooleanExtra("from_broadcast", false)) {
            // Tampilkan dialog, pindah ke fragment, atau tampilkan toast
            Toast.makeText(this, "Membuka dari notifikasi broadcast", Toast.LENGTH_SHORT).show();
        }

        if (chipNavigationBar != null) {
            chipNavigationBar.setItemSelected(R.id.nav_beranda, true);
            chipNavigationBar.setOnItemSelectedListener(navListener);

            if (savedInstanceState == null) {
                try {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, new BerandaFragment())
                            .commit();
                } catch (Exception e) {
                    Log.e("Main Host Activity", "Gagal inisialisasi fragment!", e);
                }
            }

        } else {
            Log.e("Main Host Activity", "Bottom nav tidak ditemukan!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void notifikasi() {
        // Minta izin notifikasi (Android 13+), tapi lanjut render UI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Channel notifikasi
        if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "notif_channel",
                    "Notifikasi Umum",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notifikasi diizinkan");
                recreate();
            } else {
                Log.d("Permission", "Notifikasi ditolak");
            }
        }
    }

    private final ChipNavigationBar.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        if (item == R.id.nav_beranda) {
            selectedFragment = new BerandaFragment();
        } else if (item == R.id.nav_katalog) {
            selectedFragment = new KatalogMotifFragment();
        } else if (item == R.id.nav_akun) {
            selectedFragment = new AkunFragment();
        } else if (item == R.id.nav_rilis_media) {
            selectedFragment = new RilisMediaFragment();
        } else {
            Log.w("Main Host Activity", "Gagal menghandle nav yang dipilih:" + item);
        }

        if (selectedFragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, 0)
                        .replace(R.id.main_container, selectedFragment)
                        .commit();
            } catch (Exception e) {
                Log.e("Main Host Activity", "Gagal mengganti fragment", e);
            }

        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        FCMUtils.simpanTokenGlobal(this);
    }
}
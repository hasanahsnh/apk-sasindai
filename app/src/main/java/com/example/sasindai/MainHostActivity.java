package com.example.sasindai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.sasindai.fragment.AkunFragment;
import com.example.sasindai.fragment.BerandaFragment;
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

        // Inisialisasi widget
        chipNavigationBar = findViewById(R.id.bottom_nav);

        // Cek status login (user)
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        /* Jika widget bottom nav ditemukan, maka
        aktifkan indikator pertama di nav_beranda, dan
        posisikan sebagai BerandaFragment: */

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

    private final ChipNavigationBar.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        if (item == R.id.nav_beranda) {
            selectedFragment = new BerandaFragment();
        } else if (item == R.id.nav_akun) {
            selectedFragment = new AkunFragment();
        } else {
            Log.w("Main Host Activity", "Gagal menghandle nav yang dipilih:" + item);
        }

        if (selectedFragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, selectedFragment)
                        .commit();
            } catch (Exception e) {
                Log.e("Main Host Activity", "Gagal mengganti fragment", e);
            }

        }

    };

}
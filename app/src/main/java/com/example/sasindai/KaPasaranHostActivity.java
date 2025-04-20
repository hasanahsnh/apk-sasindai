package com.example.sasindai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sasindai.adapter.KaPasaranPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class KaPasaranHostActivity extends AppCompatActivity {

    TabLayout tabLayoutKaPasaran;
    ViewPager2 viewPager2KaPasaran;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ka_pasaran_host);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.gold)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        // SharedPrefs
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Inisial widget
        tabLayoutKaPasaran = findViewById(R.id.tabLayoutKaPasaran);
        viewPager2KaPasaran = findViewById(R.id.viewPager2KaPasaran);

        // Set tab layout
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Populer")));
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Terbaru")));
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Terlaris")));
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Harga")));

        // Set adapter
        KaPasaranPagerAdapter kaPasaranPagerAdapter = new KaPasaranPagerAdapter(this);
        viewPager2KaPasaran.setAdapter(kaPasaranPagerAdapter);

        new TabLayoutMediator(tabLayoutKaPasaran, viewPager2KaPasaran,
                (tab, i) -> {
                    if (i == 0) {
                        tab.setText("Populer");
                    } else if (i == 1) {
                        tab.setText("Terbaru");
                    } else if (i == 2) {
                        tab.setText("Terlaris");
                    } else {
                        tab.setText("Harga");
                    }
                }
        ).attach();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public SharedPreferences getSharedPreferencesInstance() {
        return sharedPreferences;
    }

}
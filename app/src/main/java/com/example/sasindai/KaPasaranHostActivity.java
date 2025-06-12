package com.example.sasindai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sasindai.adapter.KaPasaranPagerAdapter;
import com.example.sasindai.theme.ThemeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KaPasaranHostActivity extends AppCompatActivity {

    TabLayout tabLayoutKaPasaran;
    ViewPager2 viewPager2KaPasaran;
    SharedPreferences sharedPreferences;
    ImageView imgKeranjang;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ka_pasaran_host);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        // SharedPrefs
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Inisial widget
        tabLayoutKaPasaran = findViewById(R.id.tabLayoutKaPasaran);
        viewPager2KaPasaran = findViewById(R.id.viewPager2KaPasaran);
        imgKeranjang = findViewById(R.id.imgKeranjang);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (imgKeranjang != null) {
            imgKeranjang.setOnClickListener(v -> {
                if (currentUser != null) {
                    Intent intent = new Intent(this, KeranjangActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, AuthHostActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("Ka Pasaran Host", "img keranjang bernilai null");
        }

        // Set tab layout
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Terbaru")));
        tabLayoutKaPasaran.addTab((tabLayoutKaPasaran.newTab().setText("Terlaris")));

        // Set adapter
        KaPasaranPagerAdapter kaPasaranPagerAdapter = new KaPasaranPagerAdapter(this);
        viewPager2KaPasaran.setAdapter(kaPasaranPagerAdapter);

        new TabLayoutMediator(tabLayoutKaPasaran, viewPager2KaPasaran,
                (tab, i) -> {
                    if (i == 0) {
                        tab.setText("terbaru");
                    } else if (i == 1) {
                        tab.setText("terlaris");
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
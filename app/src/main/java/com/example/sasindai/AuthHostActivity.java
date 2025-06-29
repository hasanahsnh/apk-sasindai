package com.example.sasindai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sasindai.adapter.AuthPagerAdapter;
import com.example.sasindai.theme.ThemeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthHostActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager_2;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_host);

        // import tema
        ThemeActivity.applyTheme(this);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Definisi widget
        tabLayout = findViewById(R.id.tabLayoutAuth);
        viewPager_2 = findViewById(R.id.viewPager_2);

        // Set tab layout
        tabLayout.addTab((tabLayout.newTab().setText("Masuk")));
        tabLayout.addTab((tabLayout.newTab().setText("Daftar")));

        AuthPagerAdapter authPagerAdapter = new AuthPagerAdapter(this);
        viewPager_2.setAdapter(authPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager_2,
                (tab, i) -> {
                    if (i == 0) {
                        tab.setText("Masuk");
                    } else {
                        tab.setText("Daftar");
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
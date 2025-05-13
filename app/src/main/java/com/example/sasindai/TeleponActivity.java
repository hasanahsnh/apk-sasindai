package com.example.sasindai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeleponActivity extends AppCompatActivity {
    TextView btnTerapkanTelepon, etNomorTelepon;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_telepon);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        Toolbar toolbar = findViewById(R.id.toolbarInputTelepon);
        setSupportActionBar(toolbar);

        // Inisialisasi EditText
        etNomorTelepon = findViewById(R.id.etNomorTelepon);
        btnTerapkanTelepon = findViewById(R.id.btnTerapkanTelepon);

        btnTerapkanTelepon.setOnClickListener(v -> {
            simpanNomorTelepon();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void simpanNomorTelepon() {
        String telp = etNomorTelepon.getText().toString().trim();
        if (telp.isEmpty()) {
            Toast.makeText(TeleponActivity.this, "Isi alamat terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan ke SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("telp", telp);
        editor.apply();

        Toast.makeText(TeleponActivity.this, "Nomor telepon berhasil disimpan", Toast.LENGTH_SHORT).show();

    }

}
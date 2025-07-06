package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SejarahSasiranganActivity extends AppCompatActivity {
    WebView wvSejarah;
    FloatingActionButton fabKembaliSejarah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sejarah_sasirangan);

        ThemeActivity.applyTheme(this);

        wvSejarah = findViewById(R.id.wvSejarah);
        fabKembaliSejarah = findViewById(R.id.fabKembaliSejarah);

        if (fabKembaliSejarah != null) {
            fabKembaliSejarah.setOnClickListener(v -> {
                finish();
            });
        }

        String url = getIntent().getStringExtra("sejarahUrl");
        if (url != null && !url.isEmpty()) {
            // Aktifkan JavaScript jika diperlukan
            wvSejarah.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            wvSejarah.getSettings().setJavaScriptEnabled(true);
            wvSejarah.loadUrl(url);
        } else {
            Log.e("MasukkanMotifForm", "URL tidak ditemukan di intent");
            Toast.makeText(this, "Gagal memuat form. URL tidak valid.", Toast.LENGTH_SHORT).show();
            finish(); // tutup activity jika gagal
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
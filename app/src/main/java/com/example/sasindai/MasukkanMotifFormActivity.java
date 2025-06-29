package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;

public class MasukkanMotifFormActivity extends AppCompatActivity {

    WebView webViewMasukkanMotif;
    TextView judulWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_masukkan_motif_form);

        // import tema
        ThemeActivity.applyTheme(this);

        webViewMasukkanMotif = findViewById(R.id.webViewMasukkanMotif);
        judulWebView = findViewById(R.id.judulWebView);

        // Ambil URL dari intent
        String url = getIntent().getStringExtra("url");
        String judul = getIntent().getStringExtra("judul");
        if (url != null && !url.isEmpty()) {
            // Aktifkan JavaScript jika diperlukan
            webViewMasukkanMotif.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            webViewMasukkanMotif.getSettings().setJavaScriptEnabled(true);
            webViewMasukkanMotif.loadUrl(url);
        } else {
            Log.e("MasukkanMotifForm", "URL tidak ditemukan di intent");
            Toast.makeText(this, "Gagal memuat form. URL tidak valid.", Toast.LENGTH_SHORT).show();
            finish(); // tutup activity jika gagal
        }

        if (judul != null && !judul.isEmpty()) {
            judulWebView.setText(judul);
        } else {
            Log.e("MasukkanMotifForm", "Judul tidak ditemukan di intent");
            finish(); // tutup activity jika gagal
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
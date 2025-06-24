package com.example.sasindai;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sasindai.model.RilisMediaData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

public class DetailRilisMediaActivity extends AppCompatActivity {
    RoundedImageView imgDetailFoto;
    TextView tvDetailJudul, tvDetailTanggal, tvDetailKonten;
    RilisMediaData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_rilis_media);

        // import tema
        ThemeActivity.applyTheme(this);

        imgDetailFoto = findViewById(R.id.imgDetailFoto);
        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        tvDetailTanggal = findViewById(R.id.tvDetailTanggal);
        tvDetailKonten = findViewById(R.id.tvDetailKonten);

        tampilkanDetailRilisMedia();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void tampilkanDetailRilisMedia() {
        String dataRilisMedia = getIntent().getStringExtra("data_rilis_media");
        data = new Gson().fromJson(dataRilisMedia, RilisMediaData.class);

        if (imgDetailFoto != null && data != null) {
            // Contoh: Load gambar ke ImageView (misalnya menggunakan Glide atau Picasso)
            Glide.with(this)
                    .load(data.getFotoBeritaUrl()) // ganti getFotoUrl() dengan properti yang sesuai di RilisMediaData
                    .into(imgDetailFoto);
        }

        if (tvDetailJudul != null && data != null) {
            tvDetailJudul.setText(data.getJudulArtikel());
        } else {
            assert tvDetailJudul != null;
            tvDetailJudul.setText("Judul Artikel");
        }

        if (tvDetailTanggal != null && data != null) {
            tvDetailTanggal.setText(data.getTanggalTerbit());
        } else {
            assert tvDetailTanggal != null;
            tvDetailTanggal.setText("Tanggal Terbit");
        }

        if (tvDetailKonten != null && data != null) {
            tvDetailKonten.setText(data.getKontenBerita());
        } else {
            assert tvDetailKonten != null;
            tvDetailKonten.setText("Tanggal Terbit");
        }
    }
}
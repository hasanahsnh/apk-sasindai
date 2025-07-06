package com.example.sasindai;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sasindai.model.KatalogMotifData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

public class DetailMotifActivity extends AppCompatActivity {
    TextView detailMotif, tvDetailSumber, tvDetailMotif;
    RoundedImageView imgDetailFotoMotif;
    FloatingActionButton fabKembaliKatalogMotif;
    KatalogMotifData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_motif);

        ThemeActivity.applyTheme(this);

        detailMotif = findViewById(R.id.detailMotif);
        tvDetailSumber = findViewById(R.id.tvDetailSumber);
        tvDetailMotif = findViewById(R.id.tvDetailMotif);
        imgDetailFotoMotif = findViewById(R.id.imgDetailFotoMotif);
        fabKembaliKatalogMotif = findViewById(R.id.fabKembaliKatalogMotif);

        if (fabKembaliKatalogMotif != null) {
            fabKembaliKatalogMotif.setOnClickListener(v -> {
                finish();
            });
        }

        tampilkanInformasiMotif();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void tampilkanInformasiMotif() {
        String dataMotif = getIntent().getStringExtra("detailMotif");
        data = new Gson().fromJson(dataMotif, KatalogMotifData.class);

        if (imgDetailFotoMotif != null && dataMotif != null) {
            // Contoh: Load gambar ke ImageView (misalnya menggunakan Glide atau Picasso)
            Glide.with(this)
                    .load(data.getGambarUrl()) // ganti getFotoUrl() dengan properti yang sesuai di RilisMediaData
                    .into(imgDetailFotoMotif);
        }

        if (tvDetailMotif != null && data != null) {
            tvDetailMotif.setText(data.getFilosofi());
        } else {
            assert tvDetailMotif != null;
            tvDetailMotif.setText("Nama Motif");
        }

        if (tvDetailSumber != null && data != null) {
            tvDetailSumber.setText(data.getSumberFilosofi());
        } else {
            assert tvDetailMotif != null;
            tvDetailMotif.setText("Nama Motif");
        }

        if (detailMotif != null && data != null) {
            detailMotif.setText(data.getMotif());
        } else {
            assert detailMotif != null;
            detailMotif.setText("Arti Motif");
        }
    }
}
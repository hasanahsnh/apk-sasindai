package com.example.sasindai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.KurirListAdapter;
import com.example.sasindai.model.DaftarKurirData;
import com.example.sasindai.model.VarianProduk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlamatActivity extends AppCompatActivity {
    EditText etAlamatLengkap, etKodePos;
    TextView tvOngkir, btnTerapkan, btnCariKurir, tvRincianKodePos;
    SharedPreferences sharedPreferences;
    FirebaseUser currentUser;
    RecyclerView jasaPengirimanTersedia;
    KurirListAdapter adapter;
    List<DaftarKurirData> dataKurir = new ArrayList<>();
    LinearLayout progressBarKurir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alamat);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        Toolbar toolbar = findViewById(R.id.toolbarAlamat);
        setSupportActionBar(toolbar);

        // Inisialisasi EditText
        etAlamatLengkap = findViewById(R.id.etAlamatLengkap);
        btnTerapkan = findViewById(R.id.btnTerapkan);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
        }

        btnTerapkan.setOnClickListener(v -> {
            simpanAlamat();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void simpanAlamat() {
        String alamat = etAlamatLengkap.getText().toString().trim();
        if (alamat.isEmpty()) {
            Toast.makeText(AlamatActivity.this, "Isi alamat terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan ke SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AlamatPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alamat", alamat);
        editor.apply();

        Toast.makeText(AlamatActivity.this, "Alamat berhasil disimpan", Toast.LENGTH_SHORT).show();

    }

}
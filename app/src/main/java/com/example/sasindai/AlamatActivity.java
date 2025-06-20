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
import com.example.sasindai.theme.ThemeActivity;
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
    SharedPreferences kurirPrefs, prefsKeranjang;
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

        // import tema
        ThemeActivity.applyTheme(this);

        Toolbar toolbar = findViewById(R.id.toolbarAlamat);
        setSupportActionBar(toolbar);

        // Inisialisasi EditText
        etAlamatLengkap = findViewById(R.id.etAlamatLengkap);
        btnTerapkan = findViewById(R.id.btnTerapkan);
        etKodePos = findViewById(R.id.etKodePos);
        tvOngkir = findViewById(R.id.tvOngkir);
        btnCariKurir = findViewById(R.id.btnCariKurir);
        tvRincianKodePos = findViewById(R.id.tvRincianKodePos);
        jasaPengirimanTersedia = findViewById(R.id.jasaPengirimanTersedia);
        progressBarKurir = findViewById(R.id.progressBarKurir);

        kurirPrefs = getSharedPreferences("AlamatPrefs", Context.MODE_PRIVATE);
        prefsKeranjang = getSharedPreferences("ProdukKeranjang", Context.MODE_PRIVATE);

        btnCariKurir.setOnClickListener(v -> {
            String kodePos = etKodePos.getText().toString().trim();
            if (kodePos.isEmpty()) {
                Toast.makeText(this, "Masukkan kode pos terlebih dahulu!", Toast.LENGTH_SHORT).show();
            } else if (kodePos.length() < 3) {
                Toast.makeText(this, "Masukkan minimal 3 karakter!", Toast.LENGTH_SHORT).show();
            } else if (!kodePos.matches("\\d+")) {
                Toast.makeText(this, "Masukkan input berupa angka!", Toast.LENGTH_SHORT).show();
            } else {
                getRincianKodePos(kodePos);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
        }

        btnTerapkan.setOnClickListener(v -> {
            simpanAlamat();
            simpanKurir();
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

    // Simpan kurir
    private void simpanKurir() {
        String kodePos = etKodePos.getText().toString().trim();
        if (kodePos.isEmpty()) {
            Toast.makeText(AlamatActivity.this, "Isi kode pos terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adapter == null) {
            Log.w("Alamat Activity", "Adapter belum diinisialisasikan");
            Toast.makeText(AlamatActivity.this, "Silakan tentukan ekspedisi terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        DaftarKurirData selectedKurir = adapter.getSelectedKurir();
        if (selectedKurir == null) {
            Toast.makeText(AlamatActivity.this, "Pilih salah satu kurir terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("KurirPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        // Simpan data kurir baru
        editor.putString("kurir_nama", selectedKurir.getName());
        editor.putString("kurir_service", selectedKurir.getService());
        editor.putInt("kurir_harga", selectedKurir.getPrice());
        editor.putString("kurir_etd", selectedKurir.getEtd());

        editor.apply();
        Toast.makeText(AlamatActivity.this, "Kurir berhasil disimpan", Toast.LENGTH_SHORT).show();
    }
    // End simpan kurir

    // Set recycler
    private void setupRecyclerViewKurir(List<DaftarKurirData> dataKurir) {
        jasaPengirimanTersedia.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KurirListAdapter(this, dataKurir);
        jasaPengirimanTersedia.setAdapter(adapter);
    }
    // End set

    // Proses menampilkan setiap kategori pengiriman
    private void processShippingCategory(JSONObject json, String category) throws JSONException {
        if (json.has(category)) {
            JSONArray services = json.getJSONArray(category);
            List<String> allowedCouriers = Arrays.asList("JNE", "J&T", "POS", "SICEPAT");
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                String shippingName = service.getString("shipping_name").toUpperCase(); // Ubah ke huruf besar untuk membandingkan

                for (String courier : allowedCouriers) {
                    if (shippingName.contains(courier)) {
                        int baseCost = service.getInt("shipping_cost_net");
                        int costWithTolerance = baseCost + 2500;

                        dataKurir.add(new DaftarKurirData(
                                service.getString("shipping_name"),
                                service.getString("service_name"),
                                costWithTolerance,
                                service.getString("etd").replace("day", " hari pengiriman")
                        ));
                        break;
                    }
                }
            }
        }
    }
    // proses kategori pengiriman

    // Request method
    private void getEkspedisiTersedia(String destinationId) {
        String WAREHOUSE_ID = "2421"; // Banjarmasin, Kel. Pangeran
        float weight = prefsKeranjang.getFloat("total_berat", 0f); // Hitung per kg
        int itemValue = prefsKeranjang.getInt("total_harga", 0);

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://sasindai.sascode.my.id/api/ekspedisi")
                .newBuilder()
                .addQueryParameter("shipper_destination_id", WAREHOUSE_ID)
                .addQueryParameter("receiver_destination_id", destinationId)
                .addQueryParameter("weight", String.valueOf(weight))
                .addQueryParameter("item_value", String.valueOf(itemValue))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(AlamatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("API_RESPONSE", responseBody); // Tambahkan log ini
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        dataKurir.clear();

                        if (response.isSuccessful()) {
                            progressBarKurir.setVisibility(View.GONE);
                            jasaPengirimanTersedia.setVisibility(View.VISIBLE);
                            // Proses semua kategori pengiriman

                            JSONObject data = json.getJSONObject("data");
                            processShippingCategory(data, "calculate_reguler");
                            processShippingCategory(data, "calculate_cargo");
                            processShippingCategory(data, "calculate_instant");

                            if (dataKurir.isEmpty()) {
                                Toast.makeText(AlamatActivity.this, "Tidak ada kurir tersedia", Toast.LENGTH_SHORT).show();
                            } else {
                                setupRecyclerViewKurir(dataKurir);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            String errorMsg = json.getJSONObject("meta").getString("message");
                            Toast.makeText(AlamatActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AlamatActivity.this, "Format data tidak valid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    // End request

    // Dapatkan rincian kode pos
    private void getRincianKodePos(String kodePos) {
        tvRincianKodePos.setText("Sedang mencari data...");
        progressBarKurir.setVisibility(View.VISIBLE);
        jasaPengirimanTersedia.setVisibility(View.GONE);

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://sasindai.sascode.my.id/api/rincian-kodepos")
                .newBuilder()
                .addQueryParameter("keyword", kodePos)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    tvRincianKodePos.setText("Gagal memuat data");
                    Toast.makeText(AlamatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ResponseBody", responseBody);
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray data = json.getJSONArray("data");
                        if (data.length() > 0) {
                            // Ambil data pertama
                            JSONObject firstResult = data.getJSONObject(0);

                            String subdistrict = firstResult.getString("subdistrict_name");
                            String district = firstResult.getString("district_name");
                            String city = firstResult.getString("city_name");
                            String province = firstResult.getString("province_name");
                            String destinationId = firstResult.getString("id");

                            Log.d("Get ID destination", "ID destination:" + destinationId);
                            getEkspedisiTersedia(destinationId);

                            SharedPreferences prefs = getSharedPreferences("AlamatPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("subdistrict", subdistrict);
                            editor.putString("district", district);
                            editor.putString("city", city);
                            editor.putString("province", province);
                            editor.putString("destination_id", destinationId);
                            editor.putString("kode_pos", kodePos);
                            editor.apply();

                            String resultText = String.format(
                                    "Kecamatan: %s\nKelurahan: %s\nKota/Kab: %s\nProvinsi: %s",
                                    subdistrict,
                                    district,
                                    city,
                                    province
                            );

                            tvRincianKodePos.setText(resultText);
                        } else {
                            tvRincianKodePos.setText("Data tidak ditemukan");
                        }
                    } catch (JSONException e) {
                        tvRincianKodePos.setText("Format data tidak valid");
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    // End rincian kode pos

}
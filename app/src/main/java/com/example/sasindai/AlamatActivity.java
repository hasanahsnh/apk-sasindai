package com.example.sasindai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.KurirListAdapter;
import com.example.sasindai.model.DaftarKurirData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    TextView btnTerapkan, tvRincianKodePos;
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
        btnTerapkan = findViewById(R.id.btnTerapkan);
        jasaPengirimanTersedia = findViewById(R.id.jasaPengirimanTersedia);
        progressBarKurir = findViewById(R.id.progressBarKurir);

        SharedPreferences prefsKurir = getSharedPreferences("KurirPrefs", MODE_PRIVATE);
        String kodePos = prefsKurir.getString("kode_pos", "");
        prefsKeranjang = getSharedPreferences("ProdukKeranjang", Context.MODE_PRIVATE);
        
        if (!kodePos.isEmpty()) {
            loadDestinationIdAndFetchCouriers(kodePos);
        } else {
            Toast.makeText(this, "Kode pos tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        btnTerapkan.setOnClickListener(v -> {
            simpanKurir();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadDestinationIdAndFetchCouriers(String kodePos) {
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
                runOnUiThread(() -> Toast.makeText(AlamatActivity.this, "Gagal memuat tujuan: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray data = json.getJSONArray("data");

                        if (data.length() > 0) {
                            String destinationId = data.getJSONObject(0).getString("id");

                            // Simpan ke KurirPrefs
                            SharedPreferences.Editor editor = getSharedPreferences("KurirPrefs", MODE_PRIVATE).edit();
                            editor.putString("destination_id", destinationId);
                            editor.apply();

                            // Langsung muat ekspedisi
                            getEkspedisiTersedia(destinationId);
                        } else {
                            Toast.makeText(AlamatActivity.this, "Tujuan tidak ditemukan untuk kode pos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AlamatActivity.this, "Data tidak valid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Simpan kurir
    private void simpanKurir() {
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

}
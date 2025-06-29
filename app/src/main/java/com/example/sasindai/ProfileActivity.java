package com.example.sasindai;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {
    TextView aturEmail, aturNama, aturTelepon, aturAlamat, rincianInputKodePos, simpanAlamat, cariDetailKodepos;
    FirebaseUser currentUser;
    String uid;
    ProgressBar progressBar;
    LinearLayout container;
    EditText inputAlamat, inputKodePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        aturEmail = findViewById(R.id.aturEmail);
        aturNama = findViewById(R.id.aturNama);
        aturTelepon = findViewById(R.id.aturTelepon);
        progressBar = findViewById(R.id.progressBarProfile);
        container = findViewById(R.id.containerProfile);
        aturAlamat = findViewById(R.id.aturAlamat);

        // dapatkan data profie
        loadProfile();

        // Perbarui data profile
        updateProfile();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateProfile() {

        // Update telepon
        aturTelepon.setOnClickListener(v -> {
            tampilkanDialogTelepon();
        });

        // atur alamat
        aturAlamat.setOnClickListener(v -> {
            tampilkanDialogAlamat();
        });
    }

    private void tampilkanDialogTelepon() {
        //
    }

    private void tampilkanDialogAlamat() {
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.bottom_sheet_isi_alamat);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        inputAlamat = dialog.findViewById(R.id.inputAlamat);
        inputKodePos = dialog.findViewById(R.id.inputKodePos);
        rincianInputKodePos = dialog.findViewById(R.id.rincianInputKodePos);
        simpanAlamat = dialog.findViewById(R.id.simpanAlamat);
        cariDetailKodepos = dialog.findViewById(R.id.cariDetailKodepos);

        cariDetailKodepos.setOnClickListener(v1 -> {
            inputKodePos.getText().toString().trim();
            String kodePos = inputKodePos.getText().toString().trim();
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

        if (simpanAlamat != null) {
            simpanAlamat.setOnClickListener(v -> {
                inputAlamatKosong();
                String alamat = inputAlamat.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();
                String kodePos = inputKodePos.getText().toString().trim();
                String rincian = rincianInputKodePos.getText().toString();
                if (rincian.contains("Kecamatan:") && rincian.contains("Kelurahan:")) {
                    String[] lines = rincian.split("\n");
                    String kecamatan = lines[0].replace("Kecamatan: ", "").trim();
                    String kelurahan = lines[1].replace("Kelurahan: ", "").trim();
                    String kota = lines[2].replace("Kota/Kab: ", "").trim();
                    String provinsi = lines[3].replace("Provinsi: ", "").trim();

                    DatabaseReference alamatRef = FirebaseDatabase.getInstance()
                            .getReference("pembeli")
                            .child(uid)
                            .child("alamat");

                    // simpan sementara
                    HashMap<String, Object> alamatData = new HashMap<>();
                    alamatData.put("alamat", alamat);
                    alamatData.put("kodePos", kodePos);
                    alamatData.put("kelurahan", kelurahan);
                    alamatData.put("kecamatan", kecamatan);
                    alamatData.put("kota", kota);
                    alamatData.put("provinsi", provinsi);

                    alamatRef.setValue(alamatData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Alamat berhasil disimpan", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Gagal menyimpan alamat", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Data rincian belum lengkap. Coba cari kode pos lagi.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int halfHeight = displayMetrics.heightPixels / 2;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfHeight);
            window.setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }

    private void getRincianKodePos(String kodePos) {
        rincianInputKodePos.setText("Sedang mencari data...");

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
                    rincianInputKodePos.setText("Gagal memuat data");
                    Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                            String resultText = String.format(
                                    "Kecamatan: %s\nKelurahan: %s\nKota/Kab: %s\nProvinsi: %s",
                                    subdistrict,
                                    district,
                                    city,
                                    province
                            );

                            rincianInputKodePos.setText(resultText);
                        } else {
                            rincianInputKodePos.setText("Data tidak ditemukan");
                        }
                    } catch (JSONException e) {
                        rincianInputKodePos.setText("Format data tidak valid");
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void inputAlamatKosong() {
        if (inputAlamat.getText().toString().isEmpty()) {
            Toast.makeText(this, "Isi alamatmu terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputKodePos.getText().toString().isEmpty()) {
            Toast.makeText(this, "Isi kodepos terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void profile() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    String namaLengkap = snapshot.child("namaLengkap").getValue(String.class);
                    String noTelp = snapshot.child("noTelp").getValue(String.class);

                    boolean isEmailValid = email != null && !email.trim().isEmpty();
                    boolean isNamaValid = namaLengkap != null && !namaLengkap.trim().isEmpty();
                    boolean isTelpValid = noTelp != null && !noTelp.trim().isEmpty();

                    if (isEmailValid) {
                        aturEmail.setText(email);
                    }

                    if (isNamaValid) {
                        aturNama.setText(namaLengkap);
                    }

                    if (isTelpValid) {
                        aturTelepon.setText(noTelp);
                    }

                    progressBar.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Profile Actv", "Gagal mengambil data: " + error.getMessage());
                }
            });
        }
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        profile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
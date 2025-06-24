package com.example.sasindai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.DetailPemesananAdapter;
import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailPemesananActivity extends AppCompatActivity {
    DetailPemesananAdapter adapter;
    RecyclerView recyclerSelectedItems;
    TextView btnMasukkanAlamat, tvNamaPembeli, tvTelpPembeli, tvTotalHarga,
            tvRincianProduk, rincianHargaKurir, jasaPengiriman, btnCheckoutOrder;
    SharedPreferences sharedPreferences, alamatPrefs, keranjangPrefs, kurirPrefs, telpPrefs;
    FirebaseUser currentUser;
    LinearLayout progressBarDetailPemesanan;
    NestedScrollView containerDetailPemesanan;
    String uid;
    String namaLengkap = "Nama lengkap tidak ditemukan";
    String noTelp = "No. Telepon tidak ditemukan";
    boolean isTelpValid = false;
    RadioGroup radioGroup;
    RadioButton radioQris, radioShopee;
    int selectedId;
    String[] selectedPayment = {"shopeepay"};
    String orderId;
    private boolean isCheckingStatus = false;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_pemesanan);

        // import tema
        ThemeActivity.applyTheme(this);

        // Inisialisasi widget
        recyclerSelectedItems = findViewById(R.id.recyclerSelectedItems);
        btnMasukkanAlamat = findViewById(R.id.btnMasukkanAlamat);
        tvNamaPembeli = findViewById(R.id.tvNamaPembeli);
        tvTelpPembeli = findViewById(R.id.tvTelpPembeli);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        tvRincianProduk = findViewById(R.id.tvRincianProduk);
        rincianHargaKurir = findViewById(R.id.rincianHargaKurir);
        progressBarDetailPemesanan = findViewById(R.id.progressBarDetailPemesanan);
        containerDetailPemesanan = findViewById(R.id.containerDetailPemesanan);
        jasaPengiriman = findViewById(R.id.btnPilihJasaPengiriman);
        btnCheckoutOrder = findViewById(R.id.btnCheckoutOrder);
        radioGroup = findViewById(R.id.radioGroupMetode);
        radioShopee = findViewById(R.id.radioShopeePay);
        radioQris = findViewById(R.id.radioQris);
        // End inisial

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String status = data.getStringExtra("resultStatus");
                            String statusMessage = data.getStringExtra("resultMessage");
                            String transactionId = data.getStringExtra("transactionId");
                            Log.d("MIDTRANS", "Status: " + status);
                            Log.d("MIDTRANS", "Message: " + statusMessage);
                            Log.d("MIDTRANS", "Transaction ID: " + transactionId);

                            // TODO: Tangani sesuai status transaksi, misal update UI, simpan data dll
                            Toast.makeText(this, "Pembayaran: " + statusMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d("MIDTRANS", "Payment canceled or failed");
                        Toast.makeText(this, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        radioQris.setChecked(true);
        String[] selectedPayment = { "qris" };

        radioShopee.setOnClickListener(v -> {
            radioShopee.setChecked(true);
            radioQris.setChecked(false);
            selectedPayment[0] = "shopeepay";
        });

        radioQris.setOnClickListener(v -> {
            radioQris.setChecked(true);
            radioShopee.setChecked(false);
            selectedPayment[0] = "qris";
        });

        // get prefs
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        alamatPrefs = getSharedPreferences("AlamatPrefs", MODE_PRIVATE);
        keranjangPrefs = getSharedPreferences("ProdukKeranjang", Context.MODE_PRIVATE);
        kurirPrefs = getSharedPreferences("KurirPrefs", Context.MODE_PRIVATE);
        telpPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // End get prefs

        // inisial tabel DB untuk atur stok (jika user checkout melalui keranjang
        DatabaseReference dbKeranjang = FirebaseDatabase.getInstance().getReference();
        // End inisial tabel

        loadUserData();

        lengkapiData();

        // Cek status transaksi untuk update tampilan keranjang:

        // Click event btn checkout, penambahan validasi kelengkapan input
        btnCheckoutOrder.setOnClickListener(v -> {
            if (checkOutOrder()) {
                cplCheckout();
            }
        });
        // end event

        // implementasi ke recycler dari selectedIems (produk yang ingin dibeli)
        String json = getIntent().getStringExtra("selectedItems");
        // Buat array data produk baru
        List<KeranjangData> selectedItems = new ArrayList<>();

        // validasi nilai json yang dikirim/diambil dari "selectedItems"
        if (json != null) {
            /* Data produk dikirim dari:
            - DetailProdukActivity (bottomSheetBeliSekarang()),
            - KeranjangActivity (btnCheckout.setOnClickListener(v -> {})
            */
            Type type = new TypeToken<List<KeranjangData>>(){}.getType();
            selectedItems = new Gson().fromJson(json, type);
        } else {
            Toast.makeText(this, "Data produk kosong", Toast.LENGTH_SHORT).show();
        }
        // End validasi

        // Set adapter untuk menampilkan data produk (selectedItems)
        recyclerSelectedItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailPemesananAdapter(this, selectedItems);
        recyclerSelectedItems.setAdapter(adapter);
        // End set

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void lengkapiData() {
        // Click btn masukkan alamat
        btnMasukkanAlamat.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPemesananActivity.this, AlamatActivity.class);
            startActivity(intent);
        });
        // end proses

        // Click btn masukkan telepon
        tvTelpPembeli.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPemesananActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        // end proses
    }

    private void loadUserData() {
        progressBarDetailPemesanan.setVisibility(View.VISIBLE);
        containerDetailPemesanan.setVisibility(View.GONE);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String namaLengkap = snapshot.child("namaLengkap").getValue(String.class);
                    noTelp = snapshot.child("noTelp").getValue(String.class);

                    boolean isNamaValid = namaLengkap != null && !namaLengkap.trim().isEmpty();
                    isTelpValid = noTelp != null && !noTelp.trim().isEmpty();

                    if (isNamaValid) {
                        tvNamaPembeli.setText(namaLengkap);
                    } else {
                        tvNamaPembeli.setText("Nama tidak ditemukan!");
                    }

                    if (isTelpValid) {
                        tvTelpPembeli.setText(noTelp);
                    } else {
                        tvTelpPembeli.setText("No telepon belum diisi!, klik untuk mengisi");
                    }

                    totalRincian();

                    progressBarDetailPemesanan.setVisibility(View.GONE);
                    containerDetailPemesanan.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Gagal mengambil data: " + error.getMessage());
                    tvNamaPembeli.setText("Gagal memuat nama");
                    tvTelpPembeli.setText("Gagal memuat nomor telepon");
                }
            });
        } else {
            tvNamaPembeli.setText("User belum login");
        }
    }

    private boolean checkOutOrder() {
        boolean isValid = true;

        // Cek nilai alamat dan sub district
        String alamat = alamatPrefs.getString("alamat", null);
        String subdistrict = alamatPrefs.getString("subdistrict", null);
        String district = alamatPrefs.getString("district", null);
        String city = alamatPrefs.getString("city", null);
        String province = alamatPrefs.getString("province", null);
        String kodePos = alamatPrefs.getString("kode_pos", null);

        if (isEmptyOrNull(alamat) || isEmptyOrNull(subdistrict) || isEmptyOrNull(district)
                || isEmptyOrNull(city) || isEmptyOrNull(province) || isEmptyOrNull(kodePos)) {
            isValid = false;
            Toast.makeText(this, "Silakan lengkapi semua data alamat terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }

        // Cek nilai telepon
        String nomorTelp = tvTelpPembeli.getText().toString().trim();

        if (nomorTelp.isEmpty() || nomorTelp.contains("belum diisi")) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Silakan masukkan alamat terlebih dahulu!", Toast.LENGTH_SHORT).show()
            );
            isValid = false;
        }

        // Cek nilai kurir
        String kurirNama = kurirPrefs.getString("kurir_nama", "");
        String kurirService = kurirPrefs.getString("kurir_service", "");

        if (kurirNama.isEmpty() || kurirService.isEmpty()) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Silakan tentukan kurir terlebih dahulu!", Toast.LENGTH_SHORT).show()
            );
            isValid = false;
        }

        if (selectedPayment[0] == null || selectedPayment[0].isEmpty()) {
            Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    /*private void lanjutkanPembayaran() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.getIdToken(true).addOnSuccessListener(result -> {
            String idToken = result.getToken();
            Log.d("AUTH", "ID Token: " + idToken);

            int totalHarga = keranjangPrefs.getInt("total_harga", 0);
            int ongkir = kurirPrefs.getInt("kurir_harga", 0);
            int totalBayar = totalHarga + ongkir;

            String alamat = alamatPrefs.getString("alamat", "");
            String kelurahan = alamatPrefs.getString("subdistrict", "Kelurahan tidak ditemukan");
            String kecamatan = alamatPrefs.getString("district", "Kecamatan tidak ditemukan");
            String kota = alamatPrefs.getString("city", "Kota tidak ditemukan");
            String provinsi = alamatPrefs.getString("province", "Provinsi tidak ditemukan");
            String kodePos = alamatPrefs.getString("kode_pos", "Kodepos tidak ditemukan");

            String alamatLengkap = alamat + ", "
                    + kelurahan + ", "
                    + kecamatan + ", "
                    + kota + ", "
                    + provinsi + ", "
                    + kodePos;

            Log.d("CHECKOUT", "Harga Produk: " + totalHarga);
            Log.d("CHECKOUT", "Ongkir: " + ongkir);
            Log.d("CHECKOUT", "Total Bayar: " + totalBayar);

            JSONObject json = new JSONObject();
            try {
                json.put("total", totalBayar);
                json.put("ongkir", ongkir);
                json.put("hargaProduk", totalHarga);
                json.put("alamat", alamatLengkap);
                json.put("kurir", kurirPrefs.getString("kurir_nama", ""));
                json.put("layanan", kurirPrefs.getString("kurir_service", ""));
                JSONArray produkArray = getProdukArray();
                json.put("produkDipesan", produkArray);
                json.put("metodePembayaran", selectedPayment);
                json.put("uid", currentUser.getUid());

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membuat data checkout", Toast.LENGTH_SHORT).show();
                return;
            }

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://sasindai.sascode.my.id/api/checkout")
                    .addHeader("Authorization", "Bearer " + idToken)
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();

            new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject responseJson = new JSONObject(responseBody);
                        String snapToken = responseJson.getString("snap_token");
                        String orderId = responseJson.getString("order_id");

                        runOnUiThread(() -> {
                            Log.d("CHECKOUT", "Order ID: " + orderId);
                            Log.d("CHECKOUT", "Snap token: " + snapToken);

                            UiKitApi uiKitApi = new UiKitApi.Builder()
                                    .withMerchantClientKey("SB-Mid-client-LtKp1sBGtb5Kkf8i")
                                    .withContext(this)
                                    .withMerchantUrl("https://sasindai.sascode.my.id/api/checkout/")
                                    .enableLog(true)
                                    .build();

                            setLocaleNew("id");

                            // Mulai proses pembayaran Midtrans Snap dengan launcher
                            uiKitApi.startPaymentUiFlow(this, launcher, snapToken);
                        });
                    } else {
                        Log.e("CHECKOUT", "Gagal Checkout: " + response.code());
                        runOnUiThread(() ->
                                Toast.makeText(this, "Checkout gagal: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }).start();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mendapatkan token autentikasi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }*/

    private void cplCheckout() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String uid = currentUser.getUid();

        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.getIdToken(true).addOnSuccessListener(result -> {
            String idToken = result.getToken();
            Log.d("AUTH", "ID Token: " + idToken);

            int totalHarga = keranjangPrefs.getInt("total_harga", 0);
            int ongkir = kurirPrefs.getInt("kurir_harga", 0);
            int totalBayar = totalHarga + ongkir;

            String alamat = alamatPrefs.getString("alamat", "");
            String kelurahan = alamatPrefs.getString("subdistrict", "Kelurahan tidak ditemukan");
            String kecamatan = alamatPrefs.getString("district", "Kecamatan tidak ditemukan");
            String kota = alamatPrefs.getString("city", "Kota tidak ditemukan");
            String provinsi = alamatPrefs.getString("province", "Provinsi tidak ditemukan");
            String kodePos = alamatPrefs.getString("kode_pos", "Kodepos tidak ditemukan");

            String alamatLengkap = alamat + ", "
                    + kelurahan + ", "
                    + kecamatan + ", "
                    + kota + ", "
                    + provinsi + ", "
                    + kodePos;

            String jsonKeranjang = getIntent().getStringExtra("selectedItems");
            assert jsonKeranjang != null;
            Log.d("JSON_KERANJANG_RAW", jsonKeranjang);
            String uidPenjual = null;

            Type type = new TypeToken<List<KeranjangData>>() {
            }.getType();
            List<KeranjangData> selectedItems = new Gson().fromJson(jsonKeranjang, type);

            if (!selectedItems.isEmpty()) {
                uidPenjual = selectedItems.get(0).getUidPenjual();
            }

            Log.d("CHECKOUT", "Harga Produk: " + totalHarga);
            Log.d("CHECKOUT", "Ongkir: " + ongkir);
            Log.d("CHECKOUT", "Total Bayar: " + totalBayar);

            String tipeCheckoutIntent = getIntent().getStringExtra("tipe_checkout");
            SharedPreferences prefss = getSharedPreferences("checkout_data", MODE_PRIVATE);
            String tipeCheckout = tipeCheckoutIntent != null ? tipeCheckoutIntent : prefss.getString("tipe_checkout", "");

            JSONObject json = new JSONObject();
            try {
                json.put("total", totalBayar);
                json.put("ongkir", ongkir);
                json.put("harga_produk", totalHarga);
                json.put("alamat", alamatLengkap);
                json.put("kurir", kurirPrefs.getString("kurir_nama", ""));
                json.put("layanan", kurirPrefs.getString("kurir_service", ""));
                JSONObject produkArray = getProdukArray();
                json.put("produk_dipesan", produkArray);
                json.put("metode_pembayaran", selectedPayment[0]);
                json.put("uid", uid);
                json.put("uidPenjual", uidPenjual);
                json.put("statusPesanan", "menunggu pembayaran");
                json.put("tipe_checkout", tipeCheckout);

                Log.i("JSON Body", "Data " + json);

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            // Bersihkan `tipe_checkout` dari SharedPreferences setelah dipakai
            SharedPreferences.Editor editor = prefss.edit();
            editor.remove("tipe_checkout");
            editor.apply();

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://sasindai.sascode.my.id/api/cpl_checkout")
                    .addHeader("Authorization", "Bearer " + idToken)
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();

            new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject responseJson = new JSONObject(responseBody);
                        String paymentUrl = responseJson.getString("payment_url");
                        orderId = responseJson.getString("order_id");

                        runOnUiThread(() -> {
                            Log.d("CHECKOUT", "Order ID: " + orderId);
                            Log.d("CHECKOUT", "Payment URL: " + paymentUrl);

                            // Buka payment URL di WebView activity (jika kamu buat) atau browser
                            Intent intent = new Intent(this, PaymentActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("payment_url", paymentUrl);
                            intent.putExtra("order_id", orderId);

                            SharedPreferences prefs = getSharedPreferences("checkout_data", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("tipe_checkout", getIntent().getStringExtra("tipe_checkout"))
                                    .putString("selectedItems", getIntent().getStringExtra("selectedItems"))
                                    .apply();

                            startActivity(intent);
                        });
                    } else {
                        Log.e("CHECKOUT", "Gagal Checkout: " + response.code());
                        runOnUiThread(() ->
                                Toast.makeText(this, "Checkout gagal: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }).start();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mendapatkan token autentikasi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    @NonNull
    private JSONObject getProdukArray() throws JSONException {
        String json = getIntent().getStringExtra("selectedItems");
        JSONObject produkObject = new JSONObject();

        if (json != null) {
            Type type = new TypeToken<List<KeranjangData>>(){}.getType();
            List<KeranjangData> selectedItems = new Gson().fromJson(json, type);

            for (KeranjangData item : selectedItems) {
                JSONObject produkJson = new JSONObject();
                produkJson.put("idProduk", item.getIdProduk());
                produkJson.put("namaProduk", item.getNamaProduk());
                produkJson.put("idVarian", item.getIdVarian());
                produkJson.put("namaVarian", item.getNamaVarian());
                produkJson.put("harga", item.getHarga());
                produkJson.put("qty", item.getQty());
                produkJson.put("varianUrl", item.getGambarVarian());
                produkObject.put(item.getIdVarian(), produkJson);
            }
        }
        return produkObject;
    }

    private void totalRincian() {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Rincian harga produk
        int rincianHargaBarang = keranjangPrefs.getInt("total_harga", 0);
        tvRincianProduk.setText(formatRupiah.format(rincianHargaBarang).replace("Rp", "Rp "));

        // Rincian ekspedisi
        int hargaEkspedisi = kurirPrefs.getInt("kurir_harga", 0);
        rincianHargaKurir.setText(formatRupiah.format(hargaEkspedisi).replace("Rp", "Rp "));

        // Nama layanan
        String namaKurir = kurirPrefs.getString("kurir_nama", "");
        String serviceKurir = kurirPrefs.getString("kurir_service", "");

        if (namaKurir.isEmpty() && serviceKurir.isEmpty()) {
            jasaPengiriman.setText("Kurir");
        } else {
            String namaLayananLengkap = namaKurir + " - " + serviceKurir;
            jasaPengiriman.setText(namaLayananLengkap);
        }

        // Rincian alamat
        String alamat = alamatPrefs.getString("alamat", "");
        String subdistrict = alamatPrefs.getString("subdistrict", "");
        String district = alamatPrefs.getString("district", "");
        String city = alamatPrefs.getString("city", "");
        String province = alamatPrefs.getString("province", "");
        String kodePos = alamatPrefs.getString("kode_pos", "");

        if (alamat.isEmpty() && subdistrict.isEmpty() && district.isEmpty()
                && city.isEmpty() && province.isEmpty() && kodePos.isEmpty()) {
            btnMasukkanAlamat.setText("Masukkan alamat");
        } else {
            String alamatLengkap = alamat + ", " + subdistrict + ", " + district + ", "
                    + city + ", " + province + ", " + kodePos;
            btnMasukkanAlamat.setText(alamatLengkap);
        }

        if (isTelpValid) {
            tvTelpPembeli.setText(noTelp);
        } else {
            tvTelpPembeli.setText("No telepon belum diisi!, klik untuk mengisi");
        }

        // Total rincian
        tvTotalHarga.setText(formatRupiah.format(rincianHargaBarang + hargaEkspedisi).replace("Rp", "Rp "));

    }

    private void setLocaleNew(String languageCode) {
        LocaleListCompat locales = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(locales);
    }

    private void tampilkanProgressBarDanLoadRincian() {
        progressBarDetailPemesanan.setVisibility(View.VISIBLE);
        containerDetailPemesanan.setVisibility(View.GONE);

        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanProgressBarDanLoadRincian();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tampilkanProgressBarDanLoadRincian();
    }
}
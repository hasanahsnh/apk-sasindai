package com.example.sasindai;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.DaftarItemRiwayatPesananAdapter;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrderItemWrapper;
import com.example.sasindai.model.OrdersData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.theme.ThemeActivity;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailPesananActivity extends AppCompatActivity {
    TextView detailIdPesanan, detailTelp, detailAlamat, detailJasaPengiriman,
            detailChannelPayment, detailTotalHargaProduk, detailHargaOngkir;
    OrdersData ordersData;
    RecyclerView itemPesanan;
    DaftarItemRiwayatPesananAdapter adapter;
    List<OrderItemWrapper> orderItemWrappers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_pesanan);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        detailIdPesanan = findViewById(R.id.detailIdPesanan);
        detailTelp = findViewById(R.id.detailTelp);
        detailAlamat = findViewById(R.id.detailAlamat);
        detailJasaPengiriman = findViewById(R.id.detailJasaPengiriman);
        detailChannelPayment = findViewById(R.id.detailChannelPayment);
        detailTotalHargaProduk = findViewById(R.id.detailTotalHargaProduk);
        detailHargaOngkir = findViewById(R.id.detailHargaOngkir);
        itemPesanan = findViewById(R.id.itemPesanan);

        itemPesanan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DaftarItemRiwayatPesananAdapter(this, orderItemWrappers);
        itemPesanan.setAdapter(adapter);

        detailPesanan();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void detailPesanan() {
        String produkJson = getIntent().getStringExtra("detailPesanan");
        ordersData = new Gson().fromJson(produkJson, OrdersData.class);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        if (ordersData != null && ordersData.getProduk() != null) {
            orderItemWrappers.clear();

            for (ItemProdukOrderData item : ordersData.getProduk()) {
                orderItemWrappers.add(new OrderItemWrapper(item, ordersData));
            }

            adapter.notifyDataSetChanged();
        }

        if (ordersData.getOrder_id() != null && detailIdPesanan != null) {
            detailIdPesanan.setText(ordersData.getOrder_id());
        } else {
            assert detailIdPesanan != null;
            detailIdPesanan.setText("-");
        }

        if (ordersData.getNo_telp() != null && detailTelp != null) {
            detailTelp.setText(ordersData.getNo_telp());
        } else {
            assert detailTelp != null;
            detailTelp.setText("-");
        }

        if (ordersData.getAlamat() != null && detailAlamat != null) {
            detailAlamat.setText(ordersData.getAlamat());
        } else {
            assert detailAlamat != null;
            detailAlamat.setText("-");
        }

        if (ordersData.getKurir() != null && detailJasaPengiriman != null) {
            detailJasaPengiriman.setText(ordersData.getKurir());
        } else {
            assert detailJasaPengiriman != null;
            detailJasaPengiriman.setText("-");
        }

        if (ordersData.getMetode_pembayaran() != null && detailChannelPayment != null) {
            detailChannelPayment.setText(ordersData.getMetode_pembayaran());
        } else {
            assert detailChannelPayment != null;
            detailChannelPayment.setText("-");
        }

        if (ordersData != null && ordersData.getProduk() != null) {
            int totalHarga = 0;

            for (ItemProdukOrderData item : ordersData.getProduk()) {
                int harga = item.getHarga();
                int jumlah = item.getQty();
                totalHarga += harga * jumlah;
            }

            if (detailTotalHargaProduk != null) {
                detailTotalHargaProduk.setText(formatter.format(totalHarga).replace("Rp", "Rp "));
            } else {
                assert false;
                detailTotalHargaProduk.setText("-");
            }
        }

        if (ordersData != null) {
            int biayaOngkir = ordersData.getBiayaOngkir();;

            if (detailHargaOngkir != null) {
                detailHargaOngkir.setText(formatter.format(biayaOngkir).replace("Rp", "Rp "));
            } else {
                assert false;
                detailHargaOngkir.setText("-");
            }
        }

    }
}
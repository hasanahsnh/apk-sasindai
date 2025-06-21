package com.example.sasindai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Type;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    WebView webView;
    String uid;
    String tipeCheckout, jsonSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        webView = findViewById(R.id.webviewPayment);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        }

        SharedPreferences prefs = getSharedPreferences("checkout_data", MODE_PRIVATE);

        tipeCheckout = getIntent().getStringExtra("tipe_checkout");
        if (tipeCheckout == null) {
            tipeCheckout = prefs.getString("tipe_checkout", null);
        }

        jsonSelectedItems = getIntent().getStringExtra("selectedItems");
        if (jsonSelectedItems == null) {
            jsonSelectedItems = prefs.getString("selectedItems", null);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainHostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
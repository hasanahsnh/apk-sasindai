package com.example.sasindai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;

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

        String paymentUrl = getIntent().getStringExtra("payment_url");
        Log.d("PAYMENT_URL", "URL: " + paymentUrl);

        if (paymentUrl != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "URL pembayaran tidak tersedia", Toast.LENGTH_SHORT).show();
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
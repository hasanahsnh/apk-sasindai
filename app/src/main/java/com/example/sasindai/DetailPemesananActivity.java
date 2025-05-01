package com.example.sasindai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.DetailPemesananAdapter;
import com.example.sasindai.model.KeranjangData;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DetailPemesananActivity extends AppCompatActivity {
    DetailPemesananAdapter adapter;
    RecyclerView recyclerSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_pemesanan);

        recyclerSelectedItems = findViewById(R.id.recyclerSelectedItems);

        String json = getIntent().getStringExtra("selectedItems");
        Type type = new TypeToken<List<KeranjangData>>(){}.getType();
        List<KeranjangData> selectedItems = new Gson().fromJson(json, type);

        recyclerSelectedItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailPemesananAdapter(this, selectedItems);
        recyclerSelectedItems.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
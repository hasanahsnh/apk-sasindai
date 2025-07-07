package com.example.sasindai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.UlasanData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UlasanListAdapter extends RecyclerView.Adapter<UlasanListAdapter.UlasanListAdapterViewHolder> {
    Context context;
    List<UlasanData> item;
    Map<String, String> namaUserCache = new HashMap<>();
    String uidPembeli, komentar;

    public UlasanListAdapter(Context context, List<UlasanData> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public UlasanListAdapter.UlasanListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_ulasan, parent, false);
        return new UlasanListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UlasanListAdapter.UlasanListAdapterViewHolder holder, int position) {
        UlasanData dataUlasan = item.get(position);

        if (dataUlasan != null && dataUlasan.getItemProduk() != null) {
            // Ambil data produk -> varian dari ItemProdukOrderData di UlasanData
            ItemProdukOrderData produk = dataUlasan.getItemProduk();
            holder.tvVarianDaftarUlasan.setText(produk.getNamaVarian());

            // ambil komentar langsung dari UlasanData
            komentar = dataUlasan.getKomentar();
            holder.tvKomentar.setText(dataUlasan.getKomentar().isEmpty() ? "(Tidak ada komentar)" : "\"" + komentar + "\"");

            /* buat cache menghindari query berulang untuk mengambil
            data nama pembeli dari tabel users
             */
            uidPembeli = dataUlasan.getUidPembeli();
            if (namaUserCache.containsKey(uidPembeli)) {
                holder.tvNamaLengkapDaftarUlasan.setText(namaUserCache.get(uidPembeli));
            } else {
                holder.tvNamaLengkapDaftarUlasan.setText("Memuat...");
                FirebaseDatabase.getInstance().getReference("users")
                        .orderByChild("uid")
                        .equalTo(uidPembeli)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                                        String nama = userSnap.child("namaLengkap").getValue(String.class);
                                        if (nama != null) {
                                            namaUserCache.put(uidPembeli, nama);
                                            holder.tvNamaLengkapDaftarUlasan.setText(nama);
                                            return;
                                        }
                                    }
                                }
                                holder.tvNamaLengkapDaftarUlasan.setText("(Tidak ditemukan)");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                holder.tvNamaLengkapDaftarUlasan.setText("(Error)");
                            }
                        });

            }
        }
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class UlasanListAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvVarianDaftarUlasan, tvNamaLengkapDaftarUlasan, tvKomentar;
        public UlasanListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVarianDaftarUlasan = itemView.findViewById(R.id.tvVarianDaftarUlasan);
            tvNamaLengkapDaftarUlasan = itemView.findViewById(R.id.tvNamaLengkapDaftarUlasan);
            tvKomentar = itemView.findViewById(R.id.tvKomentar);
        }
    }
}

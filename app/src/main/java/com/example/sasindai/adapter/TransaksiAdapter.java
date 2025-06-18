package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrdersData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.DetailPesananActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {
    private final Context context;
    private final List<OrdersData> ordersData;
    int totalPesanan = 0;

    public TransaksiAdapter(Context context, List<OrdersData> ordersData) {
        this.context = context;
        this.ordersData = ordersData;
    }

    @NonNull
    @Override
    public TransaksiAdapter.TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_riwayat_transaksi, parent, false);
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiAdapter.TransaksiViewHolder holder, int position) {
        OrdersData data = ordersData.get(position);

        String status = data.getStatusPesanan();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        totalPesanan = data.getProduk() != null ? data.getProduk().size() : 0;

        // Cek apakah list produk tidak kosong
        if (data.getProduk() != null && !data.getProduk().isEmpty()) {
            ItemProdukOrderData item = data.getProduk().get(0); // hanya produk ke-0

            holder.tvNamaProdukRiwayat.setText(item.getNamaVarian());
            holder.tvQtyRiwayat.setText(String.valueOf(item.getQty()));
            holder.tvHargaProdukRiwayat.setText(formatter.format(item.getHarga()).replace("Rp", "Rp "));
        } else {
            holder.tvNamaProdukRiwayat.setText("-");
            holder.tvQtyRiwayat.setText("-");
            holder.tvHargaProdukRiwayat.setText("-");
        }

        holder.statusPesanan.setText(status);
        holder.idPesanan.setText(data.getOrder_id());
        holder.countTotalPesanan.setText(String.valueOf(totalPesanan));
        holder.totalhargaPesanan.setText(formatter.format(data.getTotal()).replace("Rp", "Rp "));

        // Ambil gambar dari ProdukData berdasarkan id_produk dari item pertama
        if (!data.getProduk().isEmpty()) {
            String idProduk = data.getProduk().get(0).getIdProduk();

            DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference("produk").child(idProduk);
            produkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProdukData produk = snapshot.getValue(ProdukData.class);
                    if (produk != null && produk.getUrlFotoProduk() != null && !produk.getUrlFotoProduk().isEmpty()) {
                        String urlFoto = produk.getUrlFotoProduk().get(0);

                        Glide.with(holder.itemView.getContext())
                                .load(urlFoto)
                                .into(holder.imageProdukRiwayat);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Gagal ambil gambar produk: " + error.getMessage());
                }
            });
        }

        holder.framePesanan.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPesananActivity.class);
            intent.putExtra("detailPesanan", new Gson().toJson(data));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return ordersData.size();
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProdukRiwayat;
        TextView tvNamaProdukRiwayat, tvVarianRiwayat,
                tvQtyRiwayat, tvHargaProdukRiwayat,
                idPesanan, statusPesanan, countTotalPesanan, totalhargaPesanan;
        LinearLayout framePesanan;
        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProdukRiwayat = itemView.findViewById(R.id.imageProdukRiwayat);
            tvNamaProdukRiwayat = itemView.findViewById(R.id.tvNamaProdukRiwayat);
            tvQtyRiwayat = itemView.findViewById(R.id.tvQtyRiwayat);
            tvVarianRiwayat = itemView.findViewById(R.id.tvVarianRiwayat);
            tvHargaProdukRiwayat = itemView.findViewById(R.id.tvHargaProdukRiwayat);
            idPesanan = itemView.findViewById(R.id.idPesanan);
            statusPesanan = itemView.findViewById(R.id.statusPesanan);
            countTotalPesanan = itemView.findViewById(R.id.countTotalPesanan);
            totalhargaPesanan = itemView.findViewById(R.id.totalhargaPesanan);
            framePesanan = itemView.findViewById(R.id.framePesanan);
        }
    }
}

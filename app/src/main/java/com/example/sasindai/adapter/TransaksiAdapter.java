package com.example.sasindai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrdersData;
import com.example.sasindai.model.ProdukData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {
    private final Context context;
    private final List<OrdersData> ordersData;

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

        String status = data.getStatus();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Siapkan builder untuk setiap field
        StringBuilder namaProdukBuilder = new StringBuilder();
        StringBuilder qtyBuilder = new StringBuilder();
        StringBuilder hargaBuilder = new StringBuilder();

        for (ItemProdukOrderData item : data.getProduk()) {
            namaProdukBuilder.append(item.getNama_varian()).append("\n");
            qtyBuilder.append("Qty: ").append(item.getQty()).append("\n");
            hargaBuilder.append("Harga: ").append(formatter.format(item.getHarga())).append("\n");
        }

        // Set data ke masing-masing TextView
        holder.tvNamaProdukRiwayat.setText(namaProdukBuilder.toString().trim());
        holder.tvQtyRiwayat.setText(qtyBuilder.toString().trim());
        holder.tvHargaProdukRiwayat.setText(hargaBuilder.toString().trim());
        holder.statusTransaksi.setText(status);

        // Ambil gambar dari ProdukData berdasarkan id_produk dari item pertama
        if (!data.getProduk().isEmpty()) {
            String idProduk = data.getProduk().get(0).getId_produk();

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

    }

    @Override
    public int getItemCount() {
        return ordersData.size();
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProdukRiwayat;
        TextView tvNamaProdukRiwayat, tvVarianRiwayat,
                tvQtyRiwayat, tvHargaProdukRiwayat, statusTransaksi;
        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProdukRiwayat = itemView.findViewById(R.id.imageProdukRiwayat);
            tvNamaProdukRiwayat = itemView.findViewById(R.id.tvNamaProdukRiwayat);
            tvQtyRiwayat = itemView.findViewById(R.id.tvQtyRiwayat);
            tvVarianRiwayat = itemView.findViewById(R.id.tvVarianRiwayat);
            tvHargaProdukRiwayat = itemView.findViewById(R.id.tvHargaProdukRiwayat);
            statusTransaksi = itemView.findViewById(R.id.statusTransaksi);
        }
    }
}

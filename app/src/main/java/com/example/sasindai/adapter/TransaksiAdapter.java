package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.PaymentActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrdersData;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.DetailPesananActivity;
import com.example.sasindai.model.VarianProduk;
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
import java.util.Map;

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
            ItemProdukOrderData item = data.getProduk().values().iterator().next(); // hanya produk ke-0

            holder.tvNamaProdukRiwayat.setText(item.getNamaVarian());
            holder.tvQtyRiwayat.setText(String.valueOf(item.getQty()));
            holder.tvHargaProdukRiwayat.setText(formatter.format(item.getHarga()).replace("Rp", "Rp "));
            holder.tvVarianRiwayat.setText(item.getNamaVarian());
        } else {
            holder.tvNamaProdukRiwayat.setText("-");
            holder.tvQtyRiwayat.setText("-");
            holder.tvHargaProdukRiwayat.setText("-");
            holder.tvVarianRiwayat.setText("-");
        }

        holder.statusPesanan.setText(status);
        holder.idPesanan.setText(data.getOrder_id());
        holder.countTotalPesanan.setText(String.valueOf(totalPesanan));
        holder.totalhargaPesanan.setText(formatter.format(data.getTotal()).replace("Rp", "Rp "));

        // Ambil gambar dari ProdukData berdasarkan id_produk dari item pertama
        if (!data.getProduk().isEmpty()) {
            ItemProdukOrderData itemProdukOrderData = data.getProduk().values().iterator().next();
            String idProduk = itemProdukOrderData.getIdProduk();
            String namaVarian = itemProdukOrderData.getNamaVarian();

            DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference("produk").child(idProduk);
            produkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProdukData produk = snapshot.getValue(ProdukData.class);
                    if (produk != null && produk.getVarian() != null) {
                        for (Map.Entry<String, VarianProduk> entry : produk.getVarian().entrySet()) {
                            VarianProduk varian = entry.getValue();
                            if (varian.getIdVarian().equals(itemProdukOrderData.getIdVarian())) {
                                String gambar = varian.getGambar();
                                itemProdukOrderData.setVarianUrl(gambar);
                                itemProdukOrderData.setNamaVarian(namaVarian);// ✅ simpan ke model

                                Glide.with(holder.itemView.getContext())
                                        .load(gambar)
                                        .into(holder.imageProdukRiwayat);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Gagal ambil gambar produk: " + error.getMessage());
                }
            });
        }

        // updateUI per status pesanan
        String statusPembayaran = data.getStatusPesanan();
        holder.btnHubungiPenjual.setVisibility(View.VISIBLE);

        switch (statusPembayaran.toLowerCase()) {
            case "menunggu pembayaran" :
                holder.btnBayarSekarang.setVisibility(View.VISIBLE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                break;
            case "kadaluarsa" :
            case "dibatalkan" :
            case "pesanan diterima" :
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                break;
            case "dikirim" :
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.VISIBLE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                break;
            case "dikemas" :
                holder.btnAjukanPembatalan.setVisibility(View.VISIBLE);
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
        }

        // hubungi penjual arahkan ke apk wa nomor +6282251356040
        holder.btnHubungiPenjual.setOnClickListener(v -> {
            String idPesanan = data.getOrder_id();
            String pesanDefault = "Permisi, ada yang ingin saya tanyakan mengenai pesanan ini, " + idPesanan;

            String nomor = "6282251356040"; // tanpa tanda +
            String url = "https://wa.me/" + nomor + "?text=" + Uri.encode(pesanDefault);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        });

        holder.btnBayarSekarang.setOnClickListener(v -> {
            String paymentLink = data.getPayment_url();
            if (paymentLink != null && !paymentLink.isEmpty()) {
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("payment_url", paymentLink);
                // optional: jika mau bawa tipe_checkout dan selectedItems juga
                intent.putExtra("tipe_checkout", data.getTipeCheckout());
                intent.putExtra("selectedItems", new Gson().toJson(data.getProduk()));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Link pembayaran tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

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
                idPesanan, statusPesanan, countTotalPesanan, totalhargaPesanan,
                btnBayarSekarang, btnPesananDiterima, btnHubungiPenjual, btnAjukanPembatalan;
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
            btnBayarSekarang = itemView.findViewById(R.id.btnBayarSekarang);
            btnPesananDiterima = itemView.findViewById(R.id.btnPesananDiterima);
            btnHubungiPenjual = itemView.findViewById(R.id.btnHubungiPenjual);
            btnAjukanPembatalan = itemView.findViewById(R.id.btnAjukanPembatalan);
        }
    }
}

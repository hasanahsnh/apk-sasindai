package com.example.sasindai.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.KonfirmasiPesananActivity;
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

        int totalQty = 0;
        if (data.getProduk() != null) {
            for (Map.Entry<String, ItemProdukOrderData> entry : data.getProduk().entrySet()) {
                ItemProdukOrderData item = entry.getValue();
                if (item != null) {
                    totalQty += item.getQty();
                }
            }
        }

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
        holder.countTotalPesanan.setText(String.valueOf(totalQty));
        holder.totalhargaPesanan.setText(formatter.format(data.getTotal()).replace("Rp", "Rp "));

        // Ambil gambar dari ProdukData berdasarkan id_produk dari item pertama
        try {
            ItemProdukOrderData itemProdukOrderData = data.getProduk().values().iterator().next();

            // Tampilkan gambar langsung dari data yang sudah tersedia
            String gambarVarian = itemProdukOrderData.getVarianUrl();
            if (gambarVarian != null && !gambarVarian.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(gambarVarian)
                        .into(holder.imageProdukRiwayat);
            }

        } catch (Exception e) {
            Log.e("Memuat gambar preview riwayat", "itemProdukOrderData tidak ditemukan: " + e.getMessage());
        }

        // updateUI per status pesanan
        String statusPembayaran = data.getStatusPesanan();
        holder.btnHubungiPenjual.setVisibility(View.VISIBLE);

        switch (statusPembayaran.toLowerCase()) {
            case "menunggu pembayaran" :
                holder.btnBayarSekarang.setVisibility(View.VISIBLE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                holder.btnSalinResi.setVisibility(View.GONE);
                break;
            case "kadaluarsa" :
            case "dibatalkan" :
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                holder.btnSalinResi.setVisibility(View.GONE);
                break;
            case "dikirim" :
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.VISIBLE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                holder.btnSalinResi.setVisibility(View.VISIBLE);
                break;
            case "dikemas" :
                holder.btnAjukanPembatalan.setVisibility(View.VISIBLE);
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnSalinResi.setVisibility(View.GONE);
                break;
            case "pesanan diterima" :
                holder.btnBayarSekarang.setVisibility(View.GONE);
                holder.btnPesananDiterima.setVisibility(View.GONE);
                holder.btnAjukanPembatalan.setVisibility(View.GONE);
                holder.btnSalinResi.setVisibility(View.VISIBLE);
                break;
        }

        /* btn yang dialihkan ke actv transaksi
        * - btn pesanan diterima*/

        String idPesanan = data.getOrder_id();
        holder.btnSalinResi.setOnClickListener(v -> {
            DatabaseReference pengirimanRefs = FirebaseDatabase.getInstance().getReference("pengiriman");

            pengirimanRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean resiDitemukan = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String pesanan = dataSnapshot.child("idPesanan").getValue(String.class);

                        if (idPesanan.equals(pesanan)) {
                            String resi = dataSnapshot.child("resiPesanan").getValue(String.class);
                            if (resi != null && !resi.isEmpty()) {
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Resi", resi);
                                clipboard.setPrimaryClip(clip);

                                Toast.makeText(context, "Resi berhasil disalin: " + resi, Toast.LENGTH_SHORT).show();
                                resiDitemukan = true;
                                break;
                            }
                        }
                    }

                    if (!resiDitemukan) {
                        Toast.makeText(context, "Resi tidak ditemukan untuk pesanan ini.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Gagal mengambil data resi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnPesananDiterima.setOnClickListener(v -> {
            // ambil idpesanan alihkan ke actv beri penilaian
            Intent intent = new Intent(context, KonfirmasiPesananActivity.class);
            intent.putExtra("order_id", idPesanan);

            // Kirim data produk ke activity
            String jsonProduk = new Gson().toJson(data.getProduk());
            intent.putExtra("produk_dipesan", jsonProduk);

            context.startActivity(intent);
        });


        holder.btnAjukanPembatalan.setOnClickListener(v -> {
            String pesanPengajuan = "Halo, saya ingin mengajukan pembatalan pesanan.\n\n" +
                    "ID Pesanan: " + idPesanan + "\n" +
                    "Alasan pembatalan: [Tulis alasan Anda di sini]\n\n" +
                    "Mohon tindak lanjutnya. Terima kasih.";

            String nomorAdmin = "6282251356040";
            String url = "https://wa.me/" + nomorAdmin + "?text=" + Uri.encode(pesanPengajuan);

            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage("Ajukan pembatalan pesanan?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                Typeface font = ResourcesCompat.getFont(context, R.font.poppins_medium);
                Typeface fontReguler = ResourcesCompat.getFont(context, R.font.poppins_bold);
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if (positiveBtn != null) {
                    positiveBtn.setTextColor(ContextCompat.getColor(context, R.color.maroon));
                    positiveBtn.setTypeface(font);
                }

                if (negativeBtn != null) {
                    negativeBtn.setTextColor(ContextCompat.getColor(context, R.color.maroon));
                    negativeBtn.setTypeface(font);
                }

                if (messageView != null) {
                    messageView.setTypeface(fontReguler);
                    messageView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            });

            alertDialog.show();
        });

        // hubungi penjual arahkan ke apk wa nomor +6282251356040
        holder.btnHubungiPenjual.setOnClickListener(v -> {
            String pesanDefault = "Halo, saya ingin menanyakan sesuatu terkait pesanan berikut:\n\n" +
                    "ID Pesanan: " + idPesanan + "\n\n" +
                    "Mohon bantuannya, terima kasih";

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
        ImageView btnSalinResi;
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
            btnSalinResi = itemView.findViewById(R.id.btnSalinResi);
        }
    }
}

package com.example.sasindai.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrderItemWrapper;
import com.example.sasindai.model.OrdersData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KonfirmasiPesananPenilaianAdapter extends RecyclerView.Adapter<KonfirmasiPesananPenilaianAdapter.ViewHolder> {
    private final Context context;
    private final List<OrderItemWrapper> dataList;

    public KonfirmasiPesananPenilaianAdapter(Context context, List<OrderItemWrapper> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public KonfirmasiPesananPenilaianAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_penilaian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KonfirmasiPesananPenilaianAdapter.ViewHolder holder, int position) {
        OrderItemWrapper item = dataList.get(position);
        ItemProdukOrderData produk = item.getProduk();

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        holder.tvNamaProdukPenilain.setText(produk.getNamaProduk());
        holder.tvVarianPenilaian.setText(produk.getNamaVarian());
        holder.tvHargaPenilaian.setText(formatter.format(produk.getHarga()).replace("Rp", "Rp "));
        holder.tvQtyPenilaian.setText(String.valueOf(produk.getQty()));

        // Tampilkan gambar varian
        String gambarVarian = produk.getVarianUrl(); // atau produk.getGambarVarian()
        if (gambarVarian != null && !gambarVarian.isEmpty()) {
            Glide.with(context)
                    .load(gambarVarian)
                    .into(holder.imgNilaiVarian);
        }

        holder.ratingBar.setRating(item.getRating());
        holder.etKomentarPenilaian.setText(item.getKomentar());

        // Simpan rating saat berubah
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            item.setRating(rating);
        });

        // Simpan komentar saat teks berubah
        holder.etKomentarPenilaian.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setKomentar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgNilaiVarian;
        TextView tvNamaProdukPenilain, tvVarianPenilaian, tvHargaPenilaian, tvQtyPenilaian;
        EditText etKomentarPenilaian;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNilaiVarian = itemView.findViewById(R.id.imgNilaiVarian);
            tvNamaProdukPenilain = itemView.findViewById(R.id.tvNamaProdukPenilain);
            tvVarianPenilaian = itemView.findViewById(R.id.tvVarianPenilaian);
            tvHargaPenilaian = itemView.findViewById(R.id.tvHargaPenilaian);
            tvQtyPenilaian = itemView.findViewById(R.id.tvQtyPenilaian);
            etKomentarPenilaian = itemView.findViewById(R.id.etKomentarPenilaian);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

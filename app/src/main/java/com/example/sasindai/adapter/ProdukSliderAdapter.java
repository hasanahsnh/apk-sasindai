package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.sasindai.DetailProdukActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukSliderAdapter extends RecyclerView.Adapter<ProdukSliderAdapter.ProdukSliderViewHolder> {
    private final Context context;
    private final List<ProdukData> produkData;

    public ProdukSliderAdapter(Context context, List<ProdukData> produkData) {
        this.context = context;
        this.produkData = produkData;
    }

    @NonNull
    @Override
    public ProdukSliderAdapter.ProdukSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_produk_in_beranda, parent, false);
        return new ProdukSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukSliderAdapter.ProdukSliderViewHolder holder, int position) {
        ProdukData data = produkData.get(position);

        holder.tvNamaProduk.setText(data.getNamaProduk());
        holder.tvTotalProdukTerjual.setText(String.valueOf(data.getTerjual()));

        // Array; mendapat akn data varian; data produk -> varian -> harga
        List<VarianProduk> varian = data.getVarian();
        if (varian != null && !varian.isEmpty()) {
            int totalHarga = 0;
            for (VarianProduk varianProduk : varian) {
                totalHarga += varianProduk.getHarga();
            }
            int averageHarga = totalHarga / varian.size();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            holder.tvHargaProduk.setText(formatter.format(averageHarga).replace("Rp", "Rp "));
        } else {
            holder.tvHargaProduk.setText("Harga tidak tersedia");
        }


        // Array Gambar
        List<String> daftarFotoProduk = data.getUrlFotoProduk();
        if (daftarFotoProduk != null) {
            Glide.with(context)
                    .load(daftarFotoProduk.get(0))
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16)))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            if (e != null) {
                                Log.e("Produk Adapter", "Gagal memuat gambar pada hero slider beranda");
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.imgProduk);
        }

        holder.btnLihatDetailBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProdukActivity.class);
            intent.putExtra("produk", new Gson().toJson(data));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return Math.min(produkData.size(), 4);
    }

    public static class ProdukSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduk;
        TextView tvNamaProduk, tvTotalProdukTerjual, tvHargaProduk;
        LinearLayout btnLihatDetailBeranda;
        public ProdukSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduk = itemView.findViewById(R.id.imgListProdukPreview);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProdukPreview);
            tvTotalProdukTerjual = itemView.findViewById(R.id.tvTotalProdukTerjualPreview);
            tvHargaProduk = itemView.findViewById(R.id.tvHargaProdukPreview);
            btnLihatDetailBeranda = itemView.findViewById(R.id.lihatProduk);
        }
    }
}

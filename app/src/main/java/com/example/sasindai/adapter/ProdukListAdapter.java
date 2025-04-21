package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sasindai.R;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;

import java.util.List;

public class ProdukListAdapter extends RecyclerView.Adapter<ProdukListAdapter.ProdukListViewHolder> {
    private final Context context;
    private final List<ProdukData> produkDataList;

    public ProdukListAdapter(Context context, List<ProdukData> produkDataList) {
        this.context = context;
        this.produkDataList = produkDataList;
    }

    @NonNull
    @Override
    public ProdukListAdapter.ProdukListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_produk, parent, false);
        return new ProdukListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukListAdapter.ProdukListViewHolder holder, int position) {
        ProdukData produkData = produkDataList.get(position);

        holder.tvNamaProduk.setText(produkData.getNamaProduk());
        holder.tvTotalProdukTerjual.setText(produkData.getTerjual());

        Glide.with(context)
                .load(produkData.getUrlFotoProduk())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            Log.e("Glide Error", "Gagal memuat gambar pada hero slider beranda");
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imgListProduk);
    }

    @Override
    public int getItemCount() {
        return produkDataList.size();
    }

    public static class ProdukListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgListProduk;
        TextView tvNamaProduk, tvTotalProdukTerjual;
        public ProdukListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgListProduk = itemView.findViewById(R.id.imgListProduk);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvTotalProdukTerjual = itemView.findViewById(R.id.tvTotalProdukTerjual);
        }
    }
}

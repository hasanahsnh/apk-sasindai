package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.example.sasindai.R;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;

import java.util.List;

public class ProdukFotoSliderAdapter extends RecyclerView.Adapter<ProdukFotoSliderAdapter.ProdukFotoSliderViewHolder> {
    private final Context context;
    private final List<String> semuaGambar;

    public ProdukFotoSliderAdapter(Context context, List<String> semuaGambar) {
        this.context = context;
        this.semuaGambar = semuaGambar;
    }

    @NonNull
    @Override
    public ProdukFotoSliderAdapter.ProdukFotoSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_slider_foto_produk, parent, false);
        return new ProdukFotoSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukFotoSliderAdapter.ProdukFotoSliderViewHolder holder, int position) {
        String url = semuaGambar.get(position);

        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            Log.e("Produk Foto Slider Adapter", "Gagal memuat gambar: " + url);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return semuaGambar.size();
    }

    public static class ProdukFotoSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ProdukFotoSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduk);
        }
    }
}

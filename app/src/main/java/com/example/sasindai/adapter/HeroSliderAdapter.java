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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sasindai.R;
import com.example.sasindai.model.RilisMediaData;

import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {
    private final Context context;
    private final List<RilisMediaData> rilisMediaDataList;

    public HeroSliderAdapter(Context context, List<RilisMediaData> rilisMediaData) {
        this.context = context;
        this.rilisMediaDataList = rilisMediaData;
    }

    @NonNull
    @Override
    public HeroSliderAdapter.HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_slider_hero, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroSliderAdapter.HeroViewHolder holder, int position) {
        RilisMediaData heroData = rilisMediaDataList.get(position);

        Glide.with(context)
                .load(heroData.getFotoBeritaUrl())
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
                .into(holder.imgSliderHero);

    }

    @Override
    public int getItemCount() {
        return Math.min(rilisMediaDataList.size(), 4);
    }

    public static class HeroViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSliderHero;
        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSliderHero = itemView.findViewById(R.id.imgSliderHero);
        }
    }
}

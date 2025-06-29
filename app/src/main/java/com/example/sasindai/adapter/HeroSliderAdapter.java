package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.sasindai.DetailRilisMediaActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.RilisMediaData;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

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

        holder.tvJudulRilisMediaHero.setText(heroData.getJudulArtikel());

        Glide.with(context)
                .load(heroData.getFotoBeritaUrl())
                .placeholder(R.drawable.ic_motif_29)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            Log.e("Glide Error", "Gagal memuat gambar pada hero slider beranda");
                            Log.d("HeroSliderAdapter", "URL gambar hero: " + heroData.getFotoBeritaUrl());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imgSliderHero);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRilisMediaActivity.class);
            intent.putExtra("data_rilis_media", new Gson().toJson(rilisMediaDataList.get(0)));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(rilisMediaDataList.size(), 4);
    }

    public static class HeroViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgSliderHero;
        TextView tvJudulRilisMediaHero;
        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSliderHero = itemView.findViewById(R.id.imgSliderHero);
            tvJudulRilisMediaHero = itemView.findViewById(R.id.tvJudulRilisMediaHero);
        }
    }
}

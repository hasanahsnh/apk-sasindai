package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.sasindai.DetailRilisMediaActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.RilisMediaData;
import com.google.gson.Gson;

import java.util.List;

public class RilisMediaListAdapter extends RecyclerView.Adapter<RilisMediaListAdapter.RilisMediaListViewHolder> {
    private final Context context;
    private final List<RilisMediaData> data;

    public RilisMediaListAdapter(Context context, List<RilisMediaData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RilisMediaListAdapter.RilisMediaListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_berita_beranda, parent, false);
        return new RilisMediaListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RilisMediaListAdapter.RilisMediaListViewHolder holder, int position) {
        RilisMediaData rilisMediaData = data.get(position);

        holder.tvJudulRilisMedia.setText(rilisMediaData.getJudulArtikel());

        Glide.with(context)
                .load(rilisMediaData.getFotoBeritaUrl())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(25)))
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
                .into(holder.imgRilisMedia);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRilisMediaActivity.class);
            intent.putExtra("data_rilis_media", new Gson().toJson(data.get(0)));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(), 4);
    }

    public static class RilisMediaListViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulRilisMedia;
        ImageView imgRilisMedia;
        FrameLayout frameListRilisMedia;
        public RilisMediaListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulRilisMedia = itemView.findViewById(R.id.tvJudulRilisMedia);
            imgRilisMedia = itemView.findViewById(R.id.imgRilisMedia);
            frameListRilisMedia = itemView.findViewById(R.id.frameListRilisMedia);
        }
    }
}

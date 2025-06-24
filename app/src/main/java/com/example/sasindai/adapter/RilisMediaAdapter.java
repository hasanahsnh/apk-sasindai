package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sasindai.DetailRilisMediaActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.RilisMediaData;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RilisMediaAdapter extends RecyclerView.Adapter<RilisMediaAdapter.ViewHolder> {

    private Context context;
    private List<RilisMediaData> rilisMediaList;

    public RilisMediaAdapter(Context context, List<RilisMediaData> rilisMediaList) {
        this.context = context;
        this.rilisMediaList = rilisMediaList;
    }

    @NonNull
    @Override
    public RilisMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rilis_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RilisMediaAdapter.ViewHolder holder, int position) {
        RilisMediaData data = rilisMediaList.get(position);

        holder.tvJudulArtikel.setText(data.getJudulArtikel());
        holder.tvKonten.setText(data.getKontenBerita());
        holder.tvTanggal.setText(data.getTanggalTerbit());

        Glide.with(context)
                .load(data.getFotoBeritaUrl())
                .into(holder.imgThumbnail);

        // Tambahkan klik item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRilisMediaActivity.class);
            intent.putExtra("data_rilis_media", new Gson().toJson(data));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rilisMediaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgThumbnail;
        TextView tvJudulArtikel, tvKonten, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvJudulArtikel = itemView.findViewById(R.id.tvJudulArtikel);
            tvKonten = itemView.findViewById(R.id.tvKonten);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }
}

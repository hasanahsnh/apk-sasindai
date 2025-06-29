package com.example.sasindai.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.example.sasindai.DetailMotifActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.KatalogMotifData;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class KatalogMotifListAdapter extends RecyclerView.Adapter<KatalogMotifListAdapter.KatalogMotifListViewHolder> {
    private final Context context;
    private final List<KatalogMotifData> data;

    public KatalogMotifListAdapter(Context context, List<KatalogMotifData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public KatalogMotifListAdapter.KatalogMotifListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_katalog_motif, parent, false);
        return new KatalogMotifListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KatalogMotifListAdapter.KatalogMotifListViewHolder holder, int position) {
        KatalogMotifData katalogMotifData = data.get(position);

        holder.namaMotif.setText(katalogMotifData.getMotif());
        holder.deskripsiMotif.setText(katalogMotifData.getFilosofi());

        Glide.with(context)
                .load(katalogMotifData.getGambarUrl())
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(25))
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
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
                .into(holder.img);

        holder.imgMore.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuFontStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.imgMore);
            popupMenu.getMenuInflater().inflate(R.menu.menu_more_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.selengkapnya) {
                    Intent intentKeDetail = new Intent(context, DetailMotifActivity.class);
                    intentKeDetail.putExtra("detailMotif", new Gson().toJson(katalogMotifData));
                    context.startActivity(intentKeDetail);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class KatalogMotifListViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img;
        TextView namaMotif, deskripsiMotif;
        ImageView imgMore;
        public KatalogMotifListViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgMotif);
            namaMotif = itemView.findViewById(R.id.tvNamaMotif);
            deskripsiMotif = itemView.findViewById(R.id.tvDeskripsiMotif);
            imgMore = itemView.findViewById(R.id.imgMore);
        }

    }

    public void setData(List<KatalogMotifData> newData) {
        this.data.clear();
        this.data.addAll(newData);
    }
}

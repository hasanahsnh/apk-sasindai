package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sasindai.R;
import com.example.sasindai.fragment.Objek3DData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class Objek3DAdapter extends RecyclerView.Adapter<Objek3DAdapter.Objek3DViewHolder> {
    private final Context context;
    private final List<Objek3DData> data;

    public Objek3DAdapter(Context context, List<Objek3DData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Objek3DAdapter.Objek3DViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_objek_3d, parent, false);
        return new Objek3DViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Objek3DAdapter.Objek3DViewHolder holder, int position) {
        Objek3DData item = data.get(position);

        Log.d("Objek3DAdapter", "Preview URL: " + item.getPreviewObjek());
        Glide.with(context)
                .load(item.getPreviewObjek())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            Log.e("Glide Error", "Gagal memuat gambar pada preview objek");
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
        return data.size();
    }

    public static class Objek3DViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img;
        public Objek3DViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSliderObjek3d);
        }
    }
}

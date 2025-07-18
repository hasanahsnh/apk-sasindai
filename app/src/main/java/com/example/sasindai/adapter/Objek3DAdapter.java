package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.sasindai.model.Objek3DData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class Objek3DAdapter extends RecyclerView.Adapter<Objek3DAdapter.Objek3DViewHolder> {
    private final Context context;
    private final List<Objek3DData> data;
    private final OnItemClickListener listener;
    private int selectedPosition = -1;

    public Objek3DAdapter(Context context, List<Objek3DData> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Objek3DData objek3DData);
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

        holder.linLayoutItem3d.setBackgroundResource(
                selectedPosition == position ? R.drawable.frame_selected_item : android.R.color.transparent
        );

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            int clickPosition = holder.getAdapterPosition();
            if (clickPosition != RecyclerView.NO_POSITION) {
                selectedPosition = clickPosition;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onItemClick(data.get(clickPosition));
                }
            }
        });

        holder.tvNamaObjek.setText(item.getNamaObjek());

        Log.d("Adapter", "Binding position: " + position + ", selected: " + selectedPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Objek3DViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img;
        LinearLayout linLayoutItem3d;
        TextView tvNamaObjek;
        public Objek3DViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSliderObjek3d);
            linLayoutItem3d = itemView.findViewById(R.id.linLayoutItem3d);
            tvNamaObjek = itemView.findViewById(R.id.tvNamaObjek);
        }
    }
}

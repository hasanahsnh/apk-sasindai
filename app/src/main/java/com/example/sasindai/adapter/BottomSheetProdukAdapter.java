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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.sasindai.R;
import com.example.sasindai.model.VarianProduk;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BottomSheetProdukAdapter extends RecyclerView.Adapter<BottomSheetProdukAdapter.BottomSheetProdukViewHolder> {
    private final Context context;
    private final List<VarianProduk> varianProduk;
    private OnItemClickListener listener; // jgn set final karena perlu pewarisan
    private int selectedPosition = -1; // ini juga

    public BottomSheetProdukAdapter(Context context, List<VarianProduk> varianProduk) {
        this.context = context;
        this.varianProduk = varianProduk;
    }

    // Fungsi click item
    public interface OnItemClickListener {
        void onClickItem(VarianProduk varianProduk);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BottomSheetProdukAdapter.BottomSheetProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_sheet_layout, parent, false);
        return new BottomSheetProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetProdukAdapter.BottomSheetProdukViewHolder holder, int position) {
        VarianProduk data = varianProduk.get(position);

        holder.tvSheetUkuran.setText(data.getSize());
        holder.tvNamaVarian.setText(data.getNama());
        holder.tvStokVarian.setText(String.valueOf(data.getStok()));

        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        holder.layoutItem.setBackgroundResource(
                selectedPosition == position ? R.drawable.frame_selected_item: R.color.putih
        );

        holder.itemView.setOnClickListener(v -> {
            int clickPosition = holder.getAdapterPosition();
            selectedPosition = clickPosition;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onClickItem(data);
            }
        });

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.tvSheetHarga.setText(formatter.format(data.getHarga()).replace("Rp", "Rp "));

        if (data.getStok() == 0) {
            holder.layoutItem.setEnabled(false);
            holder.layoutItem.setAlpha(0.5f);
        } else {
            holder.layoutItem.setEnabled(true);
            holder.layoutItem.setAlpha(1.0f);
        }

        Glide.with(context)
                .load(data.getGambar())
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
                .into(holder.imgSheetItem);

    }

    @Override
    public int getItemCount() {
        return varianProduk.size();
    }

    public static class BottomSheetProdukViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgSheetItem;
        TextView tvSheetUkuran, tvSheetHarga, tvNamaVarian, tvStokVarian;
        LinearLayout layoutItem;
        public BottomSheetProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSheetItem = itemView.findViewById(R.id.imgSheetItem);
            tvSheetUkuran = itemView.findViewById(R.id.tvSheetUkuran);
            tvSheetHarga = itemView.findViewById(R.id.tvSheetHarga);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            tvNamaVarian = itemView.findViewById(R.id.tvNamaVarian);
            tvStokVarian = itemView.findViewById(R.id.tvStokVarian);
        }
    }
}

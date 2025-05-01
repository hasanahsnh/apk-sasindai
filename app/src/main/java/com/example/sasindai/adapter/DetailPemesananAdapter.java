package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sasindai.model.KeranjangData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailPemesananAdapter extends RecyclerView.Adapter<DetailPemesananAdapter.DetailPemesananViewHolder> {
    private final Context context;
    private final List<KeranjangData> dataKeranjang;

    public DetailPemesananAdapter(Context context, List<KeranjangData> dataKeranjang) {
        this.context = context;
        this.dataKeranjang = dataKeranjang;
    }

    @NonNull
    @Override
    public DetailPemesananAdapter.DetailPemesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_in_checkout, parent, false);
        return new DetailPemesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPemesananAdapter.DetailPemesananViewHolder holder, int position) {
        KeranjangData item = dataKeranjang.get(position);

        // Bind data to the view holder
        holder.tvNamaProdukOrder.setText(item.getNamaVarian());
        holder.tvVarianOrder.setText(item.getSize());
        holder.tvQtyOrder.setText(String.valueOf(item.getQty()));

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.tvHargaOrder.setText(formatRupiah.format(item.getHarga()).replace("Rp", "Rp "));

        Glide.with(context)
                .load(item.getGambarVarian())
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
                .into(holder.imageItem);
    }

    @Override
    public int getItemCount() {
        return dataKeranjang.size();
    }

    public static class DetailPemesananViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageItem;
        TextView tvNamaProdukOrder, tvVarianOrder, tvHargaOrder, tvQtyOrder;
        public DetailPemesananViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imageItem);
            tvNamaProdukOrder = itemView.findViewById(R.id.tvNamaProdukOrder);
            tvVarianOrder = itemView.findViewById(R.id.tvVarianOrder);
            tvHargaOrder = itemView.findViewById(R.id.tvHargaOrder);
            tvQtyOrder = itemView.findViewById(R.id.tvQtyOrder);
        }
    }
}

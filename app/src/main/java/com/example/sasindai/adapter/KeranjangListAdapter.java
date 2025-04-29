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

public class KeranjangListAdapter extends RecyclerView.Adapter<KeranjangListAdapter.KeranjangListViewHolder> {
    private final Context context;
    private final List<KeranjangData> keranjangData;

    public KeranjangListAdapter(Context context, List<KeranjangData> keranjangData) {
        this.context = context;
        this.keranjangData = keranjangData;
    }

    @NonNull
    @Override
    public KeranjangListAdapter.KeranjangListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_keranjang, parent, false);
        return new KeranjangListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeranjangListAdapter.KeranjangListViewHolder holder, int position) {
        KeranjangData data = keranjangData.get(position);

        holder.tvNamaProdukKeranjang.setText(data.getNamaVarian());
        holder.tvVarianKeranjang.setText(data.getSize());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.tvHargaProdukKeranjang.setText(formatter.format(data.getHarga()).replace("Rp", "Rp "));

        holder.tvQtyKeranjang.setText(String.valueOf(data.getQty()));

        Glide.with(context)
                .load(data.getGambarVarian())
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
                .into(holder.imageProduk);
    }

    @Override
    public int getItemCount() {
        return keranjangData.size();
    }

    public static class KeranjangListViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProduk;
        TextView tvNamaProdukKeranjang, tvVarianKeranjang, tvHargaProdukKeranjang, tvQtyKeranjang;
        public KeranjangListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduk = itemView.findViewById(R.id.imageProduk);
            tvNamaProdukKeranjang = itemView.findViewById(R.id.tvNamaProdukKeranjang);
            tvVarianKeranjang = itemView.findViewById(R.id.tvVarianKeranjang);
            tvHargaProdukKeranjang = itemView.findViewById(R.id.tvHargaProdukKeranjang);
            tvQtyKeranjang = itemView.findViewById(R.id.tvQtyKeranjang);
        }
    }
}

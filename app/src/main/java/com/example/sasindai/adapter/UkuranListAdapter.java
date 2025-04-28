package com.example.sasindai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.VarianProduk;

import java.util.List;

public class UkuranListAdapter extends RecyclerView.Adapter<UkuranListAdapter.UkuranListViewHolder> {
    private final Context context;
    private final List<VarianProduk> produkDataList;

    public UkuranListAdapter(Context context, List<VarianProduk> produkDataList) {
        this.context = context;
        this.produkDataList = produkDataList;
    }

    @NonNull
    @Override
    public UkuranListAdapter.UkuranListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_ukuran, parent, false);
        return new UkuranListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UkuranListViewHolder holder, int position) {
        VarianProduk data = produkDataList.get(position);

        holder.tvUkuran.setText(data.getSize());

        if (data.getStok() == 0) {
            holder.tvUkuran.setEnabled(false);
            holder.tvUkuran.setAlpha(0.5f);
        } else {
            holder.tvUkuran.setEnabled(true);
            holder.tvUkuran.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return produkDataList.size();
    }

    public static class UkuranListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUkuran;
        public UkuranListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUkuran = itemView.findViewById(R.id.tvUkuranProduk);
        }
    }
}

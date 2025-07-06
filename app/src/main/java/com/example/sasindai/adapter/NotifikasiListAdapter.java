package com.example.sasindai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.NotifikasiData;

import java.util.List;

public class NotifikasiListAdapter extends RecyclerView.Adapter<NotifikasiListAdapter.NotifikasiListViewHolder> {
    Context context;
    List<NotifikasiData> data;

    public NotifikasiListAdapter(Context context, List<NotifikasiData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public NotifikasiListAdapter.NotifikasiListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_notifikasi, parent, false);
        return new NotifikasiListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifikasiListAdapter.NotifikasiListViewHolder holder, int position) {
        NotifikasiData dataNotifikasi = data.get(position);

        if (dataNotifikasi != null && dataNotifikasi.getJudulNotifikasi() != null) {
            holder.judulNotifikasi.setText(dataNotifikasi.getJudulNotifikasi());
        } else {
            Log.w("getJudulNotifikasi", "data bernilai null");
        }

        if (dataNotifikasi != null && dataNotifikasi.getPesanNotifikasi() != null) {
            holder.isiNotifikasi.setText(dataNotifikasi.getPesanNotifikasi());
        } else {
            Log.w("getisiNotifikasi", "data bernilai null");
        }

        if (dataNotifikasi != null && dataNotifikasi.getWaktu() != null) {
            holder.waktu.setText(dataNotifikasi.getWaktu());
        } else {
            Log.w("getWaktuNotifikasi", "data bernilai null");
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class NotifikasiListViewHolder extends RecyclerView.ViewHolder {
        TextView judulNotifikasi, isiNotifikasi, waktu;
        public NotifikasiListViewHolder(@NonNull View itemView) {
            super(itemView);
            judulNotifikasi = itemView.findViewById(R.id.tvJudulNotifikasi);
            isiNotifikasi = itemView.findViewById(R.id.isiNotifikasi);
            waktu = itemView.findViewById(R.id.tvWaktu);
        }
    }
}

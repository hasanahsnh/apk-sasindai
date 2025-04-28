package com.example.sasindai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;

public class ShimmerRilisMediaAdapter extends RecyclerView.Adapter<ShimmerRilisMediaAdapter.ShimmerRilisMediaViewHolder> {
    private final int itemCount;

    public ShimmerRilisMediaAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public ShimmerRilisMediaAdapter.ShimmerRilisMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_rilis_media_beranda, parent, false);
        return new ShimmerRilisMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerRilisMediaAdapter.ShimmerRilisMediaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static class ShimmerRilisMediaViewHolder extends RecyclerView.ViewHolder {
        public ShimmerRilisMediaViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

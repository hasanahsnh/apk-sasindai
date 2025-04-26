package com.example.sasindai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ShimmerProdukAdapter extends RecyclerView.Adapter<ShimmerProdukAdapter.ShimmerProdukViewHolder> {
    private final int itemCount;

    public ShimmerProdukAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public ShimmerProdukAdapter.ShimmerProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_produk, parent, false);
        return new ShimmerProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerProdukAdapter.ShimmerProdukViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static class ShimmerProdukViewHolder extends RecyclerView.ViewHolder {
        public ShimmerProdukViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

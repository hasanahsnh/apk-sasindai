package com.example.sasindai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.ItemProdukOrderData;
import com.example.sasindai.model.OrderItemWrapper;
import com.example.sasindai.model.OrdersData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DaftarItemRiwayatPesananAdapter extends RecyclerView.Adapter<DaftarItemRiwayatPesananAdapter.ViewHolder> {
    private final Context context;
    private final List<OrderItemWrapper> dataList;

    public DaftarItemRiwayatPesananAdapter(Context context, List<OrderItemWrapper> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DaftarItemRiwayatPesananAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaftarItemRiwayatPesananAdapter.ViewHolder holder, int position) {
        OrderItemWrapper item = dataList.get(position);
        ItemProdukOrderData produk = item.getProduk();


        holder.tvNamaProdukOrder.setText(produk.getNamaProduk());
        holder.tvVarianOrder.setText(produk.getNamaVarian());
        holder.tvQtyOrder.setText(String.valueOf(produk.getQty()));

        String hargaFormatted = NumberFormat.getCurrencyInstance(new Locale("in", "ID"))
                .format(produk.getHarga());
        holder.tvHargaOrder.setText(hargaFormatted);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageItem;
        TextView tvNamaProdukOrder, tvVarianOrder, tvHargaOrder, tvQtyOrder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imagePesanan);
            tvNamaProdukOrder = itemView.findViewById(R.id.tvNamaProdukPesanan);
            tvVarianOrder = itemView.findViewById(R.id.tvVarianPesanan);
            tvHargaOrder = itemView.findViewById(R.id.tvHargaPesanan);
            tvQtyOrder = itemView.findViewById(R.id.tvQtyPesanan);
        }
    }
}

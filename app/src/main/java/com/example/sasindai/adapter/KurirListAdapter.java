package com.example.sasindai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.DaftarKurirData;
import com.example.sasindai.model.VarianProduk;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KurirListAdapter extends RecyclerView.Adapter<KurirListAdapter.KurirListViewHolder> {
    private final Context context;
    private final List<DaftarKurirData> daftarKurirData;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public KurirListAdapter(Context context, List<DaftarKurirData> daftarKurirData) {
        this.context = context;
        this.daftarKurirData = (daftarKurirData != null) ? daftarKurirData : new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onClickItem(DaftarKurirData daftarKurirData);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DaftarKurirData getSelectedKurir() {
        if (selectedPosition != -1 && selectedPosition < daftarKurirData.size()) {
            return daftarKurirData.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public KurirListAdapter.KurirListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_kurir, parent, false);
        return new KurirListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KurirListAdapter.KurirListViewHolder holder, int position) {
        DaftarKurirData data = daftarKurirData.get(position);

        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION){
            return;
        }

        holder.containerKurirItem.setBackgroundResource(
                selectedPosition == position ? R.drawable.frame_selected_kurir : R.color.putih
        );

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onClickItem(data);
            }
        });

        holder.tvKurir.setText(data.getName() + " - " + data.getService());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.tvHargaKurir.setText(formatter.format(data.getPrice()).replace("Rp", "Rp "));

        holder.tvEstimasiPengiriman.setText(data.getEtd());
    }

    @Override
    public int getItemCount() {
        return daftarKurirData.size();
    }

    public static class KurirListViewHolder extends RecyclerView.ViewHolder {
        TextView tvKurir, tvHargaKurir, tvEstimasiPengiriman;
        LinearLayout containerKurirItem;
        public KurirListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKurir = itemView.findViewById(R.id.tvKurir);
            tvHargaKurir = itemView.findViewById(R.id.tvHargaKurir);
            tvEstimasiPengiriman = itemView.findViewById(R.id.tvEstimasiPengiriman);
            containerKurirItem = itemView.findViewById(R.id.containerKurir);
        }
    }
}

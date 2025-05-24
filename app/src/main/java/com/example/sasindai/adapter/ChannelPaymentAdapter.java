package com.example.sasindai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.R;
import com.example.sasindai.model.ChannelPaymentData;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ChannelPaymentAdapter extends RecyclerView.Adapter<ChannelPaymentAdapter.ChannelPaymentViewHolder> {
    private final Context context;
    private List<ChannelPaymentData> data;
    int selectedPosition = -1;
    OnItemClickListener listener;

    public ChannelPaymentAdapter(Context context, List<ChannelPaymentData> data) {
        this.context = context;
        this.data = data;
    }

    public interface OnItemClickListener {
        void onClickItem(ChannelPaymentData channelPaymentData);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChannelPaymentData getSelectedPayment() {
        if (selectedPosition != -1 && selectedPosition < data.size()) {
            return data.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ChannelPaymentAdapter.ChannelPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_channel_payment, parent, false);
        return new ChannelPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelPaymentAdapter.ChannelPaymentViewHolder holder, int position) {
        ChannelPaymentData channelPayment = data.get(position);

        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        holder.containerChannelPayment.setBackgroundResource(
                selectedPosition == position ? R.drawable.frame_selected_kurir : R.color.putih
        );

        holder.itemView.setOnClickListener(v -> {
            int previouSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previouSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onClickItem(channelPayment);
            }
        });

        holder.channelPayment.setText(channelPayment.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.totalFee.setText(formatter.format(channelPayment.getFee_customer().getFlat()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ChannelPaymentViewHolder extends RecyclerView.ViewHolder {
        TextView channelPayment, totalFee;
        LinearLayout containerChannelPayment;
        public ChannelPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            channelPayment = itemView.findViewById(R.id.channelPayment);
            totalFee = itemView.findViewById(R.id.totalFee);
            containerChannelPayment = itemView.findViewById(R.id.containerChannelPayment);
        }
    }
}

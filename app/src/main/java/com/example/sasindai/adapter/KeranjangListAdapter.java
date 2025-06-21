package com.example.sasindai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.example.sasindai.KeranjangActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.KeranjangData;
import com.example.sasindai.model.VarianProduk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.OnSelectionChangedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeranjangListAdapter extends RecyclerView.Adapter<KeranjangListAdapter.KeranjangListViewHolder> {
    private final Context context;
    private final List<KeranjangData> keranjangData;
    private final List<KeranjangData> selectedItems = new ArrayList<>();
    private OnSelectionChangeListener selectionChangeListener;

    public KeranjangListAdapter(Context context, List<KeranjangData> keranjangData) {
        this.context = context;
        this.keranjangData = keranjangData;
    }

    public void setSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyItemRangeChanged(0, getItemCount());
        notifyTotalChanged();
    }

    public void setSelectedItems(List<KeranjangData> selectedItems) {
        this.selectedItems.clear();
        this.selectedItems.addAll(selectedItems);

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged();
        }
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged();
    }

    private void notifyTotalChanged() {
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged();
        }
    }

    public int getTotalHargaSelected() {
        int total = 0;
        for (KeranjangData item : selectedItems) {
            total += item.getHarga() * item.getQty();
        }
        return total;
    }

    public float getBeratItemSelected() {
        float berat = 0f;
        for (KeranjangData item : selectedItems) {
            berat += item.getBerat() * item.getQty();
        }
        return berat;
    }

    public void selectAll(boolean select) {
        selectedItems.clear();
        if (!select) {
            notifyDataSetChanged();
            notifyTotalChanged();
            return;
        }

        // Hitung jumlah item yang sudah dicek
        final int[] checkedCount = {0};

        for (KeranjangData item : keranjangData) {
            DatabaseReference varianRef = FirebaseDatabase.getInstance().getReference("produk")
                    .child(item.getIdProduk())
                    .child("varian");

            varianRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean stokHabis = false;
                    for (DataSnapshot varianSnap : snapshot.getChildren()) {
                        String namaVarianDb = varianSnap.child("nama").getValue(String.class);
                        if (namaVarianDb != null && namaVarianDb.equals(item.getNamaVarian())) {
                            Long stok = varianSnap.child("stok").getValue(Long.class);
                            if (stok != null && stok == 0) {
                                stokHabis = true;
                            }
                            break;
                        }
                    }

                    if (!stokHabis) {
                        selectedItems.add(item);
                    }

                    checkedCount[0]++;
                    if (checkedCount[0] == keranjangData.size()) {
                        notifyDataSetChanged();
                        notifyTotalChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    checkedCount[0]++;
                    if (checkedCount[0] == keranjangData.size()) {
                        notifyDataSetChanged();
                        notifyTotalChanged();
                    }
                    Log.e("selectAll", "Gagal membaca data varian: " + error.getMessage());
                }
            });
        }
    }

    public List<KeranjangData> getSelectedItems() {
        return new ArrayList<>(selectedItems);
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

        // Menyimpan item yang dipilih
        holder.checkBoxItem.setOnCheckedChangeListener(null);
        holder.checkBoxItem.setChecked(selectedItems.contains(data));
        holder.checkBoxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedItems.contains(data)) selectedItems.add(data);
                } else {
                    selectedItems.remove(data);
                }
                notifyTotalChanged();
            }
        });

        // Hapus item
        holder.imgTrash.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String idProduk = data.getIdProduk();
            String namaVarian = data.getNamaVarian();
            String idVarian = data.getIdVarian();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("keranjang")
                    .child(userId)
                    .child(idProduk)
                    .child(idVarian);

            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    int removePosition = holder.getAdapterPosition();
                    if (removePosition != RecyclerView.NO_POSITION) {
                        selectedItems.remove(data);
                        keranjangData.remove(removePosition);
                        notifyItemRemoved(removePosition);
                        notifyTotalChanged();

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (context instanceof KeranjangActivity) {
                                ((KeranjangActivity) context).AmbilProdukUser(); // refresh ulang data
                            }
                        }, 300);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Keranjang Adapter", "Gagal menghapus dari Firebase: " + e.getMessage());
                }
            });
        });

        DatabaseReference varianRef = FirebaseDatabase.getInstance().getReference("produk")
                .child(data.getIdProduk())
                .child("varian")
                .child(data.getIdVarian());

        varianRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long stok = snapshot.child("stok").getValue(Long.class);
                boolean isVarianHabis = stok != null && stok == 0;

                if (isVarianHabis) {
                    holder.checkBoxItem.setEnabled(false);
                    holder.checkBoxItem.setAlpha(0.5f);
                    holder.tvVarianKeranjang.setText("Produk habis");
                } else {
                    holder.checkBoxItem.setEnabled(true);
                    holder.checkBoxItem.setAlpha(1.0f);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

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
        CheckBox checkBoxItem;
        ImageView imgTrash;
        public KeranjangListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduk = itemView.findViewById(R.id.imageProduk);
            tvNamaProdukKeranjang = itemView.findViewById(R.id.tvNamaProdukKeranjang);
            tvVarianKeranjang = itemView.findViewById(R.id.tvVarianKeranjang);
            tvHargaProdukKeranjang = itemView.findViewById(R.id.tvHargaProdukKeranjang);
            tvQtyKeranjang = itemView.findViewById(R.id.tvQtyKeranjang);
            checkBoxItem = itemView.findViewById(R.id.checkBoxItem);
            imgTrash = itemView.findViewById(R.id.imgTrash);
        }

    }
}

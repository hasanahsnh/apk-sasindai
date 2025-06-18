package com.example.sasindai.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.sasindai.R;
import com.example.sasindai.adapter.ProdukListAdapter;
import com.example.sasindai.adapter.ShimmerProdukAdapter;
import com.example.sasindai.model.ProdukData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProdukFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProdukFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ProdukData> lists = new ArrayList<>();
    private ProdukListAdapter adapter;
    private ShimmerProdukAdapter shimmer;
    private RecyclerView recyclerView, recyclerViewShimmerProduk;
    private String kategori;

    public ProdukFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProdukFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProdukFragment newInstance(String param1, String param2) {
        ProdukFragment fragment = new ProdukFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            if (mParam1 != null) {
                kategori = mParam1.toLowerCase();
                Log.d("Produk Fragment", "Kategori diterima: " + kategori);
            }
        }

        if (mParam1 != null) {
            kategori = mParam1.toLowerCase(); // ← ini kunci penting
            Log.d("Produk Fragment", "Kategori diterima: " + kategori);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_produk, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi widget
        recyclerView = view.findViewById(R.id.gridViewListProduk);
        recyclerViewShimmerProduk = view.findViewById(R.id.recyclerViewShimmerProduk);
        Log.d("Produk Fragment", "GridView is: " + recyclerView);
        Log.d("Produk Fragment", "GridView is: " + recyclerViewShimmerProduk);

        recyclerViewShimmerProduk.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerViewShimmerProduk.setAdapter(new ShimmerProdukAdapter(6));

        adapter = new ProdukListAdapter(requireContext(), lists);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        // Proses pengambilan data dari firebase
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        if (!isAdded()) {
            Log.e("Produk Fragment", "Fragment gagal dimuat");
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("produk");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    Log.e("Produk Fragment", "Fragment telah dimuat, lewati perubahan data");
                    return;
                }

                lists.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        ProdukData data = dataSnapshot.getValue(ProdukData.class);
                        if (data != null) {
                            lists.add(data);
                            Log.d("Produk Fragment", "Produk ditambahkan: " + data.getNamaProduk());
                        } else {
                            Log.e("Produk Fragment", "Tidak ada data produk yang dimuat");
                        }
                    } catch (Exception e) {
                        Log.e("Produk Fragment", "Gagal memuat data produk!" + e.getMessage());
                    }
                }

                if (!lists.isEmpty()) {
                    recyclerViewShimmerProduk.setVisibility(View.GONE);
                } else {
                    recyclerViewShimmerProduk.setVisibility(View.VISIBLE);
                }

                // Set data per tab
                if (kategori == null) {
                    kategori = "populer";
                }
                String lowerCase = kategori.toLowerCase();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                switch (lowerCase) {
                    case "terbaru":
                        lists.sort((a, b) -> {
                            try {
                                Date dateA = sdf.parse(a.getCreateAt());
                                Date dateB = sdf.parse(b.getCreateAt());
                                return dateB.compareTo(dateA); // descending (terbaru)
                            } catch (ParseException e) {
                                return 0;
                            }
                        });
                        break;
                    case "terlaris":
                        lists.sort((a, b) -> Integer.compare(b.getTerjual(), a.getTerjual()));
                        break;
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Log.e("Produk Fragmment", "Database error " + error.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
package com.example.sasindai.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sasindai.R;
import com.example.sasindai.adapter.HeroSliderAdapter;
import com.example.sasindai.model.RilisMediaData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BerandaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BerandaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Dari sini..
    private HeroSliderAdapter heroSliderAdapter;
    private ArrayList<RilisMediaData> rilisMediaDataList;
    private RecyclerView recyclerViewHero;
    private ShimmerFrameLayout shimmerFrameLayout;

    public BerandaFragment() {
        // Required empty public constructor
    }
    public static BerandaFragment newInstance(String param1, String param2) {
        BerandaFragment fragment = new BerandaFragment();
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
        }

        // Khusus Class Fragment, letakkan semua komponen UI di onViewCreated(), bukan di onCreate().
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beranda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi widget
        recyclerViewHero = view.findViewById(R.id.hero);
        shimmerFrameLayout = view.findViewById(R.id.shimmerHero);

        // Mulai shimmer
        shimmerFrameLayout.startShimmer();

        if (recyclerViewHero != null) {
            recyclerViewHero.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rilisMediaDataList = new ArrayList<>();
        } else {
            Log.e("Recycler View Hero", "Recycler view hero gagal dimuat");
        }

        // Load data dari firebase (db)
        loadDataFromFirebase();

    }

    private void loadDataFromFirebase() {
        if (!isAdded()) {
            Log.e("Beranda Fragment", "Fragment gagal dimuat");
            return;
        }

        DatabaseReference databaseReferenceRilisMedia = FirebaseDatabase.getInstance().getReference("berita");

        databaseReferenceRilisMedia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Fragment telah dimuat, lewati perubahan data");
                    return;
                }
                rilisMediaDataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        RilisMediaData heroData = dataSnapshot.getValue(RilisMediaData.class);
                        if (heroData != null) {
                            rilisMediaDataList.add(heroData);
                        } else {
                            Log.e("Pemuatan Data", "Tidak ada data untuk hero yang dimuat" + dataSnapshot);
                        }
                    } catch (Exception e) {
                        Log.e("Pemuatan Data", "Error saat memuat data hero" + e.getMessage());
                    }
                }

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                heroSliderAdapter = new HeroSliderAdapter(getContext(), rilisMediaDataList);
                recyclerViewHero.setAdapter(heroSliderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Gagal memuat database" + error.getMessage());
                    Toast.makeText(requireContext(), "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
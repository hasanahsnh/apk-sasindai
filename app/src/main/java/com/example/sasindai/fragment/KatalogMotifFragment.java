package com.example.sasindai.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.sasindai.MasukkanMotifFormActivity;
import com.example.sasindai.R;
import com.example.sasindai.adapter.KatalogMotifListAdapter;
import com.example.sasindai.model.KatalogMotifData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KatalogMotifFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KatalogMotifFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerViewKatalogMotif;
    private KatalogMotifListAdapter adapter;
    private ArrayList<KatalogMotifData> data = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressBar progressBar;
    private LottieAnimationView animDataNotFound;
    private ImageView btnMasukkanMotif;
    private DatabaseReference db;
    private ValueEventListener dbEventListener;
    Context context = getContext();

    public KatalogMotifFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KatalogMotifFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KatalogMotifFragment newInstance(String param1, String param2) {
        KatalogMotifFragment fragment = new KatalogMotifFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_katalog_motif, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animDataNotFound = view.findViewById(R.id.animDataNotFound);

        recyclerViewKatalogMotif = view.findViewById(R.id.recyclerViewKatalogMotif);
        progressBar = view.findViewById(R.id.progressBar);
        btnMasukkanMotif = view.findViewById(R.id.btnMasukkanMotif);

        adapter = new KatalogMotifListAdapter(requireContext(), new ArrayList<>());
        recyclerViewKatalogMotif.setAdapter(adapter);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerViewKatalogMotif.setLayoutManager(staggeredGridLayoutManager);

        if (btnMasukkanMotif != null) {
            btnMasukkanMotif.setOnClickListener(v -> {
                String url = "https://form.typeform.com/to/am6kHsIO";
                String judul = "Pendapatmu soal motif";
                Intent intent = new Intent(requireContext(), MasukkanMotifFormActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("judul", judul);
                startActivity(intent);
            });
        } else {
            Log.w("Katalog Motif", "Btn saran masukkan motif tidak ditemukan");
        }

        LoadDatabase();

    }

    private void LoadDatabase() {
        if (!isAdded()) {
            Log.e("Katalog Motif Fragment", "Fragment gagal dimuat");
            return;
        }

        db = FirebaseDatabase.getInstance().getReference("katalog");
        dbEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    Log.e("Katalog Motif Fragment", "Fragment telah dimuat, lewati perubahan data");
                    return;
                }

                data.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        KatalogMotifData dataKatalog = dataSnapshot.getValue(KatalogMotifData.class);

                        if (dataKatalog != null) {
                            data.add(dataKatalog);
                        } else {
                            Log.e("Katalog Motif Fragment", "Tidak ada data untuk katalog motif yang dimuat" + dataSnapshot);
                        }

                    } catch (Exception e) {
                        Log.e("Pemuatan Data", "Error saat memuat data katalog motif" + e.getMessage());
                    }
                }

                if (!isAdded()) {
                    return;
                }

                Context context_ = getContext();
                try {
                    if (context_ != null) {
                        adapter.setData(data);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Katalog Fragment", "Gagal memuat adapter");
                    }
                } catch (Exception e) {
                    Log.e("Katalog Fragment", "Adapter error, Error: " + e.getMessage());
                }

                recyclerViewKatalogMotif.post(() -> {
                    staggeredGridLayoutManager.invalidateSpanAssignments(); // PENTING
                    recyclerViewKatalogMotif.invalidateItemDecorations();
                    recyclerViewKatalogMotif.requestLayout();
                });

                if (data.size() >= staggeredGridLayoutManager.getSpanCount()) {
                    recyclerViewKatalogMotif.setVisibility(View.VISIBLE);

                    animDataNotFound.pauseAnimation();
                    animDataNotFound.setVisibility(View.GONE);
                } else {
                    recyclerViewKatalogMotif.setVisibility(View.GONE);

                    animDataNotFound.setVisibility(View.VISIBLE);
                    animDataNotFound.setRepeatCount(LottieDrawable.INFINITE);
                    animDataNotFound.playAnimation();
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Katalog Motif Fragment", "Gagal memuat database" + error.getMessage());
                    Toast.makeText(requireContext(), "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
                }
            }
        };
        db.addValueEventListener(dbEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null && dbEventListener != null) {
            db.removeEventListener(dbEventListener);
        }
    }
}
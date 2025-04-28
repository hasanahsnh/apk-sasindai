package com.example.sasindai.fragment;

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
import android.widget.ProgressBar;
import android.widget.Toast;

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

        recyclerViewKatalogMotif = view.findViewById(R.id.recyclerViewKatalogMotif);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new KatalogMotifListAdapter(requireContext(), new ArrayList<>());
        recyclerViewKatalogMotif.setAdapter(adapter);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerViewKatalogMotif.setLayoutManager(staggeredGridLayoutManager);

        LoadDatabase();

    }

    private void LoadDatabase() {
        if (!isAdded()) {
            Log.e("Katalog Motif Fragment", "Fragment gagal dimuat");
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("katalog");
        db.addValueEventListener(new ValueEventListener() {
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

                adapter.setData(data);
                adapter.notifyDataSetChanged();

                recyclerViewKatalogMotif.post(() -> {
                    staggeredGridLayoutManager.invalidateSpanAssignments(); // PENTING
                    recyclerViewKatalogMotif.invalidateItemDecorations();
                    recyclerViewKatalogMotif.requestLayout();
                });

                if (data.size() >= staggeredGridLayoutManager.getSpanCount()) {
                    recyclerViewKatalogMotif.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewKatalogMotif.setVisibility(View.GONE);
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
        });
    }

    public void updateData(ArrayList<KatalogMotifData> data) {
        if (adapter == null) return;
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
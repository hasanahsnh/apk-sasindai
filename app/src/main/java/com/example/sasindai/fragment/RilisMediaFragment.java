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

import com.airbnb.lottie.L;
import com.example.sasindai.R;
import com.example.sasindai.adapter.RilisMediaAdapter;
import com.example.sasindai.model.RilisMediaData;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RilisMediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RilisMediaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvRilisMedia;
    private RilisMediaAdapter adapter;
    private final ArrayList<RilisMediaData> data = new ArrayList<>();


    public RilisMediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RilisMediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RilisMediaFragment newInstance(String param1, String param2) {
        RilisMediaFragment fragment = new RilisMediaFragment();
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
        return inflater.inflate(R.layout.fragment_rilis_media, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRilisMedia = view.findViewById(R.id.rvRilisMedia);

        adapter = new RilisMediaAdapter(requireContext(), data);
        rvRilisMedia.setAdapter(adapter);

        if (rvRilisMedia != null) {
            rvRilisMedia.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            Log.i("Rilis Media Fragment", "Recycler View rilis media tidak ditemukan");
        }

        tampilkanDaftarRilisMedia();

    }

    private void tampilkanDaftarRilisMedia() {
        if (!isAdded()) {
            Log.e("Rilis Media Fragment", "Fragment gagal dimuat");
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("berita");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        RilisMediaData dataRilisMedia = dataSnapshot.getValue(RilisMediaData.class);

                        if (dataRilisMedia != null) {
                            data.add(dataRilisMedia);
                        } else {
                            Log.e("Rilis Media Fragment", "Tidak ada data untuk hero yang dimuat" + dataSnapshot);
                        }
                    } catch (Exception e) {
                        Log.e("Rilis Media Fragment", "Data tidak ditemukan" + e.getMessage());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Rilis Media Fragment", "Fragment gagal dimuat");
                    return;
                }

                Log.e("Rilis Media Fragment, ", "Error: "  + error.getMessage());
            }
        });
    }
}
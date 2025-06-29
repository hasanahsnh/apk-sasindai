package com.example.sasindai.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sasindai.AuthHostActivity;
import com.example.sasindai.KaPasaranHostActivity;
import com.example.sasindai.KeranjangActivity;
import com.example.sasindai.R;
import com.example.sasindai.SceneActivity;
import com.example.sasindai.TransaksiActivity;
import com.example.sasindai.adapter.HeroSliderAdapter;
import com.example.sasindai.adapter.ProdukSliderAdapter;
import com.example.sasindai.adapter.RilisMediaListAdapter;
import com.example.sasindai.adapter.ShimmerRilisMediaAdapter;
import com.example.sasindai.callback.CallbackLayanan;
import com.example.sasindai.isLayanan.IsLayanan;
import com.example.sasindai.model.ProdukData;
import com.example.sasindai.model.RilisMediaData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ProdukSliderAdapter produkSliderAdapter;
    private RilisMediaListAdapter rilisMediaListAdapter;
    private ArrayList<RilisMediaData> rilisMediaDataList;
    private ArrayList<ProdukData> produkDataList;
    private RecyclerView recyclerViewHero, previewKaPasaran, previewRilisMedia, shimmerPreviewRilisMedia;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerProduk;
    private LinearLayout sejarahSasirangan, layoutFiturKaPasaran, fiturAR;
    private TextView tvLihatProduk;
    private ImageView btnGotoKeranjang, btnGotoPesanan;
    private SharedPreferences sharedPreferences;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceRilisMedia, databaseReferenceProduk, databaseReferencePreviewRilisMedia;
    private ValueEventListener rilisMediaListener, produkListener, previewRilisMediaListener;

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
        layoutFiturKaPasaran = view.findViewById(R.id.layoutFiturKaPasaran);
        previewKaPasaran = view.findViewById(R.id.previewKaPasaran);
        shimmerProduk = view.findViewById(R.id.shimmerProduk);
        tvLihatProduk = view.findViewById(R.id.tvLihatProduk);
        previewRilisMedia = view.findViewById(R.id.previewRilisMedia);
        shimmerPreviewRilisMedia = view.findViewById(R.id.shimmerPreviewRilisMedia);
        btnGotoKeranjang = view.findViewById(R.id.btnGotoKeranjang);
        fiturAR = view.findViewById(R.id.fiturAR);
        btnGotoPesanan = view.findViewById(R.id.btnGotoRiwayatPesanan);
        sejarahSasirangan = view.findViewById(R.id.sejarahSasirangan);
        // End inisial

        // Navigate to
        clickToNavigate();
        // End navigate

        // Hero
        if (recyclerViewHero != null) {
            recyclerViewHero.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rilisMediaDataList = new ArrayList<>();
        } else {
            Log.e("Beranda Fragment", "Recycler view hero gagal dimuat");
        }
        // End hero

        // Produk
        if (previewKaPasaran != null) {
            previewKaPasaran.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            produkDataList = new ArrayList<>();
        } else {
            Log.e("Beranda Fragment", "Recycler view preview ka pasaran gagal dimuat");
        }
        // End produk

        // Rilis media
        if (previewRilisMedia != null) {
            previewRilisMedia.setNestedScrollingEnabled(false);
            previewRilisMedia.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            rilisMediaDataList = new ArrayList<>();
        } else {
            Log.e("Beranda Fragment", "Recycler view preview rilis media gagal dimuat");
        }

        if (shimmerPreviewRilisMedia != null) {
            shimmerPreviewRilisMedia.setNestedScrollingEnabled(false);
            shimmerPreviewRilisMedia.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            shimmerPreviewRilisMedia.setAdapter(new ShimmerRilisMediaAdapter(4));
        }
        // End rilis media

        // Load data dari firebase (db)
        loadDataFromFirebase();

    }

    private void loadDataFromFirebase() {
        if (!isAdded()) {
            Log.e("Beranda Fragment", "Fragment gagal dimuat");
            return;
        }

        // Hero
        databaseReferenceRilisMedia = FirebaseDatabase.getInstance().getReference("berita");
        rilisMediaListener = new ValueEventListener() {
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
                            Log.e("Beranda Fragment", "Tidak ada data untuk hero yang dimuat" + dataSnapshot);
                        }

                    } catch (Exception e) {
                        Log.e("Pemuatan Data", "Error saat memuat data hero" + e.getMessage());
                    }
                }

                if (!isAdded()) {
                    return;
                }

                if (!rilisMediaDataList.isEmpty()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                } else {
                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                }

                try {
                    Context context = getContext();
                    if (context != null) {
                        heroSliderAdapter = new HeroSliderAdapter(requireContext(), rilisMediaDataList);
                        recyclerViewHero.setAdapter(heroSliderAdapter);
                        heroSliderAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Beranda Fragment", "Context null saat membuat adapter hero!");
                    }
                } catch (Exception e) {
                    Log.e("Beranda Fragment", "Adapter Hero error: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Gagal memuat database" + error.getMessage());
                    Toast.makeText(requireContext(), "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
                }
            }
        };
        databaseReferenceRilisMedia.addValueEventListener(rilisMediaListener);
        // End hero

        // Produk
        databaseReferenceProduk = FirebaseDatabase.getInstance().getReference("produk");
        produkListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Fragment telah dimuat, lewati perubahan data");
                    return;
                }

                produkDataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        ProdukData dataProduk = dataSnapshot.getValue(ProdukData.class);
                        if (dataProduk != null) {
                            produkDataList.add(dataProduk);
                        } else {
                            Log.e("Beranda Fragment", "Tidak ada data produk yang dimuat!" + dataSnapshot);
                        }

                    } catch (Exception e) {
                        Log.e("Pemuatan Data", "Error saat memuat data produk" + e.getMessage());
                    }
                }

                if (!isAdded()) {
                    return;
                }

                if (!produkDataList.isEmpty()) {
                    shimmerProduk.stopShimmer();
                    shimmerProduk.setVisibility(View.GONE);
                } else {
                    shimmerProduk.startShimmer();
                    shimmerProduk.setVisibility(View.VISIBLE);
                }

                Context context = getContext();
                try {
                    if (context != null) {
                        produkSliderAdapter = new ProdukSliderAdapter(requireContext(), produkDataList);
                        previewKaPasaran.setAdapter(produkSliderAdapter);
                        produkSliderAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Beranda Fragment", "Context null saat membuat adapter produk!");
                    }
                } catch (Exception e) {
                    Log.e("Beranda Fragment", "Adapter Produk error: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Gagal memuat database, Error: " + error.getMessage());
                }
            }
        };
        databaseReferenceProduk.addValueEventListener(produkListener);
        // End produk

        // Rilis media
        databaseReferencePreviewRilisMedia = FirebaseDatabase.getInstance().getReference("berita");
        previewRilisMediaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Fragment telah dimuat, lewati perubahan data");
                    return;
                }

                rilisMediaDataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        RilisMediaData dataRilisMedia = dataSnapshot.getValue(RilisMediaData.class);
                        if (dataRilisMedia != null) {
                            rilisMediaDataList.add(dataRilisMedia);
                        } else {
                            Log.e("Beranda Fragment", "Tidak ada data rilis media yang dimuat!" + dataSnapshot);
                        }

                    } catch (Exception e) {
                        Log.e("Pemuatan Data", "Error saat memuat data rilis media" + e.getMessage());
                    }
                }

                if (!isAdded()) {
                    return;
                }

                if (rilisMediaDataList != null) {
                    shimmerPreviewRilisMedia.setVisibility(View.GONE);
                } else {
                    shimmerPreviewRilisMedia.setVisibility(View.VISIBLE);
                }

                Context context = getContext();
                try {
                    if (context != null) {
                        rilisMediaListAdapter = new RilisMediaListAdapter(requireContext(), rilisMediaDataList);
                        previewRilisMedia.setAdapter(rilisMediaListAdapter);
                        rilisMediaListAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Beranda Fragment", "Context null saat membuat adapter rilis media!");
                    }
                } catch (Exception e) {
                    Log.e("Beranda Fragment", "Adapter rilis media error, Error: " + e.getMessage());
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    Log.e("Beranda Fragment", "Gagal memuat database" + error.getMessage());
                    Toast.makeText(requireContext(), "Gagal Memuat Data rilis media", Toast.LENGTH_SHORT).show();
                }
            }
        };
        databaseReferencePreviewRilisMedia.addValueEventListener(previewRilisMediaListener);
        // End rilis media

    }

    private void clickToNavigate() {
        sejarahSasirangan.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            IsLayanan.sejarah(getContext(), isAktif -> {
                if (!isAktif) {
                    Toast.makeText(getContext(), "Fitur Dengar Sejarah sedang tidak aktif", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

        });

        fiturAR.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            IsLayanan.arProduk(getContext(), isAktif -> {
                if (!isAktif) {
                    Toast.makeText(getContext(), "Fitur AR Produk sedang tidak aktif", Toast.LENGTH_SHORT).show();
                    return;
                }

                // misal pakai intent, pastikan context tidak null
                Intent intent = new Intent(getContext(), SceneActivity.class);
                startActivity(intent);
            });

        });

        btnGotoKeranjang.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            IsLayanan.kaPasaran(getContext(), isAktif -> {
                if (!isAktif) {
                    Toast.makeText(getContext(), "Fitur Ka Pasaran sedang tidak aktif", Toast.LENGTH_SHORT).show();
                    return;
                }

                // misal pakai intent, pastikan context tidak null
                Intent intent = new Intent(getContext(), KeranjangActivity.class);
                startActivity(intent);
            });

        });

        btnGotoPesanan.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            IsLayanan.kaPasaran(getContext(), isAktif -> {
                if (!isAktif) {
                    Toast.makeText(getContext(), "Fitur Ka Pasaran sedang tidak aktif", Toast.LENGTH_SHORT).show();
                    return;
                }

                // misal pakai intent, pastikan context tidak null
                Intent intent = new Intent(getContext(), TransaksiActivity.class);
                startActivity(intent);
            });

        });

        tvLihatProduk.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            Intent intent = new Intent(getContext(), KaPasaranHostActivity.class);
            startActivity(intent);
        });

        layoutFiturKaPasaran.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null) return;

            Intent intent = new Intent(getContext(), KaPasaranHostActivity.class);
            startActivity(intent);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseReferenceRilisMedia != null && rilisMediaListener != null) {
            databaseReferenceRilisMedia.removeEventListener(rilisMediaListener);
        }

        if (databaseReferenceProduk != null && produkListener != null) {
            databaseReferenceProduk.removeEventListener(produkListener);
        }

        if (databaseReferencePreviewRilisMedia != null && previewRilisMediaListener != null) {
            databaseReferencePreviewRilisMedia.removeEventListener(previewRilisMediaListener);
        }
    }
}
package com.example.sasindai;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.Objek3DAdapter;
import com.example.sasindai.model.Objek3DData;
import com.example.sasindai.theme.ThemeActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SceneActivity extends AppCompatActivity {
    ArFragment arSceneView;
    RecyclerView recyclerViewProduk;
    ArrayList<Objek3DData> dataList = new ArrayList<>();
    ShimmerFrameLayout shimmerPreviewObjek3d;
    Objek3DAdapter adapter;
    String selectedModelUrl = null;
    boolean isModelPlaced = false;
    DatabaseReference modelRefs;
    ValueEventListener modelListener;
    boolean isDataLoaded = false;
    ImageView btnGotoKaPasaran, btnKembaliDariAr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scene);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        arSceneView = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arSceneView);
        Toast.makeText(this, "Gerakkan ponsel perlahan ke arah lantai sampai muncul grid", Toast.LENGTH_LONG).show();

        recyclerViewProduk = findViewById(R.id.recyclerViewObjek3D);
        shimmerPreviewObjek3d = findViewById(R.id.shimmerPreviewObjek3d);
        btnGotoKaPasaran = findViewById(R.id.btnGotoKaPasaran);
        btnKembaliDariAr = findViewById(R.id.btnKembaliDariAr);

        if (btnGotoKaPasaran != null) {
            btnGotoKaPasaran.setOnClickListener(v -> {
                Intent intent = new Intent(this, KaPasaranHostActivity.class);
                startActivity(intent);
            });
        } else {
            Log.w("btnGotoKaPasaran", "btnGotoKaPasaran tidak ditemukan");
        }

        if (btnKembaliDariAr != null) {
            btnKembaliDariAr.setOnClickListener(v -> {
                finish();
            });
        } else {
            Log.w("btnKembaliDariAr", "btnKembaliDariAr tidak ditemukan");
        }

        shimmerPreviewObjek3d.setVisibility(View.VISIBLE);
        recyclerViewProduk.setVisibility(View.GONE);

        recyclerViewProduk.setEnabled(false);
        recyclerViewProduk.setAlpha(0.5f);

        arSceneView.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (isModelPlaced) {
                Toast.makeText(this, "Model sudah ditempatkan atau tunggu pemrosesan model", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedModelUrl == null || selectedModelUrl.isEmpty()) {
                Toast.makeText(this, "Pilih model terlebih dahulu dari daftar", Toast.LENGTH_SHORT).show();
                return;
            }

            isModelPlaced = true;

            Uri modelUri = Uri.parse(selectedModelUrl);

            ModelRenderable.builder()
                    .setSource( this, modelUri)
                    .setIsFilamentGltf(true)
                    .build()
                    .thenAccept(modelRenderable -> {
                        // Tambahkan lighting jika perlu
                        Light light = Light.builder(Light.Type.DIRECTIONAL)
                                .setColor(new Color(1f, 1f, 1f))
                                .setIntensity(100000)
                                .build();
                        Node lightNode = new Node();
                        lightNode.setLight(light);
                        lightNode.setParent(arSceneView.getArSceneView().getScene());

                        // Buat anchor dan pasang model
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arSceneView.getArSceneView().getScene());

                        TransformableNode modelNode = new TransformableNode(arSceneView.getTransformationSystem());
                        modelNode.setParent(anchorNode);
                        modelNode.setRenderable(modelRenderable);
                        modelNode.select();
                    })
                    .exceptionally(throwable -> {
                        isModelPlaced = false;
                        Toast.makeText(this, "Gagal memuat model 3D", Toast.LENGTH_SHORT).show();
                        Log.e("SceneActivity", "Error load model 3D: ", throwable);
                        return null;
                    });
        });

        if (recyclerViewProduk != null) {
            recyclerViewProduk.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            Exception e = new NullPointerException("RecyclerView 'recyclerViewProduk' is null");
            Log.e("SceneActivity", "Gagal set recyclerview", e);
        }

        loadDataFromDatabase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadDataFromDatabase() {
        modelRefs = FirebaseDatabase.getInstance().getReference("objek3d");
        modelListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        Objek3DData data = dataSnapshot.getValue(Objek3DData.class);
                        if (data != null) {
                            dataList.add(data);
                        } else {
                            Log.e("SceneActivity", "Tidak ada data untuk objek 3d yang dimuat" + dataSnapshot);
                        }
                    } catch (Exception e) {
                        Log.e("SceneActivity", "Error saat memuat data objek 3d" + e.getMessage());
                    }
                }

                if (dataList != null) {
                    shimmerPreviewObjek3d.setVisibility(View.GONE);
                    recyclerViewProduk.setVisibility(View.VISIBLE);
                } else {
                    shimmerPreviewObjek3d.setVisibility(View.VISIBLE);
                    recyclerViewProduk.setVisibility(View.GONE);
                }

                adapter = new Objek3DAdapter(SceneActivity.this, dataList, objek3DData -> {
                    if (!isDataLoaded) {
                        Toast.makeText(SceneActivity.this, "Data belum siap, harap tunggu sinkronisasi...", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    isModelPlaced = false;
                    selectedModelUrl = objek3DData.getGlbUrl();
                    List<Node> toRemove = new ArrayList<>();
                    for (Node node : arSceneView.getArSceneView().getScene().getChildren()) {
                        if (node instanceof AnchorNode) {
                            toRemove.add(node);
                        }
                    }

                    for (Node node : toRemove) {
                        fadeOutAndRemove(node, null);
                    }
                    Toast.makeText(SceneActivity.this, "Model dipilih, silakan tap di area kosong!", Toast.LENGTH_SHORT).show();
                });

                recyclerViewProduk.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                recyclerViewProduk.setEnabled(true);
                recyclerViewProduk.setAlpha(1f);
                isDataLoaded = true;



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SceneActivity", "Gagal memuat database" + error.getMessage());
                Toast.makeText(SceneActivity.this, "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
            }
        };
        modelRefs.addValueEventListener(modelListener);
    }

    private void fadeOutAndRemove(Node node, Runnable onFinish) {
        node.setRenderable(null); // Sembunyikan renderable
        node.setEnabled(false);   // Nonaktifkan node dari scene
        arSceneView.getArSceneView().getScene().removeChild(node); // Hapus node dari scene
        if (onFinish != null) onFinish.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (modelRefs != null && modelListener != null) {
            modelRefs.removeEventListener(modelListener);
        }
    }
}
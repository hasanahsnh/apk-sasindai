package com.example.sasindai;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sasindai.adapter.Objek3DAdapter;
import com.example.sasindai.fragment.Objek3DData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scene);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.maroon)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        arSceneView = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arSceneView);
        recyclerViewProduk = findViewById(R.id.recyclerViewObjek3D);
        shimmerPreviewObjek3d = findViewById(R.id.shimmerPreviewObjek3d);

        shimmerPreviewObjek3d.setVisibility(View.VISIBLE);
        recyclerViewProduk.setVisibility(View.GONE);

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

                        Node modelNode = new Node();
                        modelNode.setParent(anchorNode);
                        modelNode.setRenderable(modelRenderable);
                        modelNode.setLocalPosition(new Vector3(0f, 0f, 0f));
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("objek3d");
        databaseReference.addValueEventListener(new ValueEventListener() {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SceneActivity", "Gagal memuat database" + error.getMessage());
                Toast.makeText(SceneActivity.this, "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fadeOutAndRemove(Node node, Runnable onFinish) {
        node.setRenderable(null); // Sembunyikan renderable
        node.setEnabled(false);   // Nonaktifkan node dari scene
        arSceneView.getArSceneView().getScene().removeChild(node); // Hapus node dari scene
        if (onFinish != null) onFinish.run();
    }
}
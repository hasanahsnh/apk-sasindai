package com.example.sasindai.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.credentials.ClearCredentialStateRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sasindai.AuthHostActivity;
import com.example.sasindai.MainHostActivity;
import com.example.sasindai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.C;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AkunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AkunFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btnMasuk, btnKeluar, btnSemuaFitur;
    TextView tvSelamatDatang, emailTerverifikasi, kirimEmailVerifikasi;
    ImageView imgEmailChecked;
    LinearLayout layoutVerifikasiEmail;
    ScrollView uiFragmentAkun;
    ProgressBar progressBarAkunFragment;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    public AkunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AkunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AkunFragment newInstance(String param1, String param2) {
        AkunFragment fragment = new AkunFragment();
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

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_akun, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cek status default masuk pengguna
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Inisialisasi
        btnMasuk = view.findViewById(R.id.btnMasuk);
        btnKeluar = view.findViewById(R.id.btnKeluar);
        btnSemuaFitur = view.findViewById(R.id.btnSemuaFitur);
        tvSelamatDatang = view.findViewById(R.id.tvSelamatDatang);
        emailTerverifikasi = view.findViewById(R.id.emailTerverifikasi);
        layoutVerifikasiEmail = view.findViewById(R.id.layoutVerifikasiEmail);
        imgEmailChecked = view.findViewById(R.id.imgEmailChecked);
        kirimEmailVerifikasi = view.findViewById(R.id.kirimEmailVerifikasi);
        uiFragmentAkun = view.findViewById(R.id.uiFragmentAkun);
        progressBarAkunFragment = view.findViewById(R.id.progressBarAkunFragment);

        // Perbarui tampilan
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBarAkunFragment.setVisibility(View.GONE);
                        uiFragmentAkun.setVisibility(View.VISIBLE);

                        if(currentUser.isEmailVerified()) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(currentUser.getUid());
                            userRef.child("emailIsVerified").setValue(true);
                        }

                        updateUI(firebaseAuth.getCurrentUser());
                        
                    } else {
                        Log.e("AkunFragment", "Gagal reload user: " + task.getException());
                        progressBarAkunFragment.setVisibility(View.GONE);
                        uiFragmentAkun.setVisibility(View.VISIBLE);
                        updateUI(null);

                    }
                });
            } else {
                progressBarAkunFragment.setVisibility(View.GONE);
                uiFragmentAkun.setVisibility(View.VISIBLE);
                updateUI(null);
            }
        };

        btnMasuk.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AuthHostActivity.class);
            startActivity(intent);
        });

        btnKeluar.setOnClickListener(v -> {
            firebaseAuth.signOut();
            updateUI(null);
            Log.i("Button Keluar", "Pengguna menekan button keluar");
            Toast.makeText(requireContext(), "Berhasil keluar!", Toast.LENGTH_SHORT).show();
        });

    }


    // Perbarui tampilan
    private void updateUI(FirebaseUser currentUser) {
        Log.d("Akun Fragment", "currentUser: " + (currentUser != null));

        if (currentUser != null) {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
            btnMasuk.setVisibility(View.GONE);
            btnKeluar.setVisibility(View.VISIBLE);
            btnSemuaFitur.setVisibility(View.VISIBLE);

            // Memperoleh data akunnya
            String email = currentUser.getEmail();

            // Pengecekan null menghindari error
            emailTerverifikasi.setText(email != null ? email : "Email tidak tersedia");

            // Apakah email terverifikasi (uncheck)
            boolean emailVerified = currentUser.isEmailVerified();

            if (emailVerified) {
                layoutVerifikasiEmail.setVisibility(View.GONE);
                imgEmailChecked.setVisibility(View.VISIBLE);

                btnSemuaFitur.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Selamat menikmati semua fitur", Toast.LENGTH_SHORT).show();
                });
            } else {
                layoutVerifikasiEmail.setVisibility(View.VISIBLE);
                imgEmailChecked.setVisibility(View.GONE);
                btnSemuaFitur.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Verifikasi akun terlebih dahulu", Toast.LENGTH_SHORT).show();
                });
                kirimEmailVerifikasi.setOnClickListener(v -> {
                    KirimEmailVerifikasi();
                });
            }
        } else {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
            btnMasuk.setVisibility(View.VISIBLE);
            btnKeluar.setVisibility(View.GONE);
            layoutVerifikasiEmail.setVisibility(View.GONE);
            btnSemuaFitur.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Silakan masuk terlebih dahulu!", Toast.LENGTH_SHORT).show();
            });

            emailTerverifikasi.setText("Silakan masuk untuk akses semua fitur!");

        }
    }

    private void KirimEmailVerifikasi() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Mengirim Email", "Email verifikasi dikirim");
                            Toast.makeText(requireContext(), "Email verifikasi telah dikirim. Silakan cek email Anda", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("Mengirim Email", "Gagal mengirim email verifikasi", task.getException());
                            Toast.makeText(requireContext(), "Gagal mengirim email verifikasi", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("AkunFragment", "Gagal mengirim email: User tidak ditemukan");
            Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

    }

    private void signOut() {
        firebaseAuth.signOut();
        clearLoginStatus();

        Intent intent = new Intent(requireActivity(), MainHostActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void clearLoginStatus() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
package com.example.sasindai.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
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
import com.example.sasindai.MasukkanMotifFormActivity;
import com.example.sasindai.ProfileActivity;
import com.example.sasindai.R;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    Button btnMasuk;
    TextView tvSelamatDatang, emailTerverifikasi, kirimEmailVerifikasi;
    ImageView imgEmailChecked;
    LinearLayout layoutVerifikasiEmail, btnKeluar, lihatProfile, btnIsiKuesionerKepuasan;
    ScrollView uiFragmentAkun;
    ProgressBar progressBarAkunFragment;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    ConstraintLayout lengkapiTelp;
    View viewProfile;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Inisialisasi
        btnMasuk = view.findViewById(R.id.btnMasuk);
        btnKeluar = view.findViewById(R.id.btnKeluar);
        tvSelamatDatang = view.findViewById(R.id.tvSelamatDatang);
        emailTerverifikasi = view.findViewById(R.id.emailTerverifikasi);
        layoutVerifikasiEmail = view.findViewById(R.id.layoutVerifikasiEmail);
        imgEmailChecked = view.findViewById(R.id.imgEmailChecked);
        kirimEmailVerifikasi = view.findViewById(R.id.kirimEmailVerifikasi);
        uiFragmentAkun = view.findViewById(R.id.uiFragmentAkun);
        progressBarAkunFragment = view.findViewById(R.id.progressBarAkunFragment);
        lihatProfile = view.findViewById(R.id.lihatProfile);
        lengkapiTelp = view.findViewById(R.id.lengkapiTelp);
        viewProfile = view.findViewById(R.id.view);
        btnIsiKuesionerKepuasan = view.findViewById(R.id.btnIsiKuesionerKepuasan);

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

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        try {
                                            String noTelp = snapshot.child("noTelp").getValue(String.class);
                                            if (noTelp != null) {
                                                lengkapiTelp.setVisibility(View.GONE);
                                            } else {
                                                lengkapiTelp.setVisibility(View.VISIBLE);
                                            }
                                        } catch (Exception e) {
                                            Log.e("Akun Fragment", "error: " + e.getMessage());
                                        }

                                    } else {
                                        lengkapiTelp.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    lengkapiTelp.setVisibility(View.VISIBLE);
                                }
                            });
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

        if (btnIsiKuesionerKepuasan != null) {
            btnIsiKuesionerKepuasan.setOnClickListener(v -> {
                String url = "https://sasindai.sascode.my.id/form-kepuasan";
                String judul = "Kuesioner Kepuasan Pengguna";
                Intent intent = new Intent(requireContext(), MasukkanMotifFormActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("judul", judul);
                startActivity(intent);
            });
        } else {
            Log.w("Katalog Motif", "Btn saran masukkan motif tidak ditemukan");
        }

        btnMasuk.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AuthHostActivity.class);
            startActivity(intent);
        });

        btnKeluar.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                    .setMessage("Apakah Anda yakin ingin keluar?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        firebaseAuth.signOut();
                        updateUI(null);
                        Toast.makeText(requireContext(), "Berhasil keluar!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                Typeface font = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
                Typeface fontReguler = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold);
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if (positiveBtn != null) {
                    positiveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.maroon));
                    positiveBtn.setTypeface(font);
                }

                if (negativeBtn != null) {
                    negativeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.maroon));
                    negativeBtn.setTypeface(font);
                }

                if (messageView != null) {
                    messageView.setTypeface(fontReguler);
                    messageView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            });

            alertDialog.show();
        });

        if (lihatProfile != null) {
            lihatProfile.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("Akun Fragment", "Dari btnGotoKeranjang, btn goto keranjang gagal dimuat atau bernilai null");
        }

    }

    // Perbarui tampilan
    private void updateUI(FirebaseUser currentUser) {
        Log.d("Akun Fragment", "currentUser: " + (currentUser != null));

        if (currentUser != null) {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
            btnMasuk.setVisibility(View.GONE);
            btnKeluar.setVisibility(View.VISIBLE);
            lengkapiTelp.setVisibility(View.GONE);
            viewProfile.setVisibility(View.VISIBLE);
            lihatProfile.setVisibility(View.VISIBLE);

            // Memperoleh data akunnya
            String email = currentUser.getEmail();

            // Pengecekan null menghindari error
            emailTerverifikasi.setText(email != null ? email : "Email tidak tersedia");

            // Apakah email terverifikasi (uncheck)
            boolean emailVerified = currentUser.isEmailVerified();

            if (emailVerified) {
                layoutVerifikasiEmail.setVisibility(View.GONE);
                imgEmailChecked.setVisibility(View.VISIBLE);
            } else {
                layoutVerifikasiEmail.setVisibility(View.VISIBLE);
                imgEmailChecked.setVisibility(View.GONE);
                kirimEmailVerifikasi.setOnClickListener(v -> {
                    KirimEmailVerifikasi();
                });
            }
        } else {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
            btnMasuk.setVisibility(View.VISIBLE);
            btnKeluar.setVisibility(View.GONE);
            layoutVerifikasiEmail.setVisibility(View.GONE);
            emailTerverifikasi.setText("Silakan masuk untuk akses semua fitur!");
            lengkapiTelp.setVisibility(View.GONE);
            imgEmailChecked.setVisibility(View.GONE);
            lihatProfile.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
        }
    }

    private void KirimEmailVerifikasi() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Mengirim Email", "Email verifikasi dikirim");
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Email verifikasi telah dikirim. Silakan cek email Anda", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Exception e = task.getException();
                            Log.e("Mengirim Email", "Gagal mengirim email verifikasi", task.getException());

                            if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                                Toast.makeText(requireContext(), "Sesi login kedaluwarsa. Silakan login ulang.", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(requireContext(), "Terlalu banyak permintaan. Coba beberapa saat lagi.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(), "Gagal mengirim email verifikasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
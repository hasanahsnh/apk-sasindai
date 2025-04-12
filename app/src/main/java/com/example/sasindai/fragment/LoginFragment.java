package com.example.sasindai.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sasindai.AuthHostActivity;
import com.example.sasindai.MainHostActivity;
import com.example.sasindai.R;
import com.example.sasindai.model.AlamatData;
import com.example.sasindai.model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    ImageView btnSignInGoogle;
    SharedPreferences sharedPreferences;
    Button btnSignIn;
    EditText etEmail, etPassword;

    final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleSignIn(result.getData());
                }
            }
    );

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        // Get instance (Firebase Auth)
        firebaseAuth = FirebaseAuth.getInstance();

        // Integrasi login dengan google
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = ((AuthHostActivity) requireActivity()).getSharedPreferencesInstance();

        // Inisialisasi widget
        btnSignInGoogle = view.findViewById(R.id.btnSignInGoogle);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        // Perbarui tampilan
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateUI(currentUser);
        };

        // Pengguna masuk menggunakan metode google
        btnSignInGoogle.setOnClickListener(v -> {
            signInWithGoogle();
        });
        
        // Pengguna masuk menggunakan metode email/password
        btnSignIn.setOnClickListener(v -> {
            emailPasswordSignIn();
        });

    }

    // Email/Password Method

    // Memulai proses login
    private void emailPasswordSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login dengan Email/Password", "Berhasil mendapatkan getCurrentUser, cek role");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                String uid = user.getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String role = snapshot.child("role").getValue(String.class);
                                            Log.d("Login Fragment", "Log Role: " + role);
                                            if ("ROLE_REGULER".equals(role)) {
                                                Log.d("Login Fragment", "User reguler berhasil login, akses diizinkan");
                                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                                                updateUI(user);
                                            } else {
                                                Log.w("Login Fragment", "Bukan user reguler, akses dibatalkan. Role: " + role);
                                                firebaseAuth.signOut();
                                                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
                                                Toast.makeText(requireContext(), "Autentikasi gagal", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            firebaseAuth.signOut();
                                            Toast.makeText(requireContext(), "Data pengguna tidak ditemukan di database", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Login", "Database error: " + error.getMessage());
                                        Toast.makeText(requireContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Log.w("Login dengan Email/Password", "Gagal masuk dengan email", task.getException());
                            Toast.makeText(requireContext(), "Periksa kembali E-Mail atau Password Anda",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    // End Email/Password Method


    // Google Methode

    // Memulai proses login
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    // Menangani proses auth saat login
    private void handleSignIn(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.w("Login Fragment", "Google sign in gagal", e);
        }
    }


    // Autentikasi dengan Firebase menggunakan kredensial Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login Fragment", "Sign in dengan credential berhasil");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        if (user != null) {
                            saveUserToFirebase(user);
                            updateUI(firebaseAuth.getCurrentUser());
                        } else {
                            Log.w("Register Fragment", "User masih null setelah login");
                            Toast.makeText(requireContext(), "Terjadi kesalahan saat login!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.w("Login Fragment", "Sign in dengan credential gagal", task.getException());
                        Toast.makeText(requireContext(), "Login Gagal!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // End Google Method

    // Apakah pengguna saat ini sedang login
    private void updateUI(FirebaseUser currentUser) {
        Log.d("Login Fragment", "currentUser: " + (currentUser != null));

        if (currentUser != null && sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(requireActivity(), MainHostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
        }
    }

    // Menyimpan data pengguna setelah berhasil login (testing fungsi) metode google
    private void saveUserToFirebase(FirebaseUser user) {
        String namaLengkap = user.getDisplayName() != null ? user.getDisplayName() : "Tamu";
        saveUserToFirebase(user, namaLengkap);
    }

    // Menyimpan data pengguna setelah berhasil login (testing fungsi)
    private void saveUserToFirebase(FirebaseUser user, String namaLengkap) {
        if (user == null) return;

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String uid = user.getUid();
        String email = user.getEmail() != null ? user.getEmail() : "Tamu";
        String phone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";
        String role = "ROLE_REGULER";

        // Debug
        Log.d("Simpan User", "Mengambil uid user: " + uid);

        UserData newUser = new UserData(uid, email, namaLengkap, phone, role, "google", new AlamatData(), false);

        usersRef.child(uid).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "User berhasil disimpan dengan role: " + role);
            } else {
                Log.e("Firebase", "Gagal menyimpan user ke tabel users", task.getException());
            }
        });

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
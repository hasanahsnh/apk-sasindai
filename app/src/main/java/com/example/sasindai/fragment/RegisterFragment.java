package com.example.sasindai.fragment;

import static android.app.ProgressDialog.show;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText etEmailDaftar, etNamaLengkapDaftar, etPasswordDaftar;
    Button btnSignUp;
    ImageView btnSignUpGoogle;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    GoogleSignInOptions googleSignUpOptions;
    GoogleSignInClient googleSignUpClient;
    SharedPreferences sharedPreferences;
    final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleSignUp(result.getData());
                }
            }
    );

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        googleSignUpOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        googleSignUpClient = GoogleSignIn.getClient(requireContext(), googleSignUpOptions);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = ((AuthHostActivity) requireActivity()).getSharedPreferencesInstance();

        // Inisialisasi widget
        etEmailDaftar = view.findViewById(R.id.etEmailDaftar);
        etNamaLengkapDaftar = view.findViewById(R.id.etNamaLengkapDaftar);
        etPasswordDaftar = view.findViewById(R.id.etPasswordDaftar);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnSignUpGoogle = view.findViewById(R.id.btnSignUpGoogle);

        // Perbarui tampilan
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateUI(currentUser);
        };

        // Pengguna mendaftar melalui email/password
        btnSignUp.setOnClickListener(v -> {
            emailPasswordSignUp();
        });

        // Pengguna masuk/mendaftar melalui google
        btnSignUpGoogle.setOnClickListener(v -> {
            signUpWithGoogle();
        });

    }

    // Email/Password method

    // Memulai proses sign up (uncheck)
    private void emailPasswordSignUp() {

        String email = etEmailDaftar.getText().toString().trim();
        String password = etPasswordDaftar.getText().toString().trim();
        String namaLengkap = etNamaLengkapDaftar.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Masukkan E-Mail atau Password terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }

        if (password.length() <= 8) {
            Toast.makeText(requireContext(), "Masukkan kata sandi minimal 8 karakter!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Daftar User", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            saveUserToFirebase(user, "email/password", namaLengkap);
                            updateUI(user);
                        } else {
                            Log.w("Daftar User", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


    }

    // End Email/password method

    // Google Sign Up/In Method

    // Memulai proses login
    private void signUpWithGoogle() {
        Intent signInIntent = googleSignUpClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    // Menangani proses auth saat login
    private void handleSignUp(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.w("Register Fragment", "Google sign in gagal", e);
        }
    }

    // Autentikasi dengan Firebase menggunakan kredensial Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d("Register Fragment", "Sign Up dengan credential berhasil");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        if (user != null) {
                            saveUserToFirebase(user);
                            updateUI(user);
                        } else {
                            Log.w("Register Fragment", "User masih null setelah login");
                            Toast.makeText(requireContext(), "Terjadi kesalahan saat login!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Register Fragment", "Sign Up dengan credential gagal", task.getException());
                        Toast.makeText(requireContext(), "Login Gagal!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // End Google Sign Up/In Method

    // Apakah pengguna saat ini sedang login (uncheck untuk metode email/password)
    private void updateUI(FirebaseUser currentUser) {
        Log.d("Register Fragment", "currentUser: " + (currentUser != null));

        if (currentUser != null) {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
            Intent intent = new Intent(requireActivity(), MainHostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
        }
    }

    // Menyimpan data nama lengkap dengan metode auth google
    private void saveUserToFirebase(FirebaseUser user) {
        String namaLengkap = user.getDisplayName() != null ? user.getDisplayName() : "Tamu";
        saveUserToFirebase(user, "google", namaLengkap);
    }

    // Menyimpan data pengguna setelah berhasil login (testing fungsi) manual
    private void saveUserToFirebase(FirebaseUser user, String authMethod, String namaLengkap) {
        if (user == null) return;

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String uid = user.getUid();
        String email = user.getEmail() != null ? user.getEmail() : "Tamu";
        String role = "ROLE_REGULER";
        boolean emailVerified = user.isEmailVerified();

        // Debug
        Log.d("Simpan User", "Mengambil uid user: " + uid);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Token", "Gagal ambil token", task.getException());
                        return;
                    }

                    String fcmToken = task.getResult();
                    Log.d("FCM Token", "Token FCM: " + fcmToken);

                    // Simpan semua data ke Firebase Realtime Database
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("uid", uid);
                    updateData.put("email", email);
                    updateData.put("emailIsVerified", emailVerified);
                    updateData.put("namaLengkap", namaLengkap);
                    updateData.put("authMethod", authMethod); // sesuai parameter
                    updateData.put("role", role);
                    updateData.put("device_token", fcmToken); // disimpan di sini

                    usersRef.child(uid).updateChildren(updateData).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            Log.d("Firebase", "User berhasil disimpan dengan role: " + role);
                        } else {
                            Log.e("Firebase", "Gagal menyimpan user ke tabel users", saveTask.getException());
                        }
                    });
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
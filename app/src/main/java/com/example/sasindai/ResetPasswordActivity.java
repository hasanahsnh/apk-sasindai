package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText etResetPassword;
    Button btnKirimEmailReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Set warna nav bar

        etResetPassword = findViewById(R.id.etResetPassword);
        btnKirimEmailReset = findViewById(R.id.btnKirimEmailReset);

        btnKirimEmailReset.setOnClickListener(v -> {
            String email = etResetPassword.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Email reset password telah dikirim. Silakan cek email Anda", Toast.LENGTH_LONG).show();
                            Log.d("Reset Password", "Email reset dikirim ke: " + email);
                        } else {
                            Exception e = task.getException();
                            Log.e("Reset Password", "Gagal mengirim email reset", e);

                            if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(this, "Email tidak ditemukan atau belum terdaftar", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(this, "Terlalu banyak permintaan. Coba lagi nanti.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Gagal mengirim email reset: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
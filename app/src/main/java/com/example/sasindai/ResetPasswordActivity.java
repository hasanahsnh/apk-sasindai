package com.example.sasindai;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;
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

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

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
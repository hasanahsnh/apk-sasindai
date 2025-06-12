package com.example.sasindai;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sasindai.theme.ThemeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView aturEmail, aturNama, aturTelepon;
    FirebaseUser currentUser;
    String uid;
    ProgressBar progressBar;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // import tema
        ThemeActivity.applyTheme(this);
        // end import tema

        aturEmail = findViewById(R.id.aturEmail);
        aturNama = findViewById(R.id.aturNama);
        aturTelepon = findViewById(R.id.aturTelepon);
        progressBar = findViewById(R.id.progressBarProfile);
        container = findViewById(R.id.containerProfile);

        // dapatkan data profie
        loadProfile();

        // Perbarui data profile
        updateProfile();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateProfile() {

        // Update telepon
        aturTelepon.setOnClickListener(v -> {
            // Ambil nilai sebelumnya dari TextView
            String currentNoTelp = aturTelepon.getText().toString();

            // Buat EditText untuk input
            EditText input = new EditText(ProfileActivity.this);
            Typeface font = ResourcesCompat.getFont(this, R.font.poppins_medium);
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setText(currentNoTelp);
            input.setHint("Masukkan No. Telepon");
            input.setSelection(input.getText().length());
            input.setTypeface(font);
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            input.setTextColor(ContextCompat.getColor(this, R.color.black));
            input.setHintTextColor(ContextCompat.getColor(this, R.color.abu_terang));

            // Tampilkan Dialog
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Ubah Nomor Telepon")
                    .setMessage("Silakan perbarui nomor telepon Anda.")
                    .setView(input)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String newNoTelp = input.getText().toString().trim();

                        if (!newNoTelp.isEmpty()) {
                            // Simpan ke Firebase
                            FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("noTelp")
                                    .setValue(newNoTelp)
                                    .addOnSuccessListener(aVoid -> {
                                        aturTelepon.setText(newNoTelp);
                                        Toast.makeText(ProfileActivity.this, "Nomor telepon diperbarui", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ProfileActivity.this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Nomor telepon tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                Typeface fontAlert = ResourcesCompat.getFont(this, R.font.poppins_medium);
                Typeface fontReguler = ResourcesCompat.getFont(this, R.font.poppins_bold);
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if (positiveBtn != null) {
                    positiveBtn.setTextColor(ContextCompat.getColor(this, R.color.maroon));
                    positiveBtn.setTypeface(fontAlert);
                }

                if (negativeBtn != null) {
                    negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.maroon));
                    negativeBtn.setTypeface(fontAlert);
                }

                if (messageView != null) {
                    messageView.setTypeface(fontReguler);
                    messageView.setTextColor(ContextCompat.getColor(this, R.color.black));
                    messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            });

            alertDialog.show();

        });
    }

    private void profile() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    String namaLengkap = snapshot.child("namaLengkap").getValue(String.class);
                    String noTelp = snapshot.child("noTelp").getValue(String.class);

                    boolean isEmailValid = email != null && !email.trim().isEmpty();
                    boolean isNamaValid = namaLengkap != null && !namaLengkap.trim().isEmpty();
                    boolean isTelpValid = noTelp != null && !noTelp.trim().isEmpty();

                    if (isEmailValid) {
                        aturEmail.setText(email);
                    }

                    if (isNamaValid) {
                        aturNama.setText(namaLengkap);
                    }

                    if (isTelpValid) {
                        aturTelepon.setText(noTelp);
                    }

                    progressBar.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Profile Actv", "Gagal mengambil data: " + error.getMessage());
                }
            });
        }
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        profile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
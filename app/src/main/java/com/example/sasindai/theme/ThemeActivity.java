package com.example.sasindai.theme;

import android.app.Activity;
import android.view.Window;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.example.sasindai.R;

public class ThemeActivity {
    public static void applyTheme(Activity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Set default mode gelap
        Window window = activity.getWindow(); // Mendapatkan objek window
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.putih)); // Set warna status bar
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.black)); // Set warna nav bar
    }
}

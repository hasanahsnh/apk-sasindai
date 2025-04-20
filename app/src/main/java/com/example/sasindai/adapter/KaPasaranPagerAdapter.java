package com.example.sasindai.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sasindai.fragment.ProdukFragment;

public class KaPasaranPagerAdapter extends FragmentStateAdapter {
    public KaPasaranPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ProdukFragment();
        } else if (position == 1) {
            return new ProdukFragment();
        } else if (position == 2) {
            return new ProdukFragment();
        } else if (position == 3) {
            return new ProdukFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

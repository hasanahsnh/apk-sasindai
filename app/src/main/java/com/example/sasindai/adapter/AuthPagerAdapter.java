package com.example.sasindai.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sasindai.fragment.LoginFragment;
import com.example.sasindai.fragment.RegisterFragment;

public class AuthPagerAdapter extends FragmentStateAdapter {


    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        } else if (position == 1) {
            return new RegisterFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

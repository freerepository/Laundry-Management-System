package com.example.laundrymanagementghy.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.laundrymanagementghy.Activity.FirstFragment;
import com.example.laundrymanagementghy.Activity.SecondFragment;
import com.example.laundrymanagementghy.Activity.ThreeFragment;
import com.example.laundrymanagementghy.Amodel.UserDataModel;

public class ViewPagerAdapter extends FragmentStateAdapter {
    String laundryid;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String laundryId) {
        super(fragmentActivity);
        this.laundryid = laundryId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                return FirstFragment.newInstance(laundryid);
            case 1:
                return SecondFragment.newInstance(laundryid);
            case 2:
                return ThreeFragment.newInstance(laundryid);
            default:
                fragment = new FirstFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("id",laundryid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

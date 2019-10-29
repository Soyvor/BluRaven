package com.exuberant.bluraven.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.exuberant.bluraven.fragments.CameraFragment;
import com.exuberant.bluraven.fragments.HistoryFragment;
import com.exuberant.bluraven.fragments.HomeFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new CameraFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new HistoryFragment();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3;
    }
}

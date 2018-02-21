package com.wurfel.yaqeen.inst.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.wurfel.yaqeen.inst.Fragments.HomeFragment;
import com.wurfel.yaqeen.inst.Fragments.ProfileFragment;
import com.wurfel.yaqeen.inst.Fragments.SectionsFragment;

import java.util.ArrayList;

/**
 * Created by Farhan Ijaz on 2/12/2018.
 */

public class BottomNavViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public BottomNavViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
//        fragments.add(homeFragment.newInstance(0));
        fragments.add(new HomeFragment());
        fragments.add(new SectionsFragment());
        fragments.add(new ProfileFragment());
//        fragments.add(sectionsFragment.newInstance(1));
//        fragments.add(profileFragment.newInstance(2));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}

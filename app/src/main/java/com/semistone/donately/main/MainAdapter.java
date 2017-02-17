package com.semistone.donately.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by semistone on 2017-02-15.
 */

public class MainAdapter extends FragmentStatePagerAdapter {

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return FragmentHelper.beneficiaries.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ContentFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentHelper.beneficiaries[position];
    }
}

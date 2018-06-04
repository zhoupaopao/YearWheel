package com.example.a13126.yearwheel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 13126 on 2018/5/30.
 */

public class ViewpagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment>fragments;
    private ArrayList<String>titlelist;
    public ViewpagerAdapter(FragmentManager fm, ArrayList<Fragment>fragments,ArrayList<String>titlelist) {
        super(fm);
        this.fragments=fragments;
        this.titlelist=titlelist;
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
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }
}

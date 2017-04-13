package com.orangesword.fermchamber.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Locale;

/**
 * Created by benjamin.bartels on 5/27/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Context mContext;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new TempsFragment();
            case 1:
                // Games fragment activity
                return new GraphFragment();
        }

        return null;

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getResources().getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
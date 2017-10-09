package com.gmail.fomichov.m.youtubeanalytics.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.fragments.ChannelCompare;
import com.gmail.fomichov.m.youtubeanalytics.fragments.ChannelInfo;
import com.gmail.fomichov.m.youtubeanalytics.fragments.ChannelSort;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public MyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChannelInfo.newInstance(false);
            case 1:
                return ChannelCompare.newInstance(false);
            case 2:
                return ChannelSort.newInstance(false);
            case 3:
                return ChannelInfo.newInstance(true);
            case 4:
                return ChannelCompare.newInstance(true);
            case 5:
                return ChannelSort.newInstance(true);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.titleGlobalInformation);
            case 1:
                return context.getString(R.string.titleCompareGlobalInformation);
            case 2:
                return context.getString(R.string.titleSortChannelsData);
            case 3:
                return context.getString(R.string.titleMediaResonance);
            case 4:
                return context.getString(R.string.titleCompareMediaResonance);
            case 5:
                return context.getString(R.string.titleSortMediaResonance);
        }
        return null;
    }
}

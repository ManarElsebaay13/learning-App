package com.armo.client.home;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.armo.client.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    final List<BaseFragment> fragments = new ArrayList<>();
    final Context context;

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }


    public void setFragments(List<BaseFragment> fragments) {
        this.fragments.clear();
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(getItem(position).getTitle());
    }
}

package com.armo.client.home;

import android.graphics.Color;
import android.os.Bundle;

import com.armo.client.base.BaseActivity;
import com.armo.client.base.BaseFragment;
import com.armo.client.entertainment.EntertainmentFragment;
import com.armo.client.security.SecurityFragment;
import com.armo.client.teaching.TeachingFragment;
import com.armorobot.client.R;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.homeactivity_viewpager)
    AHBottomNavigationViewPager bottomNavigationViewPager;
    @BindView(R.id.homeactivity_bottomnavigation)
    AHBottomNavigation bottomNavigation;

    HomePagerAdapter homePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupView();
    }

    void setupView() {
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext());
        bottomNavigationViewPager.setAdapter(homePagerAdapter);

        List<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
        List<BaseFragment> fragments = new ArrayList<>();

        TeachingFragment teachingFragment = TeachingFragment.newInstance();
        fragments.add(teachingFragment);
        AHBottomNavigationItem teachingNavigationItem = new AHBottomNavigationItem(teachingFragment.getTitle(), R.drawable.teaching, 0);
        bottomNavigationItems.add(teachingNavigationItem);

        EntertainmentFragment entertainmentFragment = EntertainmentFragment.newInstance();
        fragments.add(entertainmentFragment);
        AHBottomNavigationItem entertainmentNavigationItem = new AHBottomNavigationItem(entertainmentFragment.getTitle(), R.drawable.entertainment, 0);
        bottomNavigationItems.add(entertainmentNavigationItem);

        SecurityFragment securityFragment = SecurityFragment.newInstance();
        fragments.add(securityFragment);
        AHBottomNavigationItem securityNavigationItem = new AHBottomNavigationItem(securityFragment.getTitle(), R.drawable.security, 0);
        bottomNavigationItems.add(securityNavigationItem);


        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {

            if (wasSelected) {
                return false;
            }

            setTitle(homePagerAdapter.getPageTitle(position));
            bottomNavigationViewPager.setCurrentItem(position, true);
            return true;
        });



        bottomNavigation.setDefaultBackgroundColor(Color.rgb(99, 4, 96));
        bottomNavigation.setAccentColor(Color.WHITE);
        bottomNavigation.setInactiveColor(Color.LTGRAY);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        homePagerAdapter.setFragments(fragments);
        bottomNavigation.addItems(bottomNavigationItems);
        int initPosition = 0;
        bottomNavigation.setCurrentItem(initPosition);
        setTitle(homePagerAdapter.getPageTitle(initPosition));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomNavigation.removeOnTabSelectedListener();
    }
}

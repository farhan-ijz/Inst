package com.wurfel.yaqeen.inst.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wurfel.yaqeen.inst.Adapter.BottomNavViewPagerAdapter;
import com.wurfel.yaqeen.inst.Fragments.HomeFragment;
import com.wurfel.yaqeen.inst.Fragments.ProfileFragment;
import com.wurfel.yaqeen.inst.Fragments.SectionsFragment;
import com.wurfel.yaqeen.inst.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    // UI
    private AHBottomNavigationViewPager viewPager;
    AHBottomNavigation bottomNavigation;
    private int[] tabColors;
    private AHBottomNavigationAdapter navigationAdapter;
    private BottomNavViewPagerAdapter adapter;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        createNavBar();

        initUI();
    }

    void createNavBar(){

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorWhite));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // for textColor
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.colorGrey));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorSecondary));

        // elevation
        bottomNavigation.setUseElevation(true);

    }

    /**
     * Init UI
     */
    private void initUI() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);

        tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_nav_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);


        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                if (wasSelected) {
                    return true;
                }

                if (currentFragment != null) {
                    if(currentFragment instanceof HomeFragment){
                        HomeFragment homeFragment = (HomeFragment) currentFragment;
                        homeFragment.willBeHidden();
                    }
                    else if(currentFragment instanceof SectionsFragment){
                        SectionsFragment homeFragment = (SectionsFragment) currentFragment;
                        homeFragment.willBeHidden();
                    }else if(currentFragment instanceof ProfileFragment){
                        ProfileFragment homeFragment = (ProfileFragment) currentFragment;
                        homeFragment.willBeHidden();
                    }
                }

                viewPager.setCurrentItem(position, false);

                currentFragment = adapter.getCurrentFragment();
                if(currentFragment instanceof HomeFragment){
                    HomeFragment homeFragment = (HomeFragment) currentFragment;
                    homeFragment.willBeDisplayed();
                }
                else if(currentFragment instanceof SectionsFragment){
                    SectionsFragment homeFragment = (SectionsFragment) currentFragment;
                    homeFragment.willBeDisplayed();
                }else if(currentFragment instanceof ProfileFragment){
                    ProfileFragment homeFragment = (ProfileFragment) currentFragment;
                    homeFragment.willBeDisplayed();
                }


                if (currentFragment == null) {
                    return true;
                }


                return true;
            }
        });

        viewPager.setOffscreenPageLimit(4);
        adapter = new BottomNavViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();

    }




}

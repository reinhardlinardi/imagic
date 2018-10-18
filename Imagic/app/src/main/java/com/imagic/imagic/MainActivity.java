package com.imagic.imagic;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /* Constants */
    private static final int NUM_OF_MENU = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set title bar
        Toolbar titleBar = findViewById(R.id.titleBar);
        setSupportActionBar(titleBar);

        ViewPager viewPager = findViewById(R.id.viewContainer);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        MenuAdapter menuAdapter = new MenuAdapter(getSupportFragmentManager());
        viewPager.setAdapter(menuAdapter);

        // When tab changes, change view as well and vice versa
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    // Adapter for menu
    public class MenuAdapter extends FragmentStatePagerAdapter {

        // Constructor
        MenuAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Get n-th fragment
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0 : return HistogramFragment.newInstance("a", "b");
                case 1 : return ContrastEnhancementFragment.newInstance("a", "b");
                case 2 : return EqualizerFragment.newInstance("a", "b");
                case 3 : return OCRFragment.newInstance("a", "b");
            }

            return null;
        }

        // Return number of menu
        @Override
        public int getCount() { return NUM_OF_MENU; }
    }
}

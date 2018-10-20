package com.imagic.imagic;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    /* Properties */

    // ArrayList of menu
    private static ArrayList<Menu> menu;

    /* Lifecycles */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar = complementary bar for action bar
        // We set this toolbar as support action bar to display app title
        Toolbar titleBar = findViewById(R.id.titleBar);
        setSupportActionBar(titleBar);

        // View Pager = a component to enable swipe to change to another tab instead of clicking the desired tab
        // TabLayout = parent component of a group of tabs, handle everything related to tabs
        ViewPager viewPager = findViewById(R.id.viewContainer);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        try {
            // Read all menu from JSON file and put into ArrayList
            String menuJSON = TextFile.readRawResourceFile(this, R.raw.menu);
            menu = JSONSerializer.arrayListDeserialize(this, menuJSON, Menu.class);

            // We have to create an adapter that extends FragmentStatePagerAdapter and set it as View Pager's adapter
            // FragmentStatePagerAdapter is an adapter to manage fragments in an efficient way, good for heavy fragments and prevent fragments stay in the memory all at once
            MenuAdapter menuAdapter = new MenuAdapter(getSupportFragmentManager());
            viewPager.setAdapter(menuAdapter);

            // Associate view pager swipe with the correct tab selection and vice versa
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        }
        catch(Exception e) {
            Debug.ex(e);
        }
    }

    /* Adapters */

    // Adapter for tabs (menu)
    public class MenuAdapter extends FragmentStatePagerAdapter {

        // Constructor
        MenuAdapter(FragmentManager fragmentManager) { super(fragmentManager); }

        // Return the desired fragment for corresponding tabs given its position from the left
        @Override
        public Fragment getItem(int position) {
            // Get full class path by concatenating package name with class name
            final String packageName = MainActivity.this.getApplicationContext().getPackageName();
            final String packageDelimiter = ".";
            final String fragmentClassSuffix = "Fragment";

            final String fragmentClassName = menu.get(position).fragmentClassName;
            final String fragmentClassPath = packageName + packageDelimiter + fragmentClassName + fragmentClassSuffix;

            try {
                // Return new fragment instance based on class path
                return (Fragment)(Class.forName(fragmentClassPath)).newInstance();
            }
            catch(Exception e) {
                Debug.ex(e);
            }

            return null;
        }

        // Return the number of tabs
        @Override
        public int getCount() { return menu.size(); }
    }
}

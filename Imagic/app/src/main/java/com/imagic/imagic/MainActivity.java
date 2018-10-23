package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FragmentListener, ImageDialogListener {

    /* Properties */

    // ArrayList of menu
    private static ArrayList<Menu> menu;

    // Original image URI, image, and histograms
    Uri uri;
    Image image;
    RGBHistogram rgb;
    GrayscaleHistogram grayscale;

    // ViewPager adapter and current fragment instance
    MenuAdapter menuAdapter;
    MainActivityListener currentFragment;

    // Flag for page swipe
    boolean pageSwiped;

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
            menu = JSONSerializer.arrayListDeserialize(menuJSON, Menu.class);

            // We have to create an adapter that extends FragmentStatePagerAdapter and set it as View Pager's adapter
            // FragmentStatePagerAdapter is an adapter to manage fragments in an efficient way, good for heavy fragments and prevent fragments stay in the memory all at once
            menuAdapter = new MenuAdapter(getSupportFragmentManager());
            viewPager.setAdapter(menuAdapter);

            // Associate view pager swipe with the correct tab selection and vice versa
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

            // Add another view pager listener to update current fragment instance when a page is selected
            viewPager.addOnPageChangeListener(getViewPagerOnPageChangeListener());

            // Set image URI to null, no image loaded at first
            uri = null;

            // Set page swiped to false, no page swiped at the beginning
            pageSwiped = false;

            // Initialize image and histograms
            image = new Image();
            rgb = new RGBHistogram();
            grayscale = new GrayscaleHistogram();
        }
        catch(Exception e) {
            Debug.ex(e);
        }
    }

    /* Adapters */

    // Adapter for tabs (menu)
    public class MenuAdapter extends FragmentStatePagerAdapter {

        /* Properties */

        // List of all fragments inside ViewPager
        SparseArray<Fragment> fragments;

        // Constructor
        MenuAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new SparseArray<>();
        }

        // When a fragment is created, add that fragment to list
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments.put(position, fragment);

            // Initialize current fragment with first fragment at the beginning when user has not swiped any page
            if(!pageSwiped) {
                currentFragment = (MainActivityListener) fragment;
                pageSwiped = true;
            }

            return fragment;
        }

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

        // When a fragment is destroyed, remove fragment from list
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragments.remove(position);
            super.destroyItem(container, position, object);
        }

        // Return the number of tabs
        @Override
        public int getCount() { return menu.size(); }
    }

    /* Event listeners */

    // Get view pager on page change listener
    private ViewPager.OnPageChangeListener getViewPagerOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            // When a page is selected, get current fragment by its position and update the current fragment instance
            @Override
            public void onPageSelected(int position) { currentFragment = (MainActivityListener) menuAdapter.fragments.get(position); }

            @Override
            public void onPageScrollStateChanged(int state) {}
        };
    }

    /* Implemented interfaces methods */

    // Send intent to select image
    @Override
    public void sendSelectImageIntent() {
        // Create intent to get content
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(Image.MIME_TYPE);

        currentFragment.sendSelectImageIntent(intent);
    }

    // Send intent to capture image
    @Override
    public void sendCaptureImageIntent() {
        // Create intent to capture image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null) {
            // Create temporary file to save captured image data
            final String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String extensionSuffix = ".jpg";

            // Get full file provider path by concatenating package name and provider name
            final String packageName = getApplicationContext().getPackageName();
            final String packageDelimiter = ".";
            final String providerName = "provider";

            final String providerPath = packageName + packageDelimiter + providerName;

            try {
                File file = File.createTempFile(filename, extensionSuffix, getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                uri = FileProvider.getUriForFile(this, providerPath, file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                currentFragment.sendCaptureImageIntent(intent);
            }
            catch(Exception e) {
                Debug.ex(e);
            }
        }
    }

    // Update URI on image intent result
    public void onImageIntentResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == IntentRequestCode.SELECT_IMAGE.code) uri = data.getData();
        }
    }

    // Check if image has no bitmap yet
    public boolean isImageHasBitmap() { return image.hasBitmap(); }

    // Check if RGB histogram data is available
    public boolean isRGBHistogramDataAvailable() { return rgb.allHasData(); }

    // Check if grayscale histogram data
    public boolean isGrayscaleHistogramDataAvailable() { return !grayscale.isEmpty(); }

    // Get image URI
    public Uri getImageURI() { return uri; }

    // Get image bitmap
    public Bitmap getImageBitmap() { return image.bitmap; }

    // Load image bitmap and scale it down to match image view dimension if necessary
    public void loadImageBitmap(int viewWidth, int viewHeight) throws Exception { image = new Image(this, uri, viewWidth, viewHeight); }

    // Get histogram bar graph series data
    public BarGraphSeries<DataPoint> getHistogramBarGraphSeriesData(ColorType colorType) {
        switch(colorType) {
            case RED : return rgb.red.getBarGraphSeries();
            case GREEN : return rgb.green.getBarGraphSeries();
            case BLUE : return rgb.blue.getBarGraphSeries();
            case GRAYSCALE : return grayscale.getBarGraphSeries();
            default : return null;
        }
    }

    // Reset all histogram data
    public void resetAllHistogramData() {
        rgb.resetData();
        grayscale.resetData();
    }

    // Update histogram data
    public void updateHistogramData(ColorType colorType) {
        int[] dataArray = image.generateHistogramDataByColorType(colorType);

        switch(colorType) {
            case RED : rgb.red.setData(dataArray); break;
            case GREEN : rgb.green.setData(dataArray); break;
            case BLUE : rgb.blue. setData(dataArray); break;
            case GRAYSCALE : grayscale.setData(dataArray); break;
            default : break;
        }
    }
}

package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FragmentListener, ImageDialogListener {

    /* Properties */

    // ArrayList of menu
    private static ArrayList<Menu> menu;

    // Original image URI, image view, and fragment context
    Uri uri;
    Context context;
    ImageView view;

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

            // Set image URI, image view, and fragment context to null, no image loaded at first
            uri = null;
            context = null;
            view = null;
        }
        catch(Exception e) {
            Debug.ex(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
        view = null;
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



    /* Implemented interfaces methods */

    // Register original image view to MainActivity
    @Override
    public void registerOriginalImageView(Context context, ImageView view) {
        this.context = context;
        this.view = view;
    }

    // Reset given image view to original image
    @Override
    public void resetImage(Context context) {
        // If user had loaded image before, reset image
        if(uri != null) {
            UI.setImageView(context, view, uri);
            UI.clearMemory(context);
        }
    }

    // Send intent to select image
    @Override
    public void sendSelectImageIntent() {
        // Create intent to get content
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(Image.MIME_TYPE);
        startActivityForResult(intent, IntentRequestCode.SELECT_IMAGE.code);
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

            final String packageName = getApplicationContext().getPackageName();
            final String packageDelimiter = ".";
            final String providerName = "provider";

            final String providerPath = packageName + packageDelimiter + providerName;

            try {
                File file = File.createTempFile(filename, extensionSuffix, getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                uri = FileProvider.getUriForFile(this, providerPath, file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, IntentRequestCode.CAPTURE_IMAGE.code);
            }
            catch(Exception e) {
                Debug.ex(e);
            }
        }
    }

    // Get image URI
    public Uri getImageURI() { return uri; }

    /* Intent result */

    // Change image based on select or capture image activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == IntentRequestCode.SELECT_IMAGE.code) uri = data.getData();
            UI.setImageView(context, view, uri);
            UI.clearMemory(context);
        }
    }
}

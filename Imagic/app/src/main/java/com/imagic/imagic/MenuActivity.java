package com.imagic.imagic;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // Menu Adapter
    private class MenuAdapter extends ArrayAdapter<MenuOption> {

        MenuAdapter(ArrayList<MenuOption> options) {
            super(MenuActivity.this, R.layout.list_menu_option, options);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = MenuActivity.this.getLayoutInflater();
            View optionView = inflater.inflate(R.layout.list_menu_option, null, true);

            TextView optionTitleTextView = optionView.findViewById(R.id.menuOptionTitleTextView);
            TextView optionDescriptionTextView = optionView.findViewById(R.id.menuOptionDescriptionTextView);
            MenuOption option = getItem(position);

            if(option != null) {
                optionTitleTextView.setText(option.title);
                optionDescriptionTextView.setText(option.description);
                optionView.setOnClickListener(getOptionOnClickListener(option.activityOnClick));
            }

            return optionView;
        }

        // Option on click listener
        private View.OnClickListener getOptionOnClickListener(final String activityOnClick) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(MenuActivity.this, Class.forName(MenuActivity.this.getApplicationContext().getPackageName() + "." + activityOnClick + "Activity"));
                        intent.putExtra(Cache.INTENT_BUNDLE_NAME, cachedImageDataURI.toString());
                        MenuActivity.this.startActivity(intent);
                    }
                    catch(Exception e) {
                        Log.e("Imagic", "Exception", e);
                    }
                }
            };
        }
    }

    // Cached image data URI
    private static Uri cachedImageDataURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        try{
            ArrayList<MenuOption> options = JSONSerializer.arrayDeserialize(getApplicationContext(), Text.readRawResource(getApplicationContext(), R.raw.menu), MenuOption.class);
            MenuAdapter menuAdapter = new MenuAdapter(options);
            ListView menuListView = findViewById(R.id.menuListView);
            menuListView.setAdapter(menuAdapter);
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }
}
